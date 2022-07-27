package com.xayah.guardian.activity.list

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.xayah.guardian.App
import com.xayah.guardian.adapter.PictureListAdapter
import com.xayah.guardian.data.Picture
import com.xayah.guardian.databinding.ActivityPictureListBinding
import com.xayah.guardian.util.*
import com.xayah.guardian.view.setWithEdit
import com.xayah.materialyoufileexplorer.MaterialYouFileExplorer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PictureListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPictureListBinding
    private lateinit var viewModel: PictureListViewModel
    private lateinit var materialYouFileExplorer: MaterialYouFileExplorer

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        materialYouFileExplorer = MaterialYouFileExplorer().apply {
            initialize(this@PictureListActivity)
        }

        binding = ActivityPictureListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PictureListViewModel::class.java]
        binding.viewModel = viewModel
        setContentView(binding.root)

        initialize()
    }

    fun initialize() {
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
                        for (i in it.data) {
                            adapterList.add(Gson().fromJson(i, Picture::class.java))
                            CoroutineScope(Dispatchers.Main).launch {
                                adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
                items = adapterList
            }
            fastInitialize()
        }

        binding.floatingActionButton.setOnClickListener {
            materialYouFileExplorer.apply {
                isFile = true
                suffixFilter = ArrayList("jpg,jpeg,png".split(","))
                filterWhitelist = true
                defPath = "/storage/emulated/0/DCIM"

                toExplorer(it.context) { path, _ ->
                    val context = this@PictureListActivity
                    MaterialAlertDialogBuilder(context).apply {
                        setWithEdit(context, GlobalString.editName, "") {
                            BottomSheetDialog(context).apply {
                                setLoading()
                                CoroutineScope(Dispatchers.IO).launch {
                                    Server.picturesUpload(it, deviceInfo.device_code, File(path)) {
                                        dismiss()
                                        if (it.code != 1) {
                                            Toast.makeText(
                                                this@PictureListActivity,
                                                GlobalString.uploadFailed,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                initialize()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}