package com.xayah.guardian.fragment.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dylanc.activityresult.launcher.StartActivityLauncher
import com.google.gson.Gson
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity
import com.xayah.guardian.App
import com.xayah.guardian.data.Authorize
import com.xayah.guardian.data.DeviceInfo
import com.xayah.guardian.databinding.FragmentHomeBinding
import com.xayah.guardian.util.Server
import com.xayah.guardian.util.saveDeviceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var startActivityLauncher: StartActivityLauncher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        binding.viewModel = viewModel

        val flag = ObservableBoolean(true)
        binding.buttonUp.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            Server.action("up") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        Server.action("cancel") {}
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
                            Server.action("left") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        Server.action("cancel") {}
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
                            Server.action("down") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        Server.action("cancel") {}
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
                            Server.action("right") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        Server.action("cancel") {}
                    }
                }
            }
            false
        }
        binding.textButtonBind.setOnClickListener {
            if (!viewModel.isBound.get()) {
                startActivityLauncher.launch(
                    Intent(
                        requireActivity(),
                        CaptureActivity::class.java
                    )
                ) { resultCode, data ->
                    if (resultCode == RESULT_OK) {
                        val result: String? = CameraScan.parseScanResult(data)
                        try {
                            val deviceInfo = Gson().fromJson(result, DeviceInfo::class.java)
                            App.globalContext.saveDeviceInfo(deviceInfo)
                            viewModel.initialize(requireContext())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                App.globalContext.saveDeviceInfo(DeviceInfo("", ""))
                viewModel.initialize(requireContext())
            }

        }

        binding.iconButtonScan.setOnClickListener {
            startActivityLauncher.launch(
                Intent(
                    requireActivity(),
                    CaptureActivity::class.java
                )
            ) { resultCode, data ->
                if (resultCode == RESULT_OK) {
                    val result: String? = CameraScan.parseScanResult(data)
                    try {
                        val authorize = Gson().fromJson(result, Authorize::class.java)
                        CoroutineScope(Dispatchers.IO).launch {
                            Server.authorize(authorize.id, viewModel.deviceInfo.code) {}
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        viewModel.initialize(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivityLauncher = StartActivityLauncher(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}