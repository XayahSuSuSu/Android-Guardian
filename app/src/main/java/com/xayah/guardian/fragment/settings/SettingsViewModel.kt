package com.xayah.guardian.fragment.settings

import androidx.lifecycle.ViewModel
import com.xayah.design.preference.EditableText
import com.xayah.guardian.App
import com.xayah.guardian.util.*

class SettingsViewModel : ViewModel() {
    var serverAddress = App.globalContext.readServerAddress()
    val changeServerAddress: (v: EditableText, content: CharSequence?) -> Unit = { v, content ->
        v.context.saveServerAddress(content.toString().trim())
        serverAddress = content.toString().trim()
    }

    var RTMPAddress = App.globalContext.readRTMPAddress()
    val changeRTMPAddress: (v: EditableText, content: CharSequence?) -> Unit = { v, content ->
        v.context.saveRTMPAddress(content.toString().trim())
        RTMPAddress = content.toString().trim()
    }

    var RTMPCarAddress = App.globalContext.readRTMPCarAddress()
    val changeRTMPCarAddress: (v: EditableText, content: CharSequence?) -> Unit = { v, content ->
        v.context.saveRTMPCarAddress(content.toString().trim())
        RTMPCarAddress = content.toString().trim()
    }
}