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
        val notBind = App.globalContext.getString(R.string.not_bind)
        val pleaseBindDevice = App.globalContext.getString(R.string.please_bind_device)
        val bound = App.globalContext.getString(R.string.bound)
        val bind = App.globalContext.getString(R.string.bind)
        val unbind = App.globalContext.getString(R.string.unbind)
    }
}