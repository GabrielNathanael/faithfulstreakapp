package com.faithfulstreak.app.v1.model

import java.time.LocalDate

data class StreakState(
    val count: Int = 0,
    val lastCheckIn: LocalDate? = null,
    val currentTarget: Int = 7
)
