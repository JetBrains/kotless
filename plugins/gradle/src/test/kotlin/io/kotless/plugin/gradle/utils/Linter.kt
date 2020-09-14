package io.kotless.plugin.gradle.utils

import io.kotless.plugin.gradle.utils.CommandLine.os
import org.codehaus.plexus.util.Os
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL


object Linter {
    private var defaultConfig: File? = null

    private const val version = "0.18.0"

    private val logger = LoggerFactory.getLogger(Linter::class.java)

    fun download(tflint: File) {
        if (tflint.exists()) return

        Downloads.download(URL("https://github.com/wata727/tflint/releases/download/v$version/tflint_$os.zip"), tflint.parentFile, Archive.ZIP)


        if (Os.isFamily(Os.FAMILY_MAC) || Os.isFamily(Os.FAMILY_UNIX)) {
            CommandLine.execute("chmod", listOf("+x", tflint.absolutePath), tflint.parentFile, false)
        }
    }

    fun lint(tflint: File, workingDir: File): Int {
        val config = configureTFLint(workingDir).absolutePath

        return try {
            CommandLine.execute(tflint.canonicalPath, listOf("-c=$config"), workingDir, true)
        } finally {
            defaultConfig?.delete()
        }
    }

    private fun configureTFLint(workingDir: File): File {
        val commonConfig = Resources.read("/tflint.hcl")
        defaultConfig = File(workingDir, "tflint.hcl").apply {
            writeText(commonConfig)
        }

        return defaultConfig!!
    }
}
