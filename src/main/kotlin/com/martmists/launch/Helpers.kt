package com.martmists.launch

import net.fabricmc.loader.impl.util.UrlConversionException
import net.fabricmc.loader.impl.util.UrlUtil
import java.io.IOException
import java.net.URL
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors

object Helpers {
    class EntrypointResult internal constructor(val entrypointName: String, val entrypointPath: Path)

    fun getSource(loader: ClassLoader, filename: String?): Optional<Path> {
        var url: URL?
        if (loader.getResource(filename).also { url = it } != null) {
            try {
                val urlSource = UrlUtil.getSource(filename, url)
                val classSourceFile = UrlUtil.asPath(urlSource)
                return Optional.of(classSourceFile)
            } catch (e: UrlConversionException) {
                // TODO: Point to a logger
                e.printStackTrace()
            }
        }
        return Optional.empty()
    }

    fun getSources(loader: ClassLoader, filename: String?): List<Path> {
        return try {
            val urls = loader.getResources(filename)
            val paths: MutableList<Path> = ArrayList()
            while (urls.hasMoreElements()) {
                val url = urls.nextElement()
                try {
                    val urlSource = UrlUtil.getSource(filename, url)
                    paths.add(UrlUtil.asPath(urlSource))
                } catch (e: UrlConversionException) {
                    // TODO: Point to a logger
                    e.printStackTrace()
                }
            }
            paths
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun findFirstClass(loader: ClassLoader, classNames: List<String>): Optional<EntrypointResult> {
        val entrypointFilenames = classNames.stream()
            .map { ep: String ->
                ep.replace(
                    '.',
                    '/'
                ) + ".class"
            }
            .collect(Collectors.toList())
        for (i in entrypointFilenames.indices) {
            val className = classNames[i]
            val classFilename = entrypointFilenames[i]
            val classSourcePath = getSource(loader, classFilename)
            if (classSourcePath.isPresent) {
                return Optional.of(EntrypointResult(className, classSourcePath.get()))
            }
        }
        return Optional.empty()
    }
}
