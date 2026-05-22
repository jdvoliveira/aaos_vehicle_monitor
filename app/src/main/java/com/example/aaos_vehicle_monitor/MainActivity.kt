package com.example.aaos_vehicle_monitor

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var alertBanner: TextView
    private lateinit var speedValue: TextView
    private lateinit var batteryValue: TextView
    private lateinit var tempValue: TextView
    private lateinit var doorValue: TextView

    private val repository = VehicleRepository()
    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            render(repository.nextState())
            handler.postDelayed(this, 1500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alertBanner = findViewById(R.id.alertBanner)
        speedValue = findViewById(R.id.speedValue)
        batteryValue = findViewById(R.id.batteryValue)
        tempValue = findViewById(R.id.tempValue)
        doorValue = findViewById(R.id.doorValue)

        render(repository.nextState())
    }

    override fun onStart() {
        super.onStart()
        handler.post(updateRunnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateRunnable)
    }

    private fun render(state: VehicleState) {
        speedValue.text = "${state.speedKmh} km/h"
        batteryValue.text = "${state.batteryPercent}%"
        tempValue.text = "${state.outsideTempC}°C"

        val anyDoorOpen = state.leftDoorOpen || state.rightDoorOpen
        doorValue.text = if (anyDoorOpen) "Open" else "Closed"

        when {
            state.speedKmh > 120 -> {
                alertBanner.text = "Overspeed warning"
                alertBanner.setBackgroundColor(Color.parseColor("#B71C1C"))
            }
            anyDoorOpen -> {
                alertBanner.text = "Door open"
                alertBanner.setBackgroundColor(Color.parseColor("#F9A825"))
            }
            else -> {
                alertBanner.text = "All systems normal"
                alertBanner.setBackgroundColor(Color.parseColor("#2E7D32"))
            }
        }
    }
}