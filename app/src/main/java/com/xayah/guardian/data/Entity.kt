package com.xayah.guardian.data

import com.google.gson.JsonObject

data class Body(
    val code: Int,
    val msg: String,
    val data: JsonObject
)
