package com.yassine.journaltrading.ui.goals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yassine.journaltrading.databinding.ItemGoalBinding
import com.yassine.journaltrading.data.Goal


class GoalsAdapter : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

    private var goals = listOf<Goal>()

    fun updateGoals(newGoals: List<Goal>) {
        goals = newGoals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGoalBinding.inflate(inflater, parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(goals[position])
    }

    override fun getItemCount(): Int = goals.size

    class GoalViewHolder(private val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: Goal) {
            binding.goal = goal
            binding.executePendingBindings()
        }
    }
}