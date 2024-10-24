package com.transsion.mediaplayerdemo.ui.activity

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.transsion.mediaplayerdemo.R

class BatteryDialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity created")
        val batteryStatus = getBatteryStatus()
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.battery_status_title))
            .setMessage(getString(R.string.battery_status_message, batteryStatus))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .create()

        dialog.setOnDismissListener {
            finish()
        }

        dialog.show()
    }

    private fun getBatteryStatus(): Int {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, BatteryDialogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
        const val TAG = "BatteryStatusDialogActivity"
    }
}