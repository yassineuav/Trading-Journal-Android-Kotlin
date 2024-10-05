package com.yassine.journaltrading.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yassine.journaltrading.data.Goal
import com.yassine.journaltrading.databinding.ItemGoalProgressRingBinding

class GoalsAdapterRing: RecyclerView.Adapter<GoalsAdapterRing.GoalRingViewHolder>() {

    private var goals = listOf<Goal>()

    fun updateGoals(newGoals: List<Goal>) {
        goals = newGoals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalRingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGoalProgressRingBinding.inflate(inflater, parent, false)
        return GoalRingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalRingViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount(): Int = goals.size

    class GoalRingViewHolder(private val binding: ItemGoalProgressRingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: Goal) {
            binding.goal = goal
            binding.executePendingBindings()
        }
    }
}