package io.kotless.plugin.gradle.utils

import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

internal object Downloads {
    private fun download(url: URL, toFile: File) {
        toFile.parentFile.mkdirs()
        FileOutputStream(toFile).channel.transferFrom(Channels.newChannel(url.openStream()), 0, java.lang.Long.MAX_VALUE)
    }

    fun download(url: URL, toFile: File, archiver: Archive) {
        toFile.parentFile.mkdirs()

        val archive = File(toFile.absolutePath + "." + archiver.extension)

        if (archive.exists()) archive.delete()
        download(url, archive)

        if (toFile.exists()) toFile.deleteRecursively()
        archiver.unarchive(archive, toFile)

        archive.delete()
    }
}
