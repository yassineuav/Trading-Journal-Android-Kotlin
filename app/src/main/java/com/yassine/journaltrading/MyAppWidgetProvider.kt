package com.yassine.journaltrading

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.google.firebase.database.FirebaseDatabase
import com.yassine.journaltrading.data.Goal

class MyAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId ->
            val rv = RemoteViews(context.packageName, R.layout.widget_layout)
            // Set the RemoteViewsService intent
            val intent = Intent(context, MyWidgetRemoteViewsService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }

            rv.setRemoteAdapter(R.id.widgetGridView , intent)

            setupRefreshButton(context, rv)
            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "REFRESH_WIDGET") {
            Log.d("MyAppWidgetProvider", "Refresh action received")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, MyAppWidgetProvider::class.java))
            appWidgetIds.forEach { appWidgetId ->
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widgetGridView)
                Log.d("MyAppWidgetProvider", "DataSetChanged notified for widget ID: $appWidgetId")
            }

        }
    }



    private fun setupRefreshButton(context: Context, rv: RemoteViews) {
        val refreshIntent = Intent(context, MyAppWidgetProvider::class.java).apply {
            action = "REFRESH_WIDGET"
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        rv.setOnClickPendingIntent(R.id.refreshButton, refreshPendingIntent)

    }


}