package com.martmists.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

private val mapper by lazy {
    ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)).also {
        it.registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
    }
}

inline fun <reified T : Any> loadConfig(path: String, default: String): T = loadConfig(path, default, T::class.java)

@PublishedApi
internal fun <T: Any> loadConfig(path: String, default: String, clazz: Class<T>): T {
    val config = File(path)
    if (!config.exists()) {
        config.parentFile?.mkdirs()
        config.createNewFile()

        config.outputStream().use { out ->
            clazz.getResourceAsStream(default).use { inp ->
                inp.copyTo(out)
            }
        }
    }

    return config.inputStream().use {
        mapper.readValue(it, clazz)
    }
}
