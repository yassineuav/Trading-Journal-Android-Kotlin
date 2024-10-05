package com.yassine.journaltrading.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yassine.journaltrading.data.Goal

class GoalViewModel : ViewModel() {

    private val _goals = MutableLiveData<List<Goal>>()
    val goals: LiveData<List<Goal>> get() = _goals

    private val database = FirebaseDatabase.getInstance().getReference("goals")

    init {
        fetchGoals()
    }

    private fun fetchGoals() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val goalsList = mutableListOf<Goal>()
                snapshot.children.forEach {
                    val goal = it.getValue(Goal::class.java)
                    if (goal != null) {
                        goalsList.add(goal)
                    }
                }
                _goals.value = goalsList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })
    }

    fun addGoal(goal: Goal) {
        val goalId = database.push().key ?: return
        goal.id = goalId
        database.child(goalId).setValue(goal)
    }
}