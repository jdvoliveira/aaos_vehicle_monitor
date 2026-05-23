package com.example.aaos_vehicle_monitor

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.LinkedList

class MainActivity : AppCompatActivity() {

    private lateinit var lastUpdatedText: TextView
    private lateinit var alertBanner: LinearLayout
    private lateinit var alertMessage: TextView
    private lateinit var speedValue: TextView
    private lateinit var batteryValue: TextView
    private lateinit var tempValue: TextView
    private lateinit var doorValue: TextView

    private lateinit var toggleDoorsButton: Button
    private lateinit var overspeedButton: Button
    private lateinit var lowBatteryButton: Button
    private lateinit var resetButton: Button

    private lateinit var eventHistoryText: TextView
    private val eventHistory = LinkedList<String>()
    private var lastAlertMessage: String? = null

    private val repository = VehicleRepository()
    private val handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            render(repository.nextState())
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lastUpdatedText = findViewById(R.id.lastUpdatedText)
        alertBanner = findViewById(R.id.alertBanner)
        alertMessage = findViewById(R.id.alertMessage)
        speedValue = findViewById(R.id.speedValue)
        batteryValue = findViewById(R.id.batteryValue)
        tempValue = findViewById(R.id.tempValue)
        doorValue = findViewById(R.id.doorValue)

        toggleDoorsButton = findViewById(R.id.toggleDoorsButton)
        overspeedButton = findViewById(R.id.overspeedButton)
        lowBatteryButton = findViewById(R.id.lowBatteryButton)
        resetButton = findViewById(R.id.resetButton)

        toggleDoorsButton.setOnClickListener {
            repository.toggleDoors()
            render(repository.nextState())
        }

        overspeedButton.setOnClickListener {
            repository.triggerOverspeed()
            render(repository.nextState())
        }

        lowBatteryButton.setOnClickListener {
            repository.triggerLowBattery()
            render(repository.nextState())
        }

        resetButton.setOnClickListener {
            repository.reset()
            appendEvent("Reset triggered")
            lastAlertMessage = null
            render(repository.nextState())
        }

        eventHistoryText = findViewById(R.id.eventHistoryText)

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

    private fun appendEvent(message: String) {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val eventEntry = String.format("%1\$s - %2\$s", currentTime, message)

        eventHistory.addFirst(eventEntry)

        while (eventHistory.size > 5) {
            eventHistory.removeLast()
        }

        eventHistoryText.text = eventHistory.joinToString("\n")
    }

    private fun updateEventHistory(currentAlertMessage: String) {
        if (lastAlertMessage != currentAlertMessage) {
            appendEvent(currentAlertMessage)
            lastAlertMessage = currentAlertMessage
        }
    }

    private fun render(state: VehicleState) {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        lastUpdatedText.text = "Last updated: $currentTime"

        speedValue.text = "${state.speedKmh} km/h"
        batteryValue.text = "${state.batteryPercent}%"
        tempValue.text = "${state.outsideTempC}°C"

        val anyDoorOpen = state.leftDoorOpen || state.rightDoorOpen
        doorValue.text = if (anyDoorOpen) "Open" else "Closed"

        when {
            state.speedKmh > 120 -> {
                alertMessage.text = "Overspeed warning"
                alertBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            }
            state.batteryPercent < 15 -> {
                alertMessage.text = "Low battery"
                alertBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            }
            anyDoorOpen -> {
                alertMessage.text = "Door open"
                alertBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))
            }
            else -> {
                alertMessage.text = "All systems normal"
                alertBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            }
        }

        updateEventHistory(alertMessage.text.toString())
    }
}