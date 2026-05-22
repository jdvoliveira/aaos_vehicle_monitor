package com.example.aaos_vehicle_monitor

import kotlin.random.Random

class VehicleRepository {
    private var speed = 42
    private var battery = 87
    private var temp = 21
    private var leftDoorOpen = false
    private var rightDoorOpen = false

    fun nextState(): VehicleState {
        speed = (speed + Random.nextInt(-8, 9)).coerceIn(0, 160)
        battery = (battery + Random.nextInt(-1, 1)).coerceIn(20, 100)
        temp = (temp + Random.nextInt(-1, 2)).coerceIn(16, 35)

        if (Random.nextInt(10) == 0) {
            leftDoorOpen = !leftDoorOpen
        }

        if (Random.nextInt(14) == 0) {
            rightDoorOpen = !rightDoorOpen
        }

        return VehicleState(
            speedKmh = speed,
            batteryPercent = battery,
            outsideTempC = temp,
            leftDoorOpen = leftDoorOpen,
            rightDoorOpen = rightDoorOpen
        )
    }
}