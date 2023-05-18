package com.xayah.guardian.util

import com.google.gson.Gson
import com.xayah.guardian.App
import com.xayah.guardian.data.Body
import com.xayah.guardian.data.PicturesBody
import com.xayah.guardian.data.TranslateBody
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException


class Server {
    private val client = OkHttpClient()
    private val checkApi
        get() = "${App.globalContext.readServerAddress()}/api/v1/check"
    private val stateApi
        get() = "${App.globalContext.readServerAddress()}/api/v1/state"
    private val actionApi
        get() = "${App.globalContext.readServerAddress()}/api/v1/action"
    private val authorizeApi
        get() = "${App.globalContext.readServerAddress()}/api/v1/authorize"
    private val deviceApi
        get() = "${App.globalContext.readServerAddress()}/api/v1/device"
    private val picturesApi
        get() = "${App.globalContext.readServerAddress()}/api/v1/pictures"
    private val picturesDeleteApi
        get() = "${App.globalContext.readServerAddress()}/api/v1/pictures/delete"
    private val picturesUploadApi
        get() = "${App.globalContext.readServerAddress()}/api/v1/pictures"
    private val translateApi = "https://apis.map.qq.com/ws/coord/v1/translate"
    private val tencentMapKey = "FSFBZ-ORBCX-YXQ4D-TJANU-NK6GQ-GVFJI"

    fun check(callback: (body: Body) -> Unit) {
        try {
            val request: Request = Request.Builder().url(checkApi).build()
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

    fun translate(latitude: String, longitude: String, callback: (body: TranslateBody) -> Unit) {
        try {
            val request: Request =
                Request.Builder().url("${translateApi}?key=${tencentMapKey}&type=1&locations=$latitude,$longitude")
                    .build()
            client.newCall(request).execute().use { response ->
                response.body?.apply {
                    // 解析response.body
                    try {
                        val bodyString = this.string()
                        callback(Gson().fromJson(bodyString, TranslateBody::class.java))
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