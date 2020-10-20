package io.kotless.plugin.gradle.utils

import org.codehaus.plexus.util.Os
import org.codehaus.plexus.util.cli.*
import java.io.File

internal object CommandLine {
    /** Full name of current system (assumed it is amd64) */
    val os by lazy {
        when {
            Os.isFamily(Os.FAMILY_WINDOWS) -> "windows_amd64"
            Os.isFamily(Os.FAMILY_MAC) -> "darwin_amd64"
            Os.isFamily(Os.FAMILY_UNIX) -> "linux_amd64"
            else -> error("Unknown operating system. Probably your system is not supported by Terraform.")
        }
    }

    fun execute(exec: String, args: List<String>, workingDir: File, redirectStdout: Boolean, redirectErr: Boolean = true): Int {
        return execute(exec, args, emptyMap(), workingDir, redirectStdout, redirectErr)
    }

    fun execute(exec: String, args: List<String>, envs: Map<String, String>, workingDir: File, redirectStdout: Boolean, redirectErr: Boolean): Int {
        return CommandLineUtils.executeCommandLine(
            Commandline().apply {
                workingDirectory = workingDir
                executable = exec
                for ((key, value) in envs) {
                    addEnvironment(key, value)
                }
                addArguments(args.toTypedArray())
            }, getConsumer(redirectStdout), getConsumer(redirectErr)
        )
    }

    fun executeOrFail(exec: String, args: List<String>, workingDir: File, redirectStdout: Boolean = false, redirectErr: Boolean = true) {
        return executeOrFail(exec, args, emptyMap(), workingDir, redirectStdout, redirectErr)
    }

    fun executeOrFail(
        exec: String,
        args: List<String>,
        envs: Map<String, String>,
        workingDir: File,
        redirectStdout: Boolean = false,
        redirectErr: Boolean = true
    ) {
        val returnCode = execute(exec, args, envs, workingDir, redirectStdout, redirectErr)
        if (returnCode != 0) {
            error("Command failed: '$exec ${args.joinToString { " " }}'")
        }
    }

    private fun getConsumer(redirectOutput: Boolean): StreamConsumer = if (redirectOutput) {
        DefaultConsumer()
    } else {
        CommandLineUtils.StringStreamConsumer()
    }
}
