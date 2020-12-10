package io.kotless.gen.factory.infra

import io.kotless.KotlessConfig
import io.kotless.gen.GenerationContext
import io.kotless.gen.GenerationFactory
import io.kotless.hcl.HCLEntity
import io.kotless.hcl.HCLTextField
import io.kotless.terraform.infra.TFLocals
import io.kotless.terraform.infra.locals

object TFLocalsFactory : GenerationFactory<KotlessConfig.Terraform, Unit> {

    override fun mayRun(entity: KotlessConfig.Terraform, context: GenerationContext) = true

    override fun generate(entity: KotlessConfig.Terraform, context: GenerationContext): GenerationFactory.GenerationResult<Unit> {
        val locals = locals {
            variables = object : HCLEntity() {
                init {
                    for ((key, value) in entity.locals) {
                        fields.add(HCLTextField(key, false, this, value))
                    }
                }
            }
        }
        return GenerationFactory.GenerationResult(Unit, locals)
    }
}
