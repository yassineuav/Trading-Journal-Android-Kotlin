package com.yassine.journaltrading

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.firebase.database.FirebaseDatabase
import com.yassine.journaltrading.data.Goal

class MyWidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return MyRemoteViewsFactory(this.applicationContext)
    }
}

class MyRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var goalList: List<Goal> = emptyList()

    override fun onCreate() {

    }

    override fun onDataSetChanged() {

        Log.d("GoalRemoteViewsFactory", "Fetching data from Firebase")
        val databaseReference = FirebaseDatabase.getInstance().getReference("goals")
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            val updatedList = mutableListOf<Goal>()
            for (snapshot in dataSnapshot.children) {
                val goal = snapshot.getValue(Goal::class.java)
                if (goal != null) {
                    updatedList.add(goal)
                }
            }
            goalList = updatedList
            Log.d("GoalRemoteViewsFactory", "Data fetched: ${goalList.size} items")
        }.addOnFailureListener { e ->
            Log.e("GoalRemoteViewsFactory", "Error fetching data: ", e)
        }

    }

    override fun getCount(): Int = goalList.size

    @SuppressLint("RemoteViewLayout")
    override fun getViewAt(position: Int): RemoteViews? {

        if (position >= goalList.size) return null
        val goal = goalList[position]
        val rv = RemoteViews(context.packageName, R.layout.item_goal_progress_ring)

        rv.setTextViewText(R.id.currentBalance , formatCurrency(goal.currentBalance))
        rv.setTextViewText(R.id.targetBalance, formatCurrency(goal.endBalance))
        val percent = ((goal.currentBalance / goal.endBalance) * 100).toInt()
        rv.setProgressBar(R.id.circularProgressBar, 100, percent, false)
        Log.d("GoalRemoteViewsFactory", "percent: $percent")
        // Set up the click intent for the item
//        val fillInIntent = Intent()
//        rv.setOnClickFillInIntent(R.id.itemContainer, fillInIntent)
        return rv

    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    override fun onDestroy() {
        goalList = emptyList()
    }
    private fun formatCurrency(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format("$%.0fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("$%.0fK", amount / 1_000)
            else -> String.format("$%.0f", amount)
        }
    }
}