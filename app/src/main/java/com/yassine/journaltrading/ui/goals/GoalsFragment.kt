package com.yassine.journaltrading.ui.goals

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yassine.journaltrading.R
import com.yassine.journaltrading.data.Goal
import com.yassine.journaltrading.databinding.DialogLayoutBinding
import com.yassine.journaltrading.databinding.FragmentGoalsBinding


class GoalsFragment : Fragment() {

    private lateinit var binding: FragmentGoalsBinding
    private lateinit var viewModel: GoalViewModel
    private val goalsAdapter = GoalsAdapter()
    private  val goalsAdapterProgressRing = GoalsAdapterRing()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


//        binding = FragmentGoalsBinding.inflate(inflater, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_goals, container, false)

        viewModel = ViewModelProvider(this)[GoalViewModel::class.java]

        binding.viewModel = viewModel

        binding.lifecycleOwner = this


        setupRecyclerView()


        binding.addGoal.setOnClickListener {
            showDialog()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewGoals.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalsAdapter
        }
        binding.recyclerViewRingBar.apply {
            layoutManager = LinearLayoutManager(context,  LinearLayoutManager.HORIZONTAL, false)
            adapter = goalsAdapterProgressRing
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog() {
        // Inflate the dialog layout
        val dialogBinding = DialogLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        // Find the buttons and inputs
        val buttonSave = dialogBinding.buttonSave
        val buttonCancel = dialogBinding.buttonCancel
        val inputText1 = dialogBinding.inputTextWeekNumber
        val inputText2 = dialogBinding.inputTextBalanceStart
        val inputText3 = dialogBinding.inputTextBalanceEnd

        buttonSave.setOnClickListener {
            // Handle save action
            val week = inputText1.text.toString().toIntOrNull() ?: 1
            val startBalance = inputText2.text.toString().toDoubleOrNull() ?: 0.0
            val endBalance = inputText3.text.toString().toDoubleOrNull() ?: 0.0
            // Save data or perform action with the input values

            Log.d("GoalsFragment", "Input 1: $week")
            Log.d("GoalsFragment", "Input 2: $startBalance")
            Log.d("GoalsFragment", "Input 3: $endBalance")
//            alertDialog.dismiss()

            val goal = Goal(
                week = week,
                startBalance = startBalance,
                endBalance = endBalance
            )
            addGoal(goal)
        }

        buttonCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun addGoal(goal: Goal) {
        val database = FirebaseDatabase.getInstance().getReference("goals")
        val goalId = database.push().key ?: return
        goal.id = goalId
        database.child(goalId).setValue(goal)
    }
    private fun fetchGoals() {
        val database = FirebaseDatabase.getInstance().getReference("goals")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val goals = mutableListOf<Goal>()
                snapshot.children.forEach {
                    val goal = it.getValue(Goal::class.java)
                    if (goal != null) {
                        goals.add(goal)
                    }
                }
                // Update your RecyclerView with the goals list
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }


//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
}