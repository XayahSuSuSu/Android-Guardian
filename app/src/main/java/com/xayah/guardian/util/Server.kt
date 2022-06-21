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
        private val dataApi = "${App.globalContext.readServerAddress()}/api/v1/app/data"

        fun check(callback: (body: Body) -> Unit) {
            try {
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url(checkApi)
                    .build()
                client.newCall(request).execute().use { response ->
                    response.body?.apply {
                        // 解析response.body
                        try {
                            callback(Gson().fromJson(this.string(), Body::class.java))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun data(callback: (body: Body) -> Unit) {
            try {
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url(dataApi)
                    .build()
                client.newCall(request).execute().use { response ->
                    response.body?.apply {
                        // 解析response.body
                        try {
                            callback(Gson().fromJson(this.string(), Body::class.java))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}