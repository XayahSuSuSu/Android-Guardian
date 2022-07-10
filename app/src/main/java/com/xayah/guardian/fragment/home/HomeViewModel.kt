package com.xayah.guardian.fragment.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.xayah.guardian.R
import com.xayah.guardian.util.GlobalString
import com.xayah.guardian.util.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var server = ObservableField(GlobalString.disconnect)
    var state = ObservableField(GlobalString.unknown)
    var batteryCar = ObservableField(GlobalString.unknown)
    var batteryDrone = ObservableField(GlobalString.unknown)

    var batteryCarIcon = ObservableField<Drawable?>()
    var batteryDroneIcon = ObservableField<Drawable?>()

    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            batteryCarIcon.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_unknown
                )
            )
            batteryDroneIcon.set(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_unknown
                )
            )

            Server.check {
                if (it.code == 1)
                    server.set(GlobalString.connected)
            }
            Server.data {
                if (it.code == 1) {
                    state.set(it.data["state"].asString)

                    val batteryCarLocal = it.data["battery_car"].asString
                    val batteryDroneLocal = it.data["battery_drone"].asString

                    batteryCar.set(batteryCarLocal)
                    batteryDrone.set(batteryDroneLocal)

                    val batteryCarNum = batteryCarLocal.replace("%", "").toFloat().toInt()
                    val batteryDroneNum = batteryDroneLocal.replace("%", "").toFloat().toInt()

                    batteryCarIcon.set(getBatteryDrawable(context, batteryCarNum))
                    batteryDroneIcon.set(getBatteryDrawable(context, batteryDroneNum))

                }
            }
        }
    }

    private fun getBatteryDrawable(context: Context, battery: Int): Drawable? {
        when {
            0 == battery -> {
                return AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_0
                )
            }
            0 < battery && battery <= 14.3 -> {
                return AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_1
                )
            }
            14.3 < battery && battery <= 28.6 -> {
                return AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_2
                )
            }
            28.6 < battery && battery <= 42.9 -> {
                return AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_3
                )
            }
            42.9 < battery && battery <= 57.1 -> {
                return AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_4
                )
            }
            57.1 < battery && battery <= 71.4 -> {
                return AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_5
                )
            }
            71.4 < battery && battery <= 85.7 -> {
                return AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_6
                )
            }
            85.7 < battery -> {
                return AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_round_battery_full
                )
            }
        }
        return null
    }
}