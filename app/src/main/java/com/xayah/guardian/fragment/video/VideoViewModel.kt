package com.xayah.guardian.fragment.video

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import java.util.*


class VideoViewModel : ViewModel() {

    var videoDecoder = ObservableField("")
    var fps = ObservableField("")
    var videoCached = ObservableField("")
    var audioCached = ObservableField("")
    var tcpSpeed = ObservableField("")
    var bitRate = ObservableField("")
    var seekLoadDuration = ObservableField("")

    fun formatedDurationMilli(duration: Long): String? {
        return if (duration >= 1000) {
            java.lang.String.format(Locale.US, "%.2f sec", duration.toFloat() / 1000)
        } else {
            java.lang.String.format(Locale.US, "%d msec", duration)
        }
    }

    fun formatedSize(bytes: Long): String? {
        return if (bytes >= 100 * 1000) {
            java.lang.String.format(Locale.US, "%.2f MB", bytes.toFloat() / 1000 / 1000)
        } else if (bytes >= 100) {
            java.lang.String.format(Locale.US, "%.1f KB", bytes.toFloat() / 1000)
        } else {
            java.lang.String.format(Locale.US, "%d B", bytes)
        }
    }

    fun formatedSpeed(bytes: Long, elapsed_milli: Long): String? {
        if (elapsed_milli <= 0) {
            return "0 B/s"
        }
        if (bytes <= 0) {
            return "0 B/s"
        }
        val bytes_per_sec = bytes.toFloat() * 1000f / elapsed_milli
        return if (bytes_per_sec >= 1000 * 1000) {
            java.lang.String.format(Locale.US, "%.2f MB/s", bytes_per_sec / 1000 / 1000)
        } else if (bytes_per_sec >= 1000) {
            java.lang.String.format(Locale.US, "%.1f KB/s", bytes_per_sec / 1000)
        } else {
            java.lang.String.format(Locale.US, "%d B/s", bytes_per_sec.toLong())
        }
    }
}