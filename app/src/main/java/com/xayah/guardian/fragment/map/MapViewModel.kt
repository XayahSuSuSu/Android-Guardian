package com.xayah.guardian.fragment.map

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xayah.guardian.App
import com.xayah.guardian.util.GlobalString
import com.xayah.guardian.util.readRTMPCarAddress
import com.xayah.guardian.util.saveRTMPCarAddress
import com.xayah.guardian.view.setWithEdit

class MapViewModel : ViewModel() {
    var rtmpAddressSubTitle = ObservableField("")

    fun initialize() {
        rtmpAddressSubTitle.set("${GlobalString.streamingKey}: ${getStreamingKey(App.globalContext.readRTMPCarAddress())}")
    }

    fun onEdit(v: View) {
        MaterialAlertDialogBuilder(v.context).apply {
            setWithEdit(v.context, GlobalString.rtmpAddress, v.context.readRTMPCarAddress()) {
                v.context.saveRTMPCarAddress(it.trim())
                rtmpAddressSubTitle.set("${GlobalString.streamingKey}: ${getStreamingKey(it.trim())}")
            }
        }
    }

    fun getStreamingKey(address: String): String {
        return address.trim().split("/").lastOrNull() ?: ""
    }
}