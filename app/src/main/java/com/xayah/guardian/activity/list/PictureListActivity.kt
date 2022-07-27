package com.xayah.guardian.activity.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.drakeet.multitype.MultiTypeAdapter
import com.google.gson.Gson
import com.xayah.guardian.App
import com.xayah.guardian.adapter.PictureListAdapter
import com.xayah.guardian.data.Picture
import com.xayah.guardian.databinding.ActivityPictureListBinding
import com.xayah.guardian.util.Server
import com.xayah.guardian.util.fastInitialize
import com.xayah.guardian.util.readDeviceInfo
import com.xayah.guardian.util.savePicturesNum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PictureListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPictureListBinding
    private lateinit var viewModel: PictureListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityPictureListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PictureListViewModel::class.java]
        binding.viewModel = viewModel
        setContentView(binding.root)

        initialize()
    }

    private fun initialize() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        val deviceInfo = App.globalContext.readDeviceInfo()

        binding.recyclerView.apply {
            adapter = MultiTypeAdapter().apply {
                register(PictureListAdapter {
                    initialize()
                })
                val adapterList = mutableListOf<Any>()
                CoroutineScope(Dispatchers.IO).launch {
                    Server.pictures(deviceInfo.device_code) {
                        val objectList = mutableListOf<String>()
                        for (i in it.data) {
                            val picture = Gson().fromJson(i, Picture::class.java)
                            if (objectList.indexOf(picture.name) == -1) objectList.add(picture.name)
                            adapterList.add(picture)
                            CoroutineScope(Dispatchers.Main).launch {
                                adapter?.notifyDataSetChanged()
                            }
                        }
                        App.globalContext.savePicturesNum("${objectList.size}-${it.data.size()}")
                    }
                }
                items = adapterList
            }
            fastInitialize()
        }
    }
}