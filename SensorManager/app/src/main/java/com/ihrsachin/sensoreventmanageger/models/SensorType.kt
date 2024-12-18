package com.ihrsachin.sensoreventmanageger.models

enum class SensorType(val value: Int) {
    INVALID(-1),
    ACCELEROMETER(1),
    MAGNETIC_FIELD(2),
    GYROSCOPE(4),
    LIGHT(5),
    PRESSURE(6),
    PROXIMITY(8),
    GRAVITY(9),
    LINEAR_ACCELERATION(10),
    ROTATION_VECTOR(11),
    RELATIVE_HUMIDITY(12),
    AMBIENT_TEMPERATURE(13),
    MAGNETIC_FIELD_UNCALIBRATED(14),
    GAME_ROTATION_VECTOR(15),
    GYROSCOPE_UNCALIBRATED(16),
    SIGNIFICANT_MOTION(17),
    STEP_DETECTOR(18),
    STEP_COUNTER(19),
    GEOMAGNETIC_ROTATION_VECTOR(20),
    HEART_RATE(21),
    POSE_6DOF(28),
    STATIONARY_DETECT(29),
    MOTION_DETECT(30),
    HEART_BEAT(31),
    ADDITIONAL_INFO(33),
    LOW_LATENCY_OFFBODY_DETECT(34),
    ACCELEROMETER_UNCALIBRATED(35),
    HINGE_ANGLE(36)
}