package com.example.aaos_vehicle_monitor

data class VehicleState(
    val speedKmh: Int,
    val batteryPercent: Int,
    val outsideTempC: Int,
    val leftDoorOpen: Boolean,
    val rightDoorOpen: Boolean,
    val driveMode: String
)