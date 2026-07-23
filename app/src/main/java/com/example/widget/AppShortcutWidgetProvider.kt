package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.Toast
import com.example.MainActivity
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.model.CustomWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AppShortcutWidgetProvider : AppWidgetProvider() {

    companion object {
        const val EXTRA_CUSTOM_WIDGET_ID = "extra_custom_widget_id"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val ACTION_LAUNCH_APP = "com.example.widget.ACTION_LAUNCH_APP"
        const val ACTION_WIDGET_PINNED = "com.example.widget.ACTION_WIDGET_PINNED"
        private const val PREFS_NAME = "widget_prefs"

        fun saveLatestPinWidgetId(context: Context, customWidgetId: Long) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putLong("latest_pin_widget_id", customWidgetId).apply()
        }

        fun saveAppWidgetMapping(context: Context, appWidgetId: Int, customWidgetId: Long) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putLong("app_widget_$appWidgetId", customWidgetId).apply()
        }

        fun getMappedCustomWidgetId(context: Context, appWidgetId: Int): Long {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getLong("app_widget_$appWidgetId", 0L)
        }

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
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId, // Unique request code per appWidgetId
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

        fun notifyAllWidgetsUpdate(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val providerComponent = ComponentName(context, AppShortcutWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(providerComponent)
            if (appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, AppShortcutWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val latestPinId = prefs.getLong("latest_pin_widget_id", 0L)

                for (appWidgetId in appWidgetIds) {
                    var targetCustomWidgetId = prefs.getLong("app_widget_$appWidgetId", 0L)
                    if (targetCustomWidgetId == 0L && latestPinId != 0L) {
                        targetCustomWidgetId = latestPinId
                        prefs.edit().putLong("app_widget_$appWidgetId", targetCustomWidgetId).apply()
                    }

                    val customWidget = if (targetCustomWidgetId != 0L) {
                        db.customWidgetDao().getWidgetById(targetCustomWidgetId)
                    } else {
                        db.customWidgetDao().getAllWidgets().firstOrNull()?.firstOrNull()
                    }

                    updateWidget(context, appWidgetManager, appWidgetId, customWidget)
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

        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val customWidgetId = intent.getLongExtra(EXTRA_CUSTOM_WIDGET_ID, 0L)

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && customWidgetId != 0L) {
            saveAppWidgetMapping(context, appWidgetId, customWidgetId)
        }

        if (intent.action == ACTION_WIDGET_PINNED) {
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val db = AppDatabase.getDatabase(context)
                        val targetId = if (customWidgetId != 0L) customWidgetId else getMappedCustomWidgetId(context, appWidgetId)
                        val customWidget = if (targetId != 0L) db.customWidgetDao().getWidgetById(targetId) else null
                        updateWidget(context, appWidgetManager, appWidgetId, customWidget)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        } else if (intent.action == ACTION_LAUNCH_APP) {
            var packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

            // If package name wasn't passed directly, check mapped custom widget from DB
            if (packageName.isNullOrBlank() && appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val mappedId = getMappedCustomWidgetId(context, appWidgetId)
                if (mappedId != 0L) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val db = AppDatabase.getDatabase(context)
                            val widget = db.customWidgetDao().getWidgetById(mappedId)
                            if (widget != null) {
                                launchAppPackage(context, widget.packageName)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            pendingResult.finish()
                        }
                    }
                    return
                }
            }

            if (!packageName.isNullOrBlank()) {
                launchAppPackage(context, packageName)
            }
        }
    }

    private fun launchAppPackage(context: Context, packageName: String) {
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

            val mainIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(mainIntent)
        }
    }
}
