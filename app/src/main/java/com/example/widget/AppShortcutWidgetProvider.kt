package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.widget.RemoteViews
import android.widget.Toast
import com.example.MainActivity
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.model.CustomWidget
import com.example.utils.AppListManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppShortcutWidgetProvider : AppWidgetProvider() {

    companion object {
        const val EXTRA_CUSTOM_WIDGET_ID = "extra_custom_widget_id"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val ACTION_LAUNCH_APP = "com.example.widget.ACTION_LAUNCH_APP"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            customWidget: CustomWidget?
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_app_shortcut)

            if (customWidget != null) {
                // Set Label
                views.setTextViewText(R.id.widget_label, customWidget.displayName)
                try {
                    val textColor = Color.parseColor(customWidget.textColorHex)
                    views.setTextColor(R.id.widget_label, textColor)
                } catch (e: Exception) {
                    views.setTextColor(R.id.widget_label, Color.WHITE)
                }

                // Render Custom Icon Bitmap
                val iconBitmap = WidgetHelper.getWidgetIconBitmap(context, customWidget)
                if (iconBitmap != null) {
                    views.setImageViewBitmap(R.id.widget_icon, iconBitmap)
                } else {
                    views.setImageViewResource(R.id.widget_icon, R.mipmap.ic_launcher)
                }

                // Launch Intent when clicked
                val launchIntent = Intent(context, AppShortcutWidgetProvider::class.java).apply {
                    action = ACTION_LAUNCH_APP
                    putExtra(EXTRA_PACKAGE_NAME, customWidget.packageName)
                    putExtra(EXTRA_CUSTOM_WIDGET_ID, customWidget.id)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            } else {
                // Default Widget view
                views.setTextViewText(R.id.widget_label, "Widget App")
                views.setImageViewResource(R.id.widget_icon, R.mipmap.ic_launcher)

                val openAppIntent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    openAppIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val widgets = db.customWidgetDao().getAllWidgets()
                // Fetch first widget or default
                val firstWidget = db.customWidgetDao().getWidgetById(1)
                for (id in appWidgetIds) {
                    updateWidget(context, appWidgetManager, id, firstWidget)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_LAUNCH_APP) {
            val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
            if (!packageName.isNullOrBlank()) {
                val pm = context.packageManager
                val launchIntent = pm.getLaunchIntentForPackage(packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                } else {
                    Toast.makeText(
                        context,
                        "Aplikasi tidak ditemukan ($packageName). Buka Widget App untuk edit.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Open WidgetApp if target not found
                    val mainIntent = Intent(context, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(mainIntent)
                }
            }
        }
    }
}
