package com.example.data.model

import android.graphics.drawable.Drawable

data class InstalledApp(
    val label: String,
    val packageName: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false
)
