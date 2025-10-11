package com.faithfulstreak.app.v1.util

import kotlin.math.ceil

fun ceil5(x: Double): Int {
    return (ceil(x / 5.0) * 5).toInt()
}

fun nextThreeTargets(current: Int, ratio: Double = 0.2, minStep: Int = 5): List<Int> {
    fun step(t: Int): Int {
        val rawStep = t * ratio
        return ceil5(maxOf(minStep.toDouble(), rawStep))
    }

    val targets = mutableListOf<Int>()
    var cur = current

    repeat(3) {
        val s = step(cur)
        var next = ceil5((cur + s).toDouble())
        if (next <= cur) next = cur + 5
        targets.add(next)
        cur = next
    }

    return targets
}