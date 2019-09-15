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
            else -> error("Unknown operating system. Probably your system is not supported.")
        }
    }

    fun execute(exec: String, args: List<String>, workingDir: File, redirectStdout: Boolean = false, redirectErr: Boolean = true): Int {
        return CommandLineUtils.executeCommandLine(
            Commandline().apply {
                workingDirectory = workingDir
                executable = exec
                addArguments(args.toTypedArray())
            }, getConsumer(redirectStdout), getConsumer(redirectErr)
        )
    }

    private fun getConsumer(redirectOutput: Boolean): StreamConsumer {
        return if (redirectOutput)
            DefaultConsumer()
        else
            CommandLineUtils.StringStreamConsumer()
    }
}
