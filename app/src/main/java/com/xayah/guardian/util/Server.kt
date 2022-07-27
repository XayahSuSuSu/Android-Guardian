package com.xayah.guardian.util

import com.google.gson.Gson
import com.xayah.guardian.App
import com.xayah.guardian.data.Body
import com.xayah.guardian.data.PicturesBody
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


class Server {
    companion object {
        private val checkApi = "${App.globalContext.readServerAddress()}/api/v1/check"
        private val stateApi = "${App.globalContext.readServerAddress()}/api/v1/state"
        private val actionApi = "${App.globalContext.readServerAddress()}/api/v1/action"
        private val authorizeApi = "${App.globalContext.readServerAddress()}/api/v1/authorize"
        private val deviceApi = "${App.globalContext.readServerAddress()}/api/v1/device"
        private val picturesApi = "${App.globalContext.readServerAddress()}/api/v1/pictures"
        private val picturesDeleteApi =
            "${App.globalContext.readServerAddress()}/api/v1/pictures/delete"
        private val picturesUploadApi = "${App.globalContext.readServerAddress()}/api/v1/pictures"

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

        fun state(deviceCode: String, callback: (body: Body) -> Unit) {
            try {
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url("${stateApi}?device_code=${deviceCode}")
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

        fun action(action: String, callback: (body: Body) -> Unit) {
            try {
                val client = OkHttpClient()
                val formBody = FormBody.Builder()
                    .add("action", action)
                    .build()
                val request: Request = Request.Builder()
                    .url(actionApi)
                    .post(formBody)
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

        fun authorize(
            id: String,
            deviceCode: String,
            rtmpAddressCourt: String,
            rtmpAddressCar: String,
            callback: (body: Body) -> Unit
        ) {
            try {
                val client = OkHttpClient()
                val formBody = FormBody.Builder()
                    .add("id", id)
                    .add("device_code", deviceCode)
                    .add("rtmp_address_court", rtmpAddressCourt)
                    .add("rtmp_address_car", rtmpAddressCar)
                    .build()
                val request: Request = Request.Builder()
                    .url(authorizeApi)
                    .post(formBody)
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

        fun device(bindState: String, deviceCode: String, callback: (body: Body) -> Unit) {
            try {
                val client = OkHttpClient()
                val formBody = FormBody.Builder()
                    .add("bind_state", bindState)
                    .add("device_code", deviceCode)
                    .build()
                val request: Request = Request.Builder()
                    .url(deviceApi)
                    .post(formBody)
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

        fun pictures(deviceCode: String, callback: (body: PicturesBody) -> Unit) {
            try {
                val client = OkHttpClient()
                val request: Request = Request.Builder()
                    .url("${picturesApi}?device_code=${deviceCode}")
                    .build()
                client.newCall(request).execute().use { response ->
                    response.body?.apply {
                        // 解析response.body
                        try {
                            val bodyString = this.string()
                            callback(Gson().fromJson(bodyString, PicturesBody::class.java))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun picturesDelete(path: String, callback: (body: Body) -> Unit) {
            try {
                val client = OkHttpClient()
                val formBody = FormBody.Builder()
                    .add("path", path)
                    .build()
                val request: Request = Request.Builder()
                    .url(picturesDeleteApi)
                    .post(formBody)
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

        fun picturesUpload(
            name: String,
            deviceCode: String,
            file: File,
            callback: (body: Body) -> Unit
        ) {
            try {
                val client = OkHttpClient()
                val MEDIA_TYPE_PNG: MediaType = "image/png".toMediaType()
                val multipartBody = MultipartBody.Builder().apply {
                    setType(MultipartBody.FORM)
                    addFormDataPart("name", name)
                    addFormDataPart("device_code", deviceCode)
                    addFormDataPart("file", file.name, file.asRequestBody(MEDIA_TYPE_PNG))
                }
                val request: Request = Request.Builder()
                    .url(picturesUploadApi)
                    .post(multipartBody.build())
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