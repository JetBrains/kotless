package io.kotless.gen.factory.aws.route

import io.kotless.InternalAPI
import io.kotless.URIPath
import io.kotless.gen.GenerationContext
import io.kotless.gen.factory.aws.apigateway.RestAPIFactory
import io.kotless.terraform.functions.link
import io.kotless.utils.Storage
import io.terraformkt.aws.resource.apigateway.api_gateway_resource
import io.terraformkt.hcl.ref

/**
 * Generic implementation of ApiGateway resources creation
 *
 * Will create all intermediate resources
 */
@OptIn(InternalAPI::class)
open class AbstractRouteFactory {
    data class ResourceDescriptor(val ref: String, val id: String)

    companion object {
        private val resource = Storage.Key<HashMap<URIPath, ResourceDescriptor>>()
    }

    fun getResource(resourcePath: URIPath, api: RestAPIFactory.Output, context: GenerationContext): ResourceDescriptor {
        val resources = context.storage.getOrPut(resource) { HashMap() }

        if (URIPath() !in resources) {
            resources[URIPath()] = ResourceDescriptor(api.ref, api.root_resource_id)
        }

        var path = URIPath()
        for (part in resourcePath.parts) {
            val prev = path
            path = URIPath(path, part)

            if (path !in resources) {
                val resource = api_gateway_resource(context.names.tf(path.parts)) {
                    depends_on = arrayOf(link(resources[prev]!!.ref))

                    rest_api_id = api.rest_api_id
                    parent_id = resources[prev]!!.id
                    path_part = part
                }
                context.entities.register(resource)
                resources[path] = ResourceDescriptor(resource.hcl_ref, resource::id.ref)
            }
        }

        return resources[path]!!
    }
}
