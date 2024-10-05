package com.yassine.journaltrading

import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yassine.journaltrading.data.Goal
import com.yassine.journaltrading.data.TradeHistory
import com.yassine.journaltrading.ui.goals.GoalsAdapter
import com.yassine.journaltrading.ui.goals.GoalsAdapterRing
import com.yassine.journaltrading.ui.home.TradeAdapter
import java.text.DecimalFormat

@BindingAdapter("goals")
fun bindGoals(recyclerView: RecyclerView, goals: List<Goal>?) {
    goals?.let {
        val adapter = recyclerView.adapter as GoalsAdapter
        adapter.updateGoals(it)
    }
}

@BindingAdapter("goalsRing")
fun bindGoalsRing(recyclerView: RecyclerView, goals: List<Goal>?) {
    goals?.let {
        val adapter = recyclerView.adapter as GoalsAdapterRing
        adapter.updateGoals(it)
    }
}

@BindingAdapter("trade")
fun setPercentageAndColor(textView: TextView, trade: TradeHistory) {
    // Calculate the percentage
    val percentage = (trade.profitOrLoss / trade.margin) * 100
    val formattedPercentage = if (trade.status.equals("Win", ignoreCase = true)) {
        String.format("+%.0f%%", percentage)
    } else {
        String.format("%.0f%%", percentage)
    }

    // Format the profit or loss text with a thousands separator
    val formattedProfitOrLoss = if (trade.status.equals("Win", ignoreCase = true)) {
        String.format("+$%,.0f", trade.profitOrLoss)
    } else {
        String.format("-$%,.0f", trade.profitOrLoss)
    }

    // Combine both formatted values into the final text
    val finalText = "$formattedProfitOrLoss   $formattedPercentage"

    // Set the text with the calculated profit or loss and percentage
    textView.text = finalText
    // Set the color based on the status
    val colorResId = if (trade.status.equals("Win", ignoreCase = true)) {
        R.color.green // Replace with your actual green color resource
    } else {
        R.color.red // Replace with your actual red color resource
    }
    textView.setTextColor(ContextCompat.getColor(textView.context, colorResId))
}

@BindingAdapter("trades")
fun bindTrades(recyclerView: RecyclerView, trades: List<TradeHistory>?) {
    trades?.let {
        val adapter = recyclerView.adapter as? TradeAdapter
        adapter?.updateTrades(it)
    }
}

@BindingAdapter("totalProfitOrLoss")
fun setTotalProfitOrLoss(textView: TextView, totalProfitOrLoss: String) {
    textView.text = totalProfitOrLoss
}


@BindingAdapter("progressFromBalances")
fun setProgressFromBalances(progressBar: ProgressBar, goal: Goal) {
    val currentBalance = goal.currentBalance
    val endBalance = goal.endBalance

    val progressPercentage = if (currentBalance != 0.0) {
        ((currentBalance / endBalance ) * 100).toInt()
    } else {
        0
    }

    progressBar.max = 100
    progressBar.progress = progressPercentage
}

@BindingAdapter("formattedCurrency")
fun setFormattedCurrency(textView: TextView, amount: Double?) {
    val formattedAmount = when {
        amount == null -> ""
        amount >= 1_000_000 -> "$${DecimalFormat("#,##0.#").format(amount / 1_000_000)}M"
        amount >= 1_000 -> "$${DecimalFormat("#,##0.#").format(amount / 1_000)}K"
        else -> "$${DecimalFormat("#,##0.##").format(amount)}"
    }
    textView.text = formattedAmount
}


