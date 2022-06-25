package com.xayah.guardian.util

import com.xayah.guardian.App
import com.xayah.guardian.R

class GlobalString {
    companion object {
        val defaultServerAddress = App.globalContext.getString(R.string.default_server_address)
        val defaultRTMPAddress = App.globalContext.getString(R.string.default_rtmp_address)
        val defaultRTMPCarAddress = App.globalContext.getString(R.string.default_rtmp_car_address)
        val connected = App.globalContext.getString(R.string.connected)
        val disconnect = App.globalContext.getString(R.string.disconnect)
        val unknown = App.globalContext.getString(R.string.unknown)
    }
}