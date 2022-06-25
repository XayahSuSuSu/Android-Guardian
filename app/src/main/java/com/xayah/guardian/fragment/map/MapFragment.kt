package com.xayah.guardian.fragment.map

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.permissionx.guolindev.PermissionX
import com.xayah.guardian.App
import com.xayah.guardian.databinding.FragmentMapBinding
import com.xayah.guardian.util.readRTMPCarAddress
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
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
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