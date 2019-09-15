package io.kotless.engine

import io.kotless.*
import io.kotless.Webapp.ApiGateway.DynamicRoute
import io.kotless.Webapp.ApiGateway.StaticRoute
import io.kotless.engine.optimization.LambdaMergeOptimization
import io.kotless.engine.template.LambdaWarmer
import io.kotless.engine.terraform.TfEntity
import io.kotless.engine.terraform.aws.acm.TfAcmCertificateData
import io.kotless.engine.terraform.aws.apigateway.*
import io.kotless.engine.terraform.aws.data.AwsInformation
import io.kotless.engine.terraform.aws.iam.*
import io.kotless.engine.terraform.aws.infra.TfAwsProvider
import io.kotless.engine.terraform.aws.infra.TfConfig
import io.kotless.engine.terraform.aws.lambda.TfLambda
import io.kotless.engine.terraform.aws.route53.TfRoute53Record
import io.kotless.engine.terraform.aws.route53.TfRoute53ZoneData
import io.kotless.engine.terraform.aws.s3.TfS3BucketData
import io.kotless.engine.terraform.aws.s3.TfS3Object
import io.kotless.engine.terraform.synthesizer.TfSynthesizer.TfFile
import io.kotless.engine.terraform.utils.*
import io.kotless.gen.Generator
import java.io.File

/**
 * KotlessEngine is implementation of terraform files
 * generation from Kotless schema.
 *
 * It uses Kotless terraform-dsl to generate human readable
 * and understandable terraform code.
 */
@Suppress("TooManyFunctions")
class KotlessEngine(private val schema: Schema) {

    fun generateTerraform(resourcePrefix: String) = Generator.generate(schema).map { tfFile ->
        schema.kotlessConfig.genDirectory.mkdir()
        val file = File(schema.kotlessConfig.genDirectory, tfFile.nameWithExt)
        tfFile.write(file)
        file
    }


//    fun generateTerraform(resourcePrefix: String): List<File> = try {
//        TfEntity.resourcePrefix = resourcePrefix
//
//        val kotlessBucketData = TfS3BucketData("kotless_bucket".toTfName(), schema.kotlessConfig.bucket)
//        val role = generateStaticRole("kotless".toTfName("s3", "statics", "access"), kotlessBucketData)
//        val lambdas = generateLambdas(kotlessBucketData, schema.lambdas)
//        val statics = generateStatics(kotlessBucketData, schema.statics)
//        val apis = generateWebapps(schema.webapps, role, lambdas, statics)
//        val tfFiles = TfSynthesizer.synthesize() + generateInfra(schema.kotlessConfig.terraform)
//        writeDown(tfFiles)
//    } finally {
//        cleanup()
//    }

    private fun cleanup() {
        LambdaMergeOptimization.cleanup()
        AwsInformation.cleanup()
        TfEntity.cleanup()
    }

    private fun writeDown(tfFiles: List<TfFile>): List<File> {
        schema.kotlessConfig.genDirectory.mkdirs()
        return tfFiles.map { tfFile -> File(schema.kotlessConfig.genDirectory, tfFile.name).also { it.writeText(tfFile.content) } }
    }

    private fun generateInfra(terraform: KotlessConfig.Terraform): TfFile {
        return TfFile("infra.tf", TfConfig(terraform.version, terraform.backend).render() + "\n\n" +
            TfAwsProvider(terraform.aws).render())
    }

