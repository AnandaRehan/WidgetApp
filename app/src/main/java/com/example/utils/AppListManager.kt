package com.example.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.example.data.model.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AppListManager {

    suspend fun getInstalledApps(context: Context): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        val appList = mutableListOf<InstalledApp>()

        val seenPackages = mutableSetOf<String>()

        for (ri in resolveInfos) {
            val packageName = ri.activityInfo.packageName
            if (!seenPackages.contains(packageName)) {
                seenPackages.add(packageName)
                val label = ri.loadLabel(pm).toString()
                val icon = try {
                    ri.loadIcon(pm)
                } catch (e: Exception) {
                    pm.defaultActivityIcon
                }
                val isSystem = try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                } catch (e: Exception) {
                    false
                }

                appList.add(
                    InstalledApp(
                        label = label,
                        packageName = packageName,
                        icon = icon,
                        isSystemApp = isSystem
                    )
                )
            }
        }

        // Sort non-system apps first, then by label alphabetically
        appList.sortedWith(
            compareBy<InstalledApp> { it.isSystemApp }
                .thenBy { it.label.lowercase() }
        )
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return drawable.bitmap
        }

        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
