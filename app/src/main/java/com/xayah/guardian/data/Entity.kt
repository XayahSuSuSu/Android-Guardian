package com.xayah.guardian.data

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