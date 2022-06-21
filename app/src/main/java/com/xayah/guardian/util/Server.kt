package com.xayah.guardian.util

import com.google.gson.Gson
import com.xayah.guardian.App
import com.xayah.guardian.data.Body
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class Server {
    companion object {
        private val checkApi = "${App.globalContext.readServerAddress()}/api/v1/check"

        fun check(callback: (body: Body) -> Unit) {
            try {
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url(checkApi)
                    .build()
                client.newCall(request).execute().use { response ->
                    response.body?.apply {
                        // 解析response.body
                        callback(Gson().fromJson(this.string(), Body::class.java))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}