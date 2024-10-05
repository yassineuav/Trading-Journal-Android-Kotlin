package com.yassine.journaltrading.data

data class Goal(
    var id: String = "",
    val week :Int = 1,
    val startBalance: Double = 0.0,
    val endBalance: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    var currentBalance: Double = 0.0
)