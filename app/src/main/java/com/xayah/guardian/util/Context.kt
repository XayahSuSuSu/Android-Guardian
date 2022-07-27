package com.xayah.guardian.util

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.drawable.TransitionDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.R
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.xayah.guardian.data.DeviceInfo

fun Context.savePreferences(key: String, value: String) {
    getSharedPreferences("settings", MODE_PRIVATE).edit().apply {
        putString(key, value)
        apply()
    }
}

fun Context.savePreferences(key: String, value: Boolean) {
    getSharedPreferences("settings", MODE_PRIVATE).edit().apply {
        putBoolean(key, value)
        apply()
    }
}

fun Context.readPreferencesString(key: String): String? {
    getSharedPreferences("settings", MODE_PRIVATE).apply {
        return getString(key, null)
    }
}

fun Context.readPreferencesBoolean(key: String, defValue: Boolean = false): Boolean {
    getSharedPreferences("settings", MODE_PRIVATE).apply {
        return getBoolean(key, defValue)
    }
}

fun Context.saveServerAddress(path: CharSequence?) {
    savePreferences("server_address", path.toString().trim())
}

fun Context.readServerAddress(): String {
    return readPreferencesString("server_address") ?: GlobalString.defaultServerAddress
}

fun Context.saveRTMPAddress(path: CharSequence?) {
    savePreferences("rtmp_address", path.toString().trim())
}

fun Context.readRTMPAddress(): String {
    return readPreferencesString("rtmp_address") ?: GlobalString.defaultRTMPAddress
}

fun Context.saveRTMPCarAddress(path: CharSequence?) {
    savePreferences("rtmp_car_address", path.toString().trim())
}

fun Context.readRTMPCarAddress(): String {
    return readPreferencesString("rtmp_car_address") ?: GlobalString.defaultRTMPCarAddress
}

fun Context.saveDeviceInfo(deviceInfo: DeviceInfo) {
    savePreferences("device_info", Gson().toJson(deviceInfo))
}

fun Context.readDeviceInfo(): DeviceInfo {
    var deviceInfo = DeviceInfo("")
    try {
        deviceInfo = Gson().fromJson(
            readPreferencesString("device_info") ?: "",
            DeviceInfo::class.java
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return deviceInfo
}

fun Context.getActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun RecyclerView.fastInitialize() {
    this.apply {
        (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        layoutManager = GridLayoutManager(this.context, 1)
        layoutAnimation = LayoutAnimationController(
            AnimationUtils.loadAnimation(
                context,
                R.anim.abc_grow_fade_in_from_bottom
            )
        ).apply {
            order = LayoutAnimationController.ORDER_NORMAL
            delay = 0.3F
        }
    }
}

val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

fun BottomSheetDialog.setWithTopBar(): LinearLayout {
    this.apply {
        val context = this.context
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        val topBar: View = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(36.dp, 4.dp).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            background = TransitionDrawable(
                arrayOf(
                    AppCompatResources.getDrawable(
                        context,
                        com.xayah.guardian.R.drawable.bottom_sheet_dialog_topbar
                    ),
                    AppCompatResources.getDrawable(
                        context,
                        com.xayah.guardian.R.drawable.bottom_sheet_dialog_topbar_activated
                    )
                )
            )
        }
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        (topBar.background as TransitionDrawable).startTransition(150)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        (topBar.background as TransitionDrawable).reverseTransition(150)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        (topBar.background as TransitionDrawable).reverseTransition(150)
                    }
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        val mainView = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(0, 16.dp, 0, 16.dp)
            addView(topBar)
        }
        setContentView(mainView)
        show()
        return mainView
    }
}

fun BottomSheetDialog.setLoading() {
    this.apply {
        setCancelable(false)
        val titleView =
            MaterialTextView(context).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).also {
                    it.topMargin = 16.dp
                }
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium)
                text = GlobalString.pleaseWait
                gravity = Gravity.CENTER_HORIZONTAL
            }
        val lottieAnimationView = LottieAnimationView(this.context).apply {
            layoutParams =
                RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    200.dp
                ).apply {
                    addRule(RelativeLayout.CENTER_IN_PARENT)
                }
            setAnimation(com.xayah.guardian.R.raw.loading)
            playAnimation()
            repeatCount = LottieDrawable.INFINITE
        }
        setWithTopBar().apply {
            addView(titleView)
            addView(lottieAnimationView)
        }
    }
}
