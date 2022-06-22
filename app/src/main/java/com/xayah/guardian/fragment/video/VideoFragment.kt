package com.xayah.guardian.fragment.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xayah.guardian.App
import com.xayah.guardian.databinding.FragmentVideoBinding
import com.xayah.guardian.util.readRTMPAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException
import java.lang.String
import java.util.*


class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null

    private val binding get() = _binding!!

    private var mPlayer: IjkMediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()
    }

    private fun initialize() {
        val viewModel = ViewModelProvider(requireActivity())[VideoViewModel::class.java]
        binding.viewModel = viewModel

        mPlayer = IjkMediaPlayer()
        try {
            mPlayer?.dataSource = App.globalContext.readRTMPAddress()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mPlayer?.prepareAsync()
        mPlayer?.start()
        CoroutineScope(Dispatchers.IO).launch {
            while (_binding != null) {
                delay(1000)
                when (mPlayer?.videoDecoder) {
                    IjkMediaPlayer.FFP_PROPV_DECODER_AVCODEC -> {
                        viewModel.videoDecoder.set("avcodec")
                    }
                    IjkMediaPlayer.FFP_PROPV_DECODER_MEDIACODEC -> {
                        viewModel.videoDecoder.set("MediaCodec")
                    }
                }
                val fpsOutput: Float = mPlayer?.videoOutputFramesPerSecond ?: 0F
                val fpsDecode: Float = mPlayer?.videoDecodeFramesPerSecond ?: 0F
                viewModel.fps.set(
                    String.format(Locale.US, "%.2f / %.2f", fpsDecode, fpsOutput)
                )
                val videoCachedDuration: Long = mPlayer?.videoCachedDuration ?: 0
                val audioCachedDuration: Long = mPlayer?.audioCachedDuration ?: 0
                val videoCachedBytes: Long = mPlayer?.videoCachedBytes ?: 0
                val audioCachedBytes: Long = mPlayer?.audioCachedBytes ?: 0
                val tcpSpeed: Long = mPlayer?.tcpSpeed ?: 0
                val bitRate: Long = mPlayer?.bitRate ?: 0
                val seekLoadDuration: Long = mPlayer?.seekLoadDuration ?: 0
                viewModel.videoCached.set(
                    String.format(
                        Locale.US,
                        "%s, %s",
                        viewModel.formatedDurationMilli(videoCachedDuration),
                        viewModel.formatedSize(videoCachedBytes)
                    )
                )
                viewModel.audioCached.set(
                    String.format(
                        Locale.US,
                        "%s, %s",
                        viewModel.formatedDurationMilli(audioCachedDuration),
                        viewModel.formatedSize(audioCachedBytes)
                    )
                )
                viewModel.seekLoadDuration.set(
                    String.format(Locale.US, "%d ms", seekLoadDuration)
                )
                viewModel.tcpSpeed.set(
                    String.format(
                        Locale.US,
                        "%s",
                        viewModel.formatedSpeed(tcpSpeed, 1000)
                    )
                )
                viewModel.bitRate.set(String.format(Locale.US, "%.2f kbs", bitRate / 1000f))
            }

        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mPlayer?.stop()
        mPlayer = null
    }
}