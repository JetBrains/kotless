package io.kotless.parser.utils.psi.analysis


import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil.DEFAULT_MODULE_NAME
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File

/**
 * EnvironmentManager objects wraps complex logic of creating KotlinCoreEnvironment
 * with custom classpath.
 *
 * Note: under classpath assumed set of libraries in context of which Kotlin code
 * should be interpreted. It is NOT the classpath of compiler itself.
 *
 * Resulting KotlinCoreEnvironment is used to parse Kotlin code into Kotlin AST.
 */
object EnvironmentManager {
    /** Create KotlinCoreEnvironment with specified classpath */
    fun create(libraries: Set<File>): KotlinCoreEnvironment {
        setIdeaIoUseFallback()

        val configuration = CompilerConfiguration().apply {
            addJvmClasspathRoots(PathUtil.getJdkClassesRootsFromCurrentJre() + libraries)
            put(CommonConfigurationKeys.MODULE_NAME, DEFAULT_MODULE_NAME)
            put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        }

        return KotlinCoreEnvironment.createForProduction(Disposable { }, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)
    }
}