    private fun generateLambdas(kotlessBucketData: TfS3BucketData, lambdas: Set<Lambda>): Map<Lambda, TfLambda> {

        val mergedLambdas = LambdaMergeOptimization.merge(LinkedHashSet(lambdas), schema.kotlessConfig.optimization.mergeLambda)

        val result = HashMap<Lambda, TfLambda>()

        for (lambda in mergedLambdas) {
            with(lambda.merged) {
                val role = TfRole(name.toTfName("role"), name.toAwsName("role"),
                    TfRoleAssumePolicy.LambdaAssumePolicy.toPolicyJson())
                val policyDocument = generatePermissions(name.toTfName("policy", "document"), permissions)
                TfRolePolicy(name.toTfName("policy"), role, name.toAwsName("policy"), policyDocument)


                val s3Object = TfS3Object(name.toTfName("object"), { tf(kotlessBucketData.name) }, "kotless-lambdas/$name.jar", file)

                val tfLambda = TfLambda(name.toTfName("lambda"), name.toAwsName("lambda"),
                    TfLambda.LambdaRuntime(entrypoint.qualifiedName,
                        config.timeoutSec, config.memoryMb, mapOf("KOTLESS_PACKAGES" to config.packages.joinToString())),
                    s3Object, role).apply {
                    comment = lambda.comment
                }
                lambda.from.forEach {
                    result[it] = tfLambda
                }
            }
        }

        result.filter { it.key.config.autowarm }.forEach {
            LambdaWarmer(it.value, it.key.config.autowarmMinutes)
        }

        return result
    }

    private fun generateStaticRole(tfName: String, kotlessBucketData: TfS3BucketData): TfRole {
        val tfRole = TfRole(tfName, tfName.toAwsName("role"), TfRoleAssumePolicy.ApiGatewayAssumePolicy.toPolicyJson())
        val tfPolicy = TfPolicyDocument(tfName.toTfName("policy", "document"), setOf(TfPolicyDocument.Statement("Allow",
            actions = setOf("s3:GetObject"),
            resources = setOf("\${${kotlessBucketData.arn}}/*"))))
        val tfRolePolicy = TfRolePolicy(tfName.toTfName("policy"), tfRole, document = tfPolicy)
        return tfRole
    }

    private fun generateStatics(kotlessBucketData: TfS3BucketData, statics: Set<StaticResource>): Map<StaticResource, TfS3Object> {
        return statics.map {
            it to TfS3Object(it.path.parts.joinToString(separator = "_").toTfName(), { tf(kotlessBucketData.name) }, it.path.toString(), it.content, it.mime)
        }.toMap()
    }

    private fun generateWebapps(webapps: Set<Webapp>, staticsRole: TfRole,
                                lambdas: Map<Lambda, TfLambda>,
                                statics: Map<StaticResource, TfS3Object>): Map<Webapp.ApiGateway, TfRestApi> {
        return webapps.map {
            val api = generateApiGateway(it.api, staticsRole, lambdas, statics)
            val deployment = generateDeployment(it.api.deployment, api.second)
            it.route53?.let {
                generateRoute53(it, deployment, api)
            }
            api
        }.toMap()
    }

    private fun generateRoute53(route53: Webapp.Route53, deployment: TfRestApiDeployment, api: Pair<Webapp.ApiGateway, TfRestApi>): TfRoute53Record {
        val apiGatewayDomain = TfRestApiDomainName(route53.alias.toTfName("api", "alias"),
            route53.alias + "." + route53.zone,
            TfAcmCertificateData(route53.certificate.toTfName("acm"), route53.certificate))
        TfRestApiBasePathMapping(route53.alias.toTfName("api", "mapping"), api.second, apiGatewayDomain, deployment)

        val zoneData = TfRoute53ZoneData(api.first.name.toTfName("route53", "alias", "zone"), route53.zone)
        return TfRoute53Record(
            api.first.name.toTfName("route53", "alias"),
            route53.alias, zoneData,
            type = "A",
            aliases = listOf(TfRoute53Record.AliasRecord({ tf(apiGatewayDomain.cloudfront_domain_name) },
                { tf(apiGatewayDomain.cloudfront_zone_id) }, false)))
    }

    private fun generateDeployment(deployment: Webapp.ApiGateway.Deployment, tfApi: TfRestApi): TfRestApiDeployment {
        return TfRestApiDeployment(
            deployment.name.toTfName(), tfApi,
            deployment.name.toAwsName(deployment.version),
            TfEntity.instantiatedEntities.mapNotNull { it.value as? TfRestApiLambdaIntegration },
            TfEntity.instantiatedEntities.mapNotNull { it.value as? TfRestApiS3Integration })
    }

