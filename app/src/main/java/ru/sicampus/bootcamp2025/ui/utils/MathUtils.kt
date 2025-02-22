package ru.sicampus.bootcamp2025.ui.utils

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

fun distanceBetweenTwoPoints(x0: Double, y0: Double, x1: Double, y1: Double): Double {
    return 111.2 * 1000 * acos(sin(x0) * sin(x1) + cos(x0) * cos(x1) * cos(y1 - y0))
}