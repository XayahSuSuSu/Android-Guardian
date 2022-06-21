package com.xayah.guardian.fragment.settings

import androidx.lifecycle.ViewModel
import com.xayah.design.preference.EditableText
import com.xayah.guardian.App
import com.xayah.guardian.util.readServerAddress
import com.xayah.guardian.util.saveServerAddress

class SettingsViewModel : ViewModel() {
    var serverAddress = App.globalContext.readServerAddress()
    val changeServerAddress: (v: EditableText, content: CharSequence?) -> Unit = { v, content ->
        v.context.saveServerAddress(content.toString().trim())
        serverAddress = content.toString().trim()
    }
}