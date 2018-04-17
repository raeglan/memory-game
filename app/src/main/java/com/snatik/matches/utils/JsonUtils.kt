package com.snatik.matches.utils

import android.content.Context
import android.support.annotation.RawRes
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Just for getting and parsing json files.
 */
object JsonUtils {

    fun getJsonStringFromRaw(context: Context, @RawRes rawRes: Int): String {
        val inputStream = context.resources.openRawResource(rawRes)
        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        return reader.use(BufferedReader::readText)
    }
}
