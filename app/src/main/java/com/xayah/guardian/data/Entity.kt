package com.xayah.guardian.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject

data class Body(
    val code: Int,
    val msg: String,
    val data: JsonObject
)

data class Action(
    val action: String,
    val state: String,
)

data class DeviceInfo(
    val device_code: String,
)

data class Authorize(
    val id: String,
)

data class PicturesBody(
    val code: Int,
    val msg: String,
    val data: JsonArray
)

data class Picture(
    val device_code: String,
    val name: String,
    val path: String
)