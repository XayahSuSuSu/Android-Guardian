package com.xayah.guardian.fragment.home

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.xayah.guardian.util.GlobalString
import com.xayah.guardian.util.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var server = ObservableField(GlobalString.disconnect)

    fun initialize() {
        CoroutineScope(Dispatchers.IO).launch {
            Server.check {
                if (it.code == 1)
                    server.set(GlobalString.connected)
            }
        }
    }

}