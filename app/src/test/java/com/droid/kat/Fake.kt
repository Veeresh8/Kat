package com.droid.kat

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object Fake {

    fun buildFakeCatsJson(): String {
        return readFile("fake_cats.json")
    }

    fun buildFakeCatsList(items: Int = 0): List<CatData> {
        val catJson = buildFakeCatsJson()
        val json = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            explicitNulls = false
        }
        val result =  json.decodeFromString<List<CatData>>(catJson)
        if (items > 0)
            return result.take(items)
        return result
    }

    @Throws(IOException::class)
    fun readFile(fileName: String): String {
        var inputStream: InputStream? = null
        try {
            inputStream = javaClass.classLoader?.getResourceAsStream(fileName)
            val builder = StringBuilder()
            val reader = BufferedReader(InputStreamReader(inputStream))

            var str: String? = reader.readLine()
            while (str != null) {
                builder.append(str)
                str = reader.readLine()
            }
            return builder.toString()
        } finally {
            inputStream?.close()
        }
    }
}