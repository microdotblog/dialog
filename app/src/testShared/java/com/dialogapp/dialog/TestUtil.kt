package com.dialogapp.dialog

import com.dialogapp.dialog.vo.MicroBlogResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.Okio
import java.io.IOException
import java.nio.charset.StandardCharsets

object TestUtil {

    @Throws(IOException::class)
    fun readFromJson(classLoader: ClassLoader, fileName: String): MicroBlogResponse? {
        val moshi = Moshi.Builder().build()
        val listMyData = Types.newParameterizedType(MicroBlogResponse::class.java)
        val jsonAdapter = moshi.adapter<MicroBlogResponse>(listMyData)

        val inputStream = classLoader.getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        return jsonAdapter.fromJson(source.readString(StandardCharsets.UTF_8))
    }
}
