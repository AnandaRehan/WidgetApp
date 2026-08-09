package com.ehan.widgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ehan.widgetapp.ui.screens.AppSelectionDialog
import com.ehan.widgetapp.ui.screens.HomeScreen
import com.ehan.widgetapp.ui.screens.InstalledAppsSheet
import com.ehan.widgetapp.ui.screens.ManualPackageSheet
import com.ehan.widgetapp.ui.screens.SettingsDialog
import com.ehan.widgetapp.ui.screens.WidgetCustomizeDialog
import com.ehan.widgetapp.ui.theme.WidgetAppTheme
import com.ehan.widgetapp.ui.viewmodel.WidgetAppViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WidgetAppViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val widgets by viewModel.widgets.collectAsStateWithLifecycle()

            val showAddChoiceDialog by viewModel.showAddChoiceDialog.collectAsStateWithLifecycle()
            val showInstalledAppsSheet by viewModel.showInstalledAppsSheet.collectAsStateWithLifecycle()
            val showManualPackageSheet by viewModel.showManualPackageSheet.collectAsStateWithLifecycle()
            val showCustomizeDialog by viewModel.showCustomizeDialog.collectAsStateWithLifecycle()
            val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()

            val appSearchQuery by viewModel.appSearchQuery.collectAsStateWithLifecycle()
            val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
            val isLoadingApps by viewModel.isLoadingApps.collectAsStateWithLifecycle()

            val manualPackageInput by viewModel.manualPackageInput.collectAsStateWithLifecycle()
            val isManualPackageInstalled by viewModel.isManualPackageInstalled.collectAsStateWithLifecycle()

            val selectedAppInfo by viewModel.selectedAppInfo.collectAsStateWithLifecycle()
            val customWidgetName by viewModel.customWidgetName.collectAsStateWithLifecycle()
            val customIconType by viewModel.customIconType.collectAsStateWithLifecycle()
            val customIconColor by viewModel.customIconColor.collectAsStateWithLifecycle()
            val customIconEmoji by viewModel.customIconEmoji.collectAsStateWithLifecycle()
            val customIconShape by viewModel.customIconShape.collectAsStateWithLifecycle()

            var userMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                viewModel.userMessage.collect { msg ->
                    userMessage = msg
                }
            }

            WidgetAppTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(
                        widgets = widgets,
                        themeMode = themeMode,
                        onAddWidgetClick = { viewModel.openAddChoiceDialog() },
                        onLaunchApp = { pkg -> viewModel.testLaunchApp(pkg) },
                        onPinWidgetToHome = { widget -> viewModel.pinExistingWidgetToHomeScreen(widget) },
                        onEditWidget = { widget -> viewModel.startEditWidget(widget) },
                        onDeleteWidget = { widget -> viewModel.deleteWidget(widget) },
                        onOpenSettings = { viewModel.openSettingsDialog() },
                        getAppIconDrawable = { pkg -> viewModel.getAppIconDrawable(pkg) },
                        userMessageEvent = userMessage
                    )

                    // Pop up choice between "Daftar Aplikasi" or "Input Manual"
                    if (showAddChoiceDialog) {
                        AppSelectionDialog(
                            onDismiss = { viewModel.closeAddChoiceDialog() },
                            onSelectFromInstalled = { viewModel.selectAddFromInstalledApps() },
                            onSelectManualInput = { viewModel.selectAddFromManualInput() }
                        )
                    }

                    // Installed Apps Sheet (with search searching app name AND package name)
                    if (showInstalledAppsSheet) {
                        InstalledAppsSheet(
                            searchQuery = appSearchQuery,
                            onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                            installedApps = installedApps,
                            isLoading = isLoadingApps,
                            onSelectApp = { app -> viewModel.selectAppForCustomization(app) },
                            onDismiss = { viewModel.closeInstalledAppsSheet() }
                        )
                    }

                    // Manual Package Input Sheet
                    if (showManualPackageSheet) {
                        ManualPackageSheet(
                            packageInput = manualPackageInput,
                            onPackageInputChanged = { viewModel.onManualPackageInputChanged(it) },
                            isInstalled = isManualPackageInstalled,
                            onTestLaunch = { viewModel.testLaunchApp(manualPackageInput) },
                            onConfirm = { viewModel.confirmManualPackage() },
                            onDismiss = { viewModel.closeManualPackageSheet() }
                        )
                    }

                    // Customization Sheet/Dialog
                    if (showCustomizeDialog && selectedAppInfo != null) {
                        WidgetCustomizeDialog(
                            appInfo = selectedAppInfo!!,
                            customName = customWidgetName,
                            onCustomNameChanged = { viewModel.setCustomWidgetName(it) },
                            iconType = customIconType,
                            onIconTypeChanged = { viewModel.setCustomIconType(it) },
                            iconColorHex = customIconColor,
                            onIconColorChanged = { viewModel.setCustomIconColor(it) },
                            iconEmoji = customIconEmoji,
                            onIconEmojiChanged = { viewModel.setCustomIconEmoji(it) },
                            iconShape = customIconShape,
                            onIconShapeChanged = { viewModel.setCustomIconShape(it) },
                            onSave = { viewModel.saveWidget() },
                            onSaveAndPin = { viewModel.saveAndPinShortcutToHomeScreen() },
                            onDismiss = { viewModel.closeCustomizeDialog() }
                        )
                    }

                    // Settings Dialog (Dark/Light/System theme)
                    if (showSettingsDialog) {
                        SettingsDialog(
                            currentTheme = themeMode,
                            onThemeSelected = { mode -> viewModel.setThemeMode(mode) },
                            onDismiss = { viewModel.closeSettingsDialog() }
                        )
                    }
                }
            }
        }
    }
}
