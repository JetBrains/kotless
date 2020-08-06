package io.kotless.plugin.gradle.utils

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver
import org.codehaus.plexus.archiver.zip.ZipUnArchiver
import org.codehaus.plexus.logging.console.ConsoleLogger
import java.io.File

internal enum class Archive(val extension: String) {
    ZIP("zip") {
        override fun unarchive(from: File, to: File) {
            to.mkdirs()
            ZipUnArchiver(from).apply {
                enableLogging(ConsoleLogger(plexusErrorLevel, "Archiver"))
                sourceFile = from
                destDirectory = to
            }.extract()
        }
    },
    TARGZ("tar.gz") {
        override fun unarchive(from: File, to: File) {
            to.mkdirs()
            TarGZipUnArchiver(from).apply {
                enableLogging(ConsoleLogger(plexusErrorLevel, "Archiver"))
                sourceFile = from
                destDirectory = to
            }.extract()
        }
    };

    companion object {
        private const val plexusErrorLevel: Int = 5
    }

    abstract fun unarchive(from: File, to: File)
}
