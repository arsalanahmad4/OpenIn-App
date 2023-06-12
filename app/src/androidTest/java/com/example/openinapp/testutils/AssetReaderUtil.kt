package com.example.openinapp.testutils

import androidx.test.platform.app.InstrumentationRegistry
import java.io.IOException
import java.io.InputStreamReader

open class AssetReaderUtil {
    companion object {
        fun asset(assetPath: String): String {
            try {
                val inputStream = (InstrumentationRegistry.getInstrumentation().targetContext
                    .applicationContext).classLoader.getResourceAsStream("api-test-response/$assetPath")
                val builder = StringBuilder()
                val reader = InputStreamReader(inputStream, "UTF-8")
                reader.readLines().forEach {
                    builder.append(it)
                }
                return builder.toString()
            } catch (e: IOException) {
                throw e
            }

        }
    }
}