package io.kotless.parser.utils.psi.analysis


import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.load.java.JvmAbi
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
internal object EnvironmentManager {
    /** Create KotlinCoreEnvironment with specified classpath */
    fun createEnvironment(libraries: Set<File>): KotlinCoreEnvironment {
        val arguments = K2JVMCompilerArguments()
        val configuration = CompilerConfiguration()

        configuration.addJvmClasspathRoots(PathUtil.getJdkClassesRootsFromCurrentJre() + libraries)

        configuration.put(JVMConfigurationKeys.DISABLE_PARAM_ASSERTIONS, arguments.noParamAssertions)
        configuration.put(JVMConfigurationKeys.DISABLE_CALL_ASSERTIONS, arguments.noCallAssertions)

        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        configuration.put(CommonConfigurationKeys.MODULE_NAME, JvmAbi.DEFAULT_MODULE_NAME)

        configuration.languageVersionSettings = arguments.toLanguageVersionSettings(configuration[CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY]!!)

        return KotlinCoreEnvironment.createForProduction(Disposable { }, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)
    }
}
