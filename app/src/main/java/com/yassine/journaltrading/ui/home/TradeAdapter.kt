package com.yassine.journaltrading.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yassine.journaltrading.data.TradeHistory
import com.yassine.journaltrading.databinding.ItemTradeBinding

class TradeAdapter : RecyclerView.Adapter<TradeAdapter.TradeViewHolder>() {

    private var trades = listOf<TradeHistory>()

    fun updateTrades(newTrades: List<TradeHistory>) {
        trades = newTrades
        notifyDataSetChanged()
    }

    fun getTradeAtPosition(position: Int): TradeHistory {
        return trades[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTradeBinding.inflate(inflater, parent, false)
        return TradeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TradeViewHolder, position: Int) {
        holder.bind(trades[position])
    }

    override fun getItemCount(): Int = trades.size

    class TradeViewHolder(private val binding: ItemTradeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trade :TradeHistory) {
            binding.trade = trade
            binding.executePendingBindings()
        }
    }
}