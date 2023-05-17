package com.xayah.guardian.fragment.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dylanc.activityresult.launcher.StartActivityLauncher
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity
import com.xayah.guardian.App
import com.xayah.guardian.R
import com.xayah.guardian.data.Authorize
import com.xayah.guardian.data.DeviceInfo
import com.xayah.guardian.databinding.FragmentHomeBinding
import com.xayah.guardian.util.GlobalString
import com.xayah.guardian.util.readRTMPAddress
import com.xayah.guardian.util.readRTMPCarAddress
import com.xayah.guardian.util.saveDeviceInfo
import com.xayah.guardian.util.setLoading
import com.xayah.guardian.view.setWithEdit
import com.xayah.materialyoufileexplorer.MaterialYouFileExplorer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var materialYouFileExplorer: MaterialYouFileExplorer
    private lateinit var startActivityLauncher: StartActivityLauncher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        binding.viewModel = viewModel

        initialize()
    }

    fun initialize() {
        val flag = ObservableBoolean(true)
        binding.buttonUp.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            App.server.action("up") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        App.server.action("cancel") {}
                    }
                }
            }
            false
        }
        binding.buttonLeft.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            App.server.action("left") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        App.server.action("cancel") {}
                    }
                }
            }
            false
        }
        binding.buttonDown.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            App.server.action("down") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        App.server.action("cancel") {}
                    }
                }
            }
            false
        }
        binding.buttonRight.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            App.server.action("right") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        App.server.action("cancel") {}
                    }
                }
            }
            false
        }
        binding.textButtonBind.setOnClickListener {
            if (!viewModel.isBound.get()) {
                startActivityLauncher.launch(
                    Intent(
                        requireActivity(), CaptureActivity::class.java
                    )
                ) { resultCode, data ->
                    if (resultCode == RESULT_OK) {
                        val result: String? = CameraScan.parseScanResult(data)
                        try {
                            val deviceInfo = Gson().fromJson(result, DeviceInfo::class.java)
                            if (deviceInfo.device_code != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    App.server.device("是", deviceInfo.device_code) {
                                        App.globalContext.saveDeviceInfo(deviceInfo)
                                        viewModel.initialize(requireContext())
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    this@HomeFragment.context,
                                    getString(R.string.qrcode_error),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    App.server.device("否", viewModel.deviceInfo.device_code) {
                        App.globalContext.saveDeviceInfo(DeviceInfo(""))
                    }
                }
                viewModel.initialize(requireContext())
            }

        }
        binding.iconButtonScan.setOnClickListener {
            startActivityLauncher.launch(
                Intent(
                    requireActivity(), CaptureActivity::class.java
                )
            ) { resultCode, data ->
                if (resultCode == RESULT_OK) {
                    val result: String? = CameraScan.parseScanResult(data)
                    try {
                        val authorize = Gson().fromJson(result, Authorize::class.java)
                        if (authorize.id != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                App.server.authorize(
                                    authorize.id,
                                    viewModel.deviceInfo.device_code,
                                    App.globalContext.readRTMPAddress(),
                                    App.globalContext.readRTMPCarAddress()
                                ) {}
                            }
                        } else {
                            Toast.makeText(
                                this@HomeFragment.context,
                                getString(R.string.qrcode_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        binding.materialButtonRecord.setOnClickListener {
            val view = it
            materialYouFileExplorer.apply {
                isFile = true
                suffixFilter = ArrayList("jpg,jpeg,png".split(","))
                filterWhitelist = true
                defPath = "/storage/emulated/0/DCIM"

                toExplorer(it.context) { path, _ ->
                    val context = requireContext()
                    MaterialAlertDialogBuilder(context).apply {
                        setWithEdit(context, GlobalString.editName, "") {
                            BottomSheetDialog(context).apply {
                                setLoading()
                                CoroutineScope(Dispatchers.IO).launch {
                                    App.server.picturesUpload(
                                        it,
                                        viewModel.deviceInfo.device_code,
                                        File(path)
                                    ) {
                                        dismiss()
                                        if (it.code != 1) {
                                            Toast.makeText(
                                                context,
                                                GlobalString.uploadFailed,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                viewModel.toPictureListActivity(view)
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
        binding.materialButtonRefresh.setOnClickListener {
            initialize()
        }

        viewModel.initialize(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityLauncher = StartActivityLauncher(requireActivity())
        materialYouFileExplorer = MaterialYouFileExplorer().apply {
            initialize(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()
        initialize()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}