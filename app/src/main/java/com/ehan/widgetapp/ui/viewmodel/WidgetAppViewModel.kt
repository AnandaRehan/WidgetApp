package com.ehan.widgetapp.ui.viewmodel

import android.app.Application
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ehan.widgetapp.data.AppDatabase
import com.ehan.widgetapp.data.model.AppWidgetEntity
import com.ehan.widgetapp.data.model.InstalledAppInfo
import com.ehan.widgetapp.data.repository.AppRepository
import com.ehan.widgetapp.data.repository.SettingsRepository
import com.ehan.widgetapp.widget.WidgetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WidgetAppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.appWidgetDao()
    private val appRepository = AppRepository(application)
    private val settingsRepository = SettingsRepository(application)

    // Flow of created widgets from Room Database
    val widgets: StateFlow<List<AppWidgetEntity>> = dao.getAllWidgets().let { flow ->
        val state = MutableStateFlow<List<AppWidgetEntity>>(emptyList())
        viewModelScope.launch {
            flow.collectLatest { list ->
                state.value = list
            }
        }
        state.asStateFlow()
    }

    // Theme Mode
    val themeMode: StateFlow<String> = settingsRepository.themeMode

    // Toast / User Notifications
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Dialog & Sheet States
    private val _showAddChoiceDialog = MutableStateFlow(false)
    val showAddChoiceDialog: StateFlow<Boolean> = _showAddChoiceDialog.asStateFlow()

    private val _showInstalledAppsSheet = MutableStateFlow(false)
    val showInstalledAppsSheet: StateFlow<Boolean> = _showInstalledAppsSheet.asStateFlow()

    private val _showManualPackageSheet = MutableStateFlow(false)
    val showManualPackageSheet: StateFlow<Boolean> = _showManualPackageSheet.asStateFlow()

    private val _showCustomizeDialog = MutableStateFlow(false)
    val showCustomizeDialog: StateFlow<Boolean> = _showCustomizeDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    // Installed Apps Search
    private val _appSearchQuery = MutableStateFlow("")
    val appSearchQuery: StateFlow<String> = _appSearchQuery.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    // Manual Package Input
    private val _manualPackageInput = MutableStateFlow("")
    val manualPackageInput: StateFlow<String> = _manualPackageInput.asStateFlow()

    private val _isManualPackageInstalled = MutableStateFlow<Boolean?>(null)
    val isManualPackageInstalled: StateFlow<Boolean?> = _isManualPackageInstalled.asStateFlow()

    // Currently Selected or Edited App Widget
    private val _selectedAppInfo = MutableStateFlow<InstalledAppInfo?>(null)
    val selectedAppInfo: StateFlow<InstalledAppInfo?> = _selectedAppInfo.asStateFlow()

    private val _customWidgetName = MutableStateFlow("")
    val customWidgetName: StateFlow<String> = _customWidgetName.asStateFlow()

    private val _customIconType = MutableStateFlow("DEFAULT") // "DEFAULT", "COLOR", "EMOJI"
    val customIconType: StateFlow<String> = _customIconType.asStateFlow()

    private val _customIconColor = MutableStateFlow("#4F46E5")
    val customIconColor: StateFlow<String> = _customIconColor.asStateFlow()

    private val _customIconEmoji = MutableStateFlow("📱")
    val customIconEmoji: StateFlow<String> = _customIconEmoji.asStateFlow()

    private val _customIconShape = MutableStateFlow("SQUIRCLE") // "CIRCLE", "SQUIRCLE", "ROUNDED_SQUARE"
    val customIconShape: StateFlow<String> = _customIconShape.asStateFlow()

    private val _editingWidgetId = MutableStateFlow<Long?>(null)

    init {
        loadInstalledApps()
    }

    // Installed Apps Loading & Filtering
    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingApps.value = true
            val apps = appRepository.getInstalledApps(_appSearchQuery.value)
            _installedApps.value = apps
            _isLoadingApps.value = false
        }
    }

    fun onSearchQueryChanged(query: String) {
        _appSearchQuery.value = query
        loadInstalledApps()
    }

    // Add Choice Dialog Triggers
    fun openAddChoiceDialog() {
        _showAddChoiceDialog.value = true
    }

    fun closeAddChoiceDialog() {
        _showAddChoiceDialog.value = false
    }

    fun selectAddFromInstalledApps() {
        closeAddChoiceDialog()
        _appSearchQuery.value = ""
        loadInstalledApps()
        _showInstalledAppsSheet.value = true
    }

    fun closeInstalledAppsSheet() {
        _showInstalledAppsSheet.value = false
    }

    fun selectAddFromManualInput() {
        closeAddChoiceDialog()
        _manualPackageInput.value = ""
        _isManualPackageInstalled.value = null
        _showManualPackageSheet.value = true
    }

    fun closeManualPackageSheet() {
        _showManualPackageSheet.value = false
    }

    // Manual Package Handling
    fun onManualPackageInputChanged(input: String) {
        _manualPackageInput.value = input.trim()
        if (input.isNotBlank()) {
            _isManualPackageInstalled.value = appRepository.isPackageInstalled(input.trim())
        } else {
            _isManualPackageInstalled.value = null
        }
    }

    fun confirmManualPackage() {
        val packageName = _manualPackageInput.value.trim()
        if (packageName.isBlank()) {
            emitUserMessage("Harap masukkan nama paket yang valid!")
            return
        }

        val appName = appRepository.getAppNameForPackage(packageName)
        val appIcon = appRepository.getAppIconForPackage(packageName)

        val appInfo = InstalledAppInfo(
            appName = appName,
            packageName = packageName,
            icon = appIcon
        )

        _selectedAppInfo.value = appInfo
        _customWidgetName.value = appName
        _customIconType.value = "DEFAULT"
        _customIconColor.value = "#4F46E5"
        _customIconEmoji.value = "📱"
        _customIconShape.value = "SQUIRCLE"
        _editingWidgetId.value = null

        closeManualPackageSheet()
        _showCustomizeDialog.value = true
    }

    // App Selection from list
    fun selectAppForCustomization(appInfo: InstalledAppInfo) {
        _selectedAppInfo.value = appInfo
        _customWidgetName.value = appInfo.appName
        _customIconType.value = "DEFAULT"
        _customIconColor.value = "#4F46E5"
        _customIconEmoji.value = "📱"
        _customIconShape.value = "SQUIRCLE"
        _editingWidgetId.value = null

        closeInstalledAppsSheet()
        _showCustomizeDialog.value = true
    }

    // Editing Existing Saved Widget
    fun startEditWidget(widget: AppWidgetEntity) {
        val iconDrawable = appRepository.getAppIconForPackage(widget.packageName)
        val appInfo = InstalledAppInfo(
            appName = widget.originalName,
            packageName = widget.packageName,
            icon = iconDrawable
        )

        _selectedAppInfo.value = appInfo
        _customWidgetName.value = widget.customName
        _customIconType.value = widget.iconType
        _customIconColor.value = widget.iconColorHex
        _customIconEmoji.value = widget.iconEmoji
        _customIconShape.value = widget.iconShape
        _editingWidgetId.value = widget.id

        _showCustomizeDialog.value = true
    }

    fun closeCustomizeDialog() {
        _showCustomizeDialog.value = false
    }

    // Customization Setters
    fun setCustomWidgetName(name: String) {
        _customWidgetName.value = name
    }

    fun setCustomIconType(type: String) {
        _customIconType.value = type
    }

    fun setCustomIconColor(colorHex: String) {
        _customIconColor.value = colorHex
    }

    fun setCustomIconEmoji(emoji: String) {
        _customIconEmoji.value = emoji
    }

    fun setCustomIconShape(shape: String) {
        _customIconShape.value = shape
    }

    // Save Widget to Room Database
    fun saveWidget() {
        val appInfo = _selectedAppInfo.value ?: return
        val name = _customWidgetName.value.ifBlank { appInfo.appName }

        viewModelScope.launch(Dispatchers.IO) {
            val entity = AppWidgetEntity(
                id = _editingWidgetId.value ?: 0L,
                customName = name,
                originalName = appInfo.appName,
                packageName = appInfo.packageName,
                iconType = _customIconType.value,
                iconColorHex = _customIconColor.value,
                iconEmoji = _customIconEmoji.value,
                iconShape = _customIconShape.value
            )

            if (_editingWidgetId.value != null && _editingWidgetId.value!! > 0L) {
                dao.updateWidget(entity)
                emitUserMessage("Widget '${name}' berhasil diperbarui!")
            } else {
                dao.insertWidget(entity)
                emitUserMessage("Widget '${name}' berhasil disimpan!")
            }

            _showCustomizeDialog.value = false
        }
    }

    // Save and Pin directly to Home Screen Shortcut
    fun saveAndPinShortcutToHomeScreen() {
        val appInfo = _selectedAppInfo.value ?: return
        val name = _customWidgetName.value.ifBlank { appInfo.appName }

        viewModelScope.launch(Dispatchers.IO) {
            val entity = AppWidgetEntity(
                id = _editingWidgetId.value ?: 0L,
                customName = name,
                originalName = appInfo.appName,
                packageName = appInfo.packageName,
                iconType = _customIconType.value,
                iconColorHex = _customIconColor.value,
                iconEmoji = _customIconEmoji.value,
                iconShape = _customIconShape.value
            )

            val newId = if (_editingWidgetId.value != null && _editingWidgetId.value!! > 0L) {
                dao.updateWidget(entity)
                _editingWidgetId.value!!
            } else {
                dao.insertWidget(entity)
            }

            val savedEntity = entity.copy(id = newId)
            val iconDrawable = appInfo.icon ?: appRepository.getAppIconForPackage(appInfo.packageName)

            val success = WidgetManager.pinShortcutToHomeScreen(
                getApplication(),
                savedEntity,
                iconDrawable
            )

            if (success) {
                emitUserMessage("Permintaan membuat pemintas '${name}' terkirim ke Layar Utama!")
            } else {
                emitUserMessage("Widget disimpan. Peluncur tidak mendukung pin otomatis.")
            }

            _showCustomizeDialog.value = false
        }
    }

    fun pinExistingWidgetToHomeScreen(widget: AppWidgetEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val iconDrawable = appRepository.getAppIconForPackage(widget.packageName)
            val success = WidgetManager.pinShortcutToHomeScreen(
                getApplication(),
                widget,
                iconDrawable
            )
            if (success) {
                emitUserMessage("Permintaan pemintas '${widget.customName}' dikirim ke Layar Utama!")
            } else {
                emitUserMessage("Gagal memasang pemintas otomatis.")
            }
        }
    }

    // Delete Widget
    fun deleteWidget(widget: AppWidgetEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteWidget(widget)
            emitUserMessage("Widget '${widget.customName}' telah dihapus")
        }
    }

    // Launch App Directly
    fun testLaunchApp(packageName: String): Boolean {
        val launched = appRepository.launchApp(packageName)
        if (!launched) {
            emitUserMessage("Aplikasi '$packageName' tidak dapat dibuka atau belum terpasang.")
        }
        return launched
    }

    fun getAppIconDrawable(packageName: String): Drawable? {
        return appRepository.getAppIconForPackage(packageName)
    }

    // Settings Dialog
    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun setThemeMode(mode: String) {
        settingsRepository.setThemeMode(mode)
    }

    private fun emitUserMessage(msg: String) {
        viewModelScope.launch {
            _userMessage.emit(msg)
        }
    }
}
