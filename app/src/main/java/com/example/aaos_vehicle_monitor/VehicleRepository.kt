package com.example.aaos_vehicle_monitor

import kotlin.random.Random

class VehicleRepository {

    private var speed = 42
    private var battery = 87
    private var temp = 21
    private var leftDoorOpen = false
    private var rightDoorOpen = false

    private var forceOverspeed = false
    private var forceLowBattery = false

    fun nextState(): VehicleState {
        if (forceOverspeed) {
            speed = 135
        } else {
            speed = (speed + Random.nextInt(-8, 9)).coerceIn(0, 160)
        }

        if (forceLowBattery) {
            battery = 12
        } else {
            battery = (battery + Random.nextInt(-1, 2)).coerceIn(20, 100)
        }

        temp = (temp + Random.nextInt(-1, 2)).coerceIn(16, 35)

        return VehicleState(
            speedKmh = speed,
            batteryPercent = battery,
            outsideTempC = temp,
            leftDoorOpen = leftDoorOpen,
            rightDoorOpen = rightDoorOpen
        )
    }

    fun toggleDoors() {
        val newValue = !(leftDoorOpen || rightDoorOpen)
        leftDoorOpen = newValue
        rightDoorOpen = newValue
    }

    fun triggerOverspeed() {
        forceOverspeed = !forceOverspeed
    }

    fun triggerLowBattery() {
        forceLowBattery = !forceLowBattery
    }

    fun reset() {
        speed = 42
        battery = 87
        temp = 21
        leftDoorOpen = false
        rightDoorOpen = false
        forceOverspeed = false
        forceLowBattery = false
    }
}