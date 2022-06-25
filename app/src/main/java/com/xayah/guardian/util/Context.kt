package com.xayah.guardian.util

import android.content.Context
import android.content.Context.MODE_PRIVATE

fun Context.savePreferences(key: String, value: String) {
    getSharedPreferences("settings", MODE_PRIVATE).edit().apply {
        putString(key, value)
        apply()
    }
}

fun Context.savePreferences(key: String, value: Boolean) {
    getSharedPreferences("settings", MODE_PRIVATE).edit().apply {
        putBoolean(key, value)
        apply()
    }
}

fun Context.readPreferencesString(key: String): String? {
    getSharedPreferences("settings", MODE_PRIVATE).apply {
        return getString(key, null)
    }
}

fun Context.readPreferencesBoolean(key: String, defValue: Boolean = false): Boolean {
    getSharedPreferences("settings", MODE_PRIVATE).apply {
        return getBoolean(key, defValue)
    }
}

fun Context.saveServerAddress(path: CharSequence?) {
    savePreferences("server_address", path.toString().trim())
}

fun Context.readServerAddress(): String {
    return readPreferencesString("server_address") ?: GlobalString.defaultServerAddress
}

fun Context.saveRTMPAddress(path: CharSequence?) {
    savePreferences("rtmp_address", path.toString().trim())
}

fun Context.readRTMPAddress(): String {
    return readPreferencesString("rtmp_address") ?: GlobalString.defaultRTMPAddress
}

fun Context.saveRTMPCarAddress(path: CharSequence?) {
    savePreferences("rtmp_car_address", path.toString().trim())
}

fun Context.readRTMPCarAddress(): String {
    return readPreferencesString("rtmp_car_address") ?: GlobalString.defaultRTMPCarAddress
}