    @Suppress("ComplexMethod")
    private fun generateApiGateway(api: Webapp.ApiGateway, staticsRole: TfRole,
                                   lambdas: Map<Lambda, TfLambda>,
                                   statics: Map<StaticResource, TfS3Object>): Pair<Webapp.ApiGateway, TfRestApi> {
        val restApi = TfRestApi(api.name.toTfName("rest", "api"), api.name.toAwsName())

        val routesByPath = api.dynamics.map { it.path to it } + api.statics.map { it.path to it }
        fun createGroupingByResource(previousResource: TfRestApiResource?, previousPath: URIPath,
                                     currentPathPart: String, routes: List<Webapp.ApiGateway.Route>) {
            val tfNameByPath = (previousPath.parts + currentPathPart).toTfName()
            val resource = TfRestApiResource(api.name.toTfName("rest", "resource", tfNameByPath),
                restApi, path = currentPathPart, parentResource = previousResource)

            val onCurrentPath = routes.filter { it.path == URIPath(previousPath, currentPathPart) }
            for (route in onCurrentPath) {
                val method = TfRestApiMethod(api.name.toTfName("rest", "method", tfNameByPath, route.method.name.toLowerCase()),
                    restApi, { tf(resource.id) }, route.method)
                when (route) {
                    is DynamicRoute -> {
                        TfRestApiLambdaIntegration(api.name.toTfName("rest", "integration", tfNameByPath, route.method.name.toLowerCase()),
                            restApi, { tf(resource.id) }, { tfRaw(resource.path) }, method, lambdas[route.lambda]!!)
                    }
                    is StaticRoute -> {
                        TfRestApiS3Integration(api.name.toTfName("rest", "integration", tfNameByPath, route.method.name.toLowerCase()),
                            restApi, { tf(resource.id) }, method, staticsRole, statics[route.resource]!!)
                    }
                }

            }
            (routes - onCurrentPath).groupBy { it.path.parts.drop(previousPath.parts.size + 1).first() }.forEach {
                createGroupingByResource(resource, URIPath(previousPath, currentPathPart), it.key, it.value)
            }
        }

        fun createRoot(api: Webapp.ApiGateway, tfApi: TfRestApi, dynamic: DynamicRoute?, static: StaticRoute?) {
            if (dynamic == null && static == null) {
                return
            }
            val method = TfRestApiMethod(api.name.toTfName("rest", "method", "root".toTfName(), (dynamic ?: static!!).method.name.toLowerCase()),
                restApi, { tf(tfApi.root_resource_id) }, (dynamic ?: static!!).method)
            if (dynamic != null) {
                TfRestApiLambdaIntegration(api.name.toTfName("rest", "integration", "root".toTfName(), dynamic.method.name.toLowerCase()),
                    restApi, { tf(tfApi.root_resource_id) }, { "/" }, method, lambdas[dynamic.lambda]!!)
            } else if (static != null) {
                TfRestApiS3Integration(api.name.toTfName("rest", "integration", "root".toTfName(), static.method.name.toLowerCase()),
                    restApi, { tf(tfApi.root_resource_id) }, method, staticsRole, statics[static.resource]!!)
            }
        }

        createRoot(api, restApi, routesByPath.firstOrNull { it.first.parts.isEmpty() && it.second is DynamicRoute }?.second as DynamicRoute?,
            routesByPath.firstOrNull { it.first.parts.isEmpty() && it.second is StaticRoute }?.second as StaticRoute?)

        routesByPath.filter { it.first.parts.isNotEmpty() }.groupBy { it.first.parts.first() }.forEach {
            createGroupingByResource(null, URIPath(), it.key, it.value.map { it.second })
        }
        return api to restApi
    }

    private fun generatePermissions(tfName: String, permissions: Set<Permission>): TfPolicyDocument {
        return TfPolicyDocument(tfName, permissions.map { permission ->
            TfPolicyDocument.Statement("Allow",
                actions = permission.actions.map { permission.resource.prefix + ":$it" }.toSet(),
                resources = permission.awsIds)
        }.toSet())
    }
}
