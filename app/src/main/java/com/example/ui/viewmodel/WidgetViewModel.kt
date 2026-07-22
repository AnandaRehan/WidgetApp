package com.example.ui.viewmodel

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CustomWidget
import com.example.data.model.InstalledApp
import com.example.data.repository.WidgetRepository
import com.example.widget.AppShortcutWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WidgetViewModel(
    private val repository: WidgetRepository
) : ViewModel() {

    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps

    private val _appSearchQuery = MutableStateFlow("")
    val appSearchQuery: StateFlow<String> = _appSearchQuery

    private val _widgetSearchQuery = MutableStateFlow("")
    val widgetSearchQuery: StateFlow<String> = _widgetSearchQuery

    val filteredInstalledApps: StateFlow<List<InstalledApp>> = combine(_installedApps, _appSearchQuery) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            val q = query.lowercase().trim()
            apps.filter { app ->
                app.label.lowercase().contains(q) || app.packageName.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawSavedWidgets: StateFlow<List<CustomWidget>> = repository.allWidgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredSavedWidgets: StateFlow<List<CustomWidget>> = combine(rawSavedWidgets, _widgetSearchQuery) { widgets, query ->
        if (query.isBlank()) {
            widgets
        } else {
            val q = query.lowercase().trim()
            widgets.filter { w ->
                w.displayName.lowercase().contains(q) || w.packageName.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadInstalledApps(context: Context) {
        if (_installedApps.value.isNotEmpty()) return
        viewModelScope.launch {
            _isLoadingApps.value = true
            try {
                val apps = repository.loadInstalledApps(context)
                _installedApps.value = apps
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingApps.value = false
            }
        }
    }

    fun setAppSearchQuery(query: String) {
        _appSearchQuery.value = query
    }

    fun setWidgetSearchQuery(query: String) {
        _widgetSearchQuery.value = query
    }

    fun saveWidget(widget: CustomWidget, onComplete: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val id = repository.saveWidget(widget)
            onComplete?.invoke(id)
        }
    }

    fun deleteWidget(id: Long) {
        viewModelScope.launch {
            repository.deleteWidget(id)
        }
    }

    fun exportBackup(): String {
        return repository.exportBackup(rawSavedWidgets.value)
    }

    fun importBackup(jsonString: String, appendMode: Boolean = true, onResult: (Result<Int>) -> Unit) {
        viewModelScope.launch {
            val res = repository.importBackup(jsonString, appendMode)
            onResult(res)
        }
    }

    fun restorePresetSamples() {
        viewModelScope.launch {
            val presets = com.example.utils.BackupRestoreManager.getPresetSamples()
            repository.restoreWidgets(presets, appendMode = true)
        }
    }

    fun pinWidgetToHomeScreen(context: Context, widget: CustomWidget) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val providerComponent = ComponentName(context, AppShortcutWidgetProvider::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val successIntent = Intent(context, AppShortcutWidgetProvider::class.java).apply {
                    action = AppShortcutWidgetProvider.ACTION_LAUNCH_APP
                    putExtra(AppShortcutWidgetProvider.EXTRA_PACKAGE_NAME, widget.packageName)
                }

                val successPendingIntent = PendingIntent.getBroadcast(
                    context,
                    widget.id.toInt(),
                    successIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                appWidgetManager.requestPinAppWidget(providerComponent, null, successPendingIntent)
                Toast.makeText(context, "Permintaan pin widget '${widget.displayName}' dikirim!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Peluncur layar utama Anda tidak mendukung pendaftaran pin otomatis. Tambahkan manual via menu Widget layar utama Anda.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                "Fitur pin otomatis membutuhkan Android 8.0+. Silakan tambahkan widget manual melalui layar utama ponsel Anda.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    class Factory(private val repository: WidgetRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WidgetViewModel(repository) as T
        }
    }
}
