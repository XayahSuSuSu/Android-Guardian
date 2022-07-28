package com.xayah.guardian

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.xayah.guardian.util.Server

class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var globalContext: Context
        lateinit var server: Server
    }

    override fun onCreate() {
        super.onCreate()
        globalContext = this
        server = Server()
    }
}