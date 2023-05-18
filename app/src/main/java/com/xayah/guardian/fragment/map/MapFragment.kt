package com.xayah.guardian.fragment.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.permissionx.guolindev.PermissionX
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
import com.tencent.tencentmap.mapsdk.maps.TencentMap
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions
import com.xayah.guardian.App
import com.xayah.guardian.databinding.FragmentMapBinding
import com.xayah.guardian.util.readDeviceInfo
import com.xayah.guardian.util.readRTMPCarAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    private val binding get() = _binding!!

    lateinit var openDocumentTreeLauncher: ActivityResultLauncher<Uri>

    private var mPlayer: IjkMediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]
        binding.viewModel = viewModel

        openDocumentTreeLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree(), this::onOpenDocumentTreeResult
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PermissionX.init(this).permissions(
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            ).request { allGranted, _, _ ->
                if (!allGranted) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.parse("package:com.xayah.guardian")
                    startActivity(intent)
                }
            }
        } else {
            PermissionX.init(this).permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ).request { _, _, _ ->
            }
        }

        mPlayer = IjkMediaPlayer()
        try {
            mPlayer?.dataSource = App.globalContext.readRTMPCarAddress()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mPlayer?.prepareAsync()
        mPlayer?.start()
        val surfaceView = binding.surfaceView
        mPlayer?.setOnInfoListener { mp, _, _ ->
            if (mp.videoWidth != 0)
                surfaceView.apply {
                    layoutParams = layoutParams.apply {
                        height =
                            (mp.videoHeight.toFloat() / mp.videoWidth.toFloat() * surfaceView.width.toFloat()).toInt()
                    }
                }
            false
        }
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                mPlayer?.setDisplay(holder)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })

        viewModel.initialize()

        binding.materialButtonRefresh.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                App.server.state(App.globalContext.readDeviceInfo().device_code) {
                    if (it.code == 1) {
                        // "28.17821833", "112.94097200"
                        App.server.translate(it.data["latitude"].asString, it.data["longitude"].asString) {
                            Log.d("TAG", "translate: ${it}")
                            if (it.locations.isNotEmpty()) {
                                val location = it.locations.first()
                                binding.mapView.map.moveCamera(
                                    CameraUpdateFactory.newCameraPosition(
                                        CameraPosition(
                                            LatLng(location.lat.toDouble(), location.lng.toDouble()), // 中心点
                                            18.8F, // 缩放级别
                                            22.5F, // 倾斜角
                                            0F
                                        )
                                    )
                                ) // 移动地图
                                binding.mapView.map.addMarker(
                                    MarkerOptions(
                                        LatLng(
                                            location.lat.toDouble(),
                                            location.lng.toDouble()
                                        )
                                    )
                                ) // 添加标记点
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        binding.mapView.map.mapType = TencentMap.MAP_TYPE_SATELLITE
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun onOpenDocumentTreeResult(result: Uri?) {
        if (result != null) {
            requireActivity().contentResolver.takePersistableUriPermission(
                result,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        mPlayer?.stop()
        mPlayer = null
        _binding = null
    }
}