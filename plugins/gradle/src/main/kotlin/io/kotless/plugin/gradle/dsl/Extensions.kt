package io.kotless.plugin.gradle.dsl

import java.io.File
import java.io.Serializable

@KotlessDSLTag
class Extensions : Serializable {
    internal val terraform = Terraform()

    @KotlessDSLTag
    fun terraform(configure: Terraform.() -> Unit) {
        terraform.configure()
    }

    @KotlessDSLTag
    class Terraform: Serializable {
        var allowDestroy = false

        internal val files = Files()

        @KotlessDSLTag
        fun files(configure: Files.() -> Unit) {
            files.configure()
        }

        @KotlessDSLTag
        class Files: Serializable {
            internal val additional = HashSet<File>()

            fun add(file: File) {
                additional.add(file)
            }
        }
    }
}
