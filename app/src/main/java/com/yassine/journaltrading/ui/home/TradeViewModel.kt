package com.yassine.journaltrading.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yassine.journaltrading.data.TradeHistory

class TradeViewModel() : ViewModel() {

    private val _trades = MutableLiveData<List<TradeHistory>>()
    val trades : LiveData<List<TradeHistory>> get() = _trades

    // LiveData for total profit or loss
    private val _totalProfitOrLoss = MediatorLiveData<String>()
    val totalProfitOrLoss: LiveData<String> get() = _totalProfitOrLoss




    private val database = FirebaseDatabase.getInstance().getReference("TradeHistory")

    init {

        fetchTradeHistories()

        // Update total profit or loss when trades list changes
        _totalProfitOrLoss.addSource(trades) { tradesList ->
            _totalProfitOrLoss.value = calculateTotalProfitOrLoss(tradesList)
        }
    }




    fun deleteTrade(tradeHistory: TradeHistory) {
        database.child(tradeHistory.id).removeValue()
            .addOnSuccessListener {
                // Successfully deleted
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    private fun fetchTradeHistories() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tradesList = mutableListOf<TradeHistory>()
                snapshot.children.forEach {
                    val trade = it.getValue(TradeHistory::class.java)
                    if (trade != null) {
                        tradesList.add(trade)
                    }
                }
                _trades.value = tradesList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }



    // Function to calculate total profit and loss
    private fun calculateTotalProfitOrLoss(trades: List<TradeHistory>): String {
        val totalProfitOrLoss = trades.sumOf {
            when (it.status) {
                "Win" -> (it.profitOrLoss ?: 0.0)
                "Loss" -> -(it.profitOrLoss ?: 0.0)
                else -> 0.0
            }
        }
        val formattedTotal = String.format("$%,.0f", totalProfitOrLoss)
        val sign = if (totalProfitOrLoss >= 0) "+" else ""
        return "$sign$formattedTotal"
    }
}