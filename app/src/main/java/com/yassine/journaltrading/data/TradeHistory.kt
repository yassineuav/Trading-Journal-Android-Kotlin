package com.yassine.journaltrading.data

data class TradeHistory (
    var id: String = "",
    val status: String = "",
    val win: Int ?= 0,
    val loss: Int ?= 0,
    val date: String = "",
    val tradeNumber: Int = 0,
    val margin: Double = 0.0,
    val profitOrLoss: Double = 0.0
)