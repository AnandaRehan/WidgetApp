package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CustomWidget
import com.example.data.model.InstalledApp
import com.example.ui.viewmodel.WidgetViewModel
import com.example.utils.AppListManager
import com.example.widget.WidgetHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWidgetDialog(
    viewModel: WidgetViewModel,
    editingWidget: CustomWidget? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
    }

    val installedApps by viewModel.filteredInstalledApps.collectAsState()
    val rawInstalledApps by viewModel.installedApps.collectAsState()

    var showAppPickerModal by remember { mutableStateOf(false) }
    var showManualInput by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    var displayName by remember { mutableStateOf(editingWidget?.displayName ?: "") }
    var packageName by remember { mutableStateOf(editingWidget?.packageName ?: "") }
    var customIconType by remember { mutableStateOf(editingWidget?.customIconType ?: "SYSTEM_APP") }
    var customIconValue by remember { mutableStateOf(editingWidget?.customIconValue ?: "chat") }
    var backgroundColorHex by remember { mutableStateOf(editingWidget?.backgroundColorHex ?: "#1E293B") }
    var textColorHex by remember { mutableStateOf(editingWidget?.textColorHex ?: "#FFFFFF") }
    var shapeType by remember { mutableStateOf(editingWidget?.shapeType ?: "ROUNDED") }

    val selectedApp = remember(packageName, rawInstalledApps) {
        rawInstalledApps.find { it.packageName == packageName }
    }

    val presetIcons = remember {
        listOf("chat", "video", "browser", "settings", "camera", "store", "game", "music", "social", "star", "rocket", "flame", "shield", "code")
    }

    val colorOptions = remember {
        listOf("#1E293B", "#6366F1", "#10B981", "#EF4444", "#F59E0B", "#8B5CF6", "#06B6D4", "#EC4899", "#000000", "#FFFFFF")
    }

    val shapeOptions = remember {
        listOf("ROUNDED" to "Persegi", "CIRCLE" to "Lingkaran", "SQUIRCLE" to "Squircle", "GLASS" to "Kaca")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (editingWidget == null) "Buat Widget Studio" else "Edit Widget",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // LIVE PREVIEW CARD
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PREVIEW TAMPILAN WIDGET",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val sampleWidget = CustomWidget(
                                displayName = if (displayName.isBlank()) "Aplikasi" else displayName,
                                packageName = if (packageName.isBlank()) "com.example.app" else packageName,
                                customIconType = customIconType,
                                customIconValue = customIconValue,
                                backgroundColorHex = backgroundColorHex,
                                textColorHex = textColorHex,
                                shapeType = shapeType
                            )

                            val iconBitmap = remember(sampleWidget) {
                                WidgetHelper.getWidgetIconBitmap(context, sampleWidget)
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .width(96.dp)
                                    .padding(8.dp)
                            ) {
                                if (iconBitmap != null) {
                                    Image(
                                        bitmap = iconBitmap.asImageBitmap(),
                                        contentDescription = "Preview Icon",
                                        modifier = Modifier.size(56.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .background(Color.Gray, CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (displayName.isBlank()) "Aplikasi" else displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = try {
                                        Color(android.graphics.Color.parseColor(textColorHex))
                                    } catch (e: Exception) {
                                        Color.White
                                    },
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    // SECTION 1: SELEKSI APLIKASI TARGET
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "1. Pilih Aplikasi Target",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (packageName.isNotBlank()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val iconBitmap = remember(selectedApp) {
                                        selectedApp?.icon?.let { AppListManager.drawableToBitmap(it) }
                                    }

                                    if (iconBitmap != null) {
                                        Image(
                                            bitmap = iconBitmap.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Smartphone, contentDescription = null, tint = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = selectedApp?.label ?: displayName.ifBlank { "Aplikasi Terpilih" },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = packageName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = { showAppPickerModal = true },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Ganti App", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showAppPickerModal = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pilih dari Daftar App", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }

                                OutlinedButton(
                                    onClick = { showManualInput = !showManualInput },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Input Manual", fontSize = 12.sp)
                                }
                            }

                            if (showManualInput) {
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = packageName,
                                    onValueChange = { packageName = it },
                                    label = { Text("Nama Paket (Package Name)") },
                                    placeholder = { Text("contoh: com.whatsapp") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_package_input"),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    // SECTION 2: KUSTOMISASI
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "2. Kustomisasi Tampilan Widget",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Display Name Input
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                label = { Text("Nama Tampilan Widget") },
                                placeholder = { Text("misal: Pintasan Chat") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_display_name_input"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Icon Type
                            Column {
                                Text(
                                    text = "Gaya Ikon:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(
                                        selected = customIconType == "SYSTEM_APP",
                                        onClick = { customIconType = "SYSTEM_APP" },
                                        label = { Text("Ikon Asli App") }
                                    )
                                    FilterChip(
                                        selected = customIconType == "PRESET_VECTOR",
                                        onClick = { customIconType = "PRESET_VECTOR" },
                                        label = { Text("Ikon Simbol Custom") }
                                    )
                                }

                                if (customIconType == "PRESET_VECTOR") {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(presetIcons) { iconKey ->
                                            val symbol = WidgetHelper.getSymbolForPreset(iconKey)
                                            val isSel = customIconValue == iconKey
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                                    )
                                                    .clickable { customIconValue = iconKey },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = symbol, fontSize = 18.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            // Shape Type
                            Column {
                                Text(
                                    text = "Bentuk Widget:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(shapeOptions) { (key, label) ->
                                        FilterChip(
                                            selected = shapeType == key,
                                            onClick = { shapeType = key },
                                            label = { Text(label) }
                                        )
                                    }
                                }
                            }

                            // Color Choice
                            Column {
                                Text(
                                    text = "Warna Latar Belakang:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(colorOptions) { hex ->
                                        val isSel = backgroundColorHex.equals(hex, ignoreCase = true)
                                        val color = try {
                                            Color(android.graphics.Color.parseColor(hex))
                                        } catch (e: Exception) {
                                            Color.Gray
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(
                                                    width = if (isSel) 3.dp else 1.dp,
                                                    color = if (isSel) MaterialTheme.colorScheme.primary else Color.Gray,
                                                    shape = CircleShape
                                                )
                                                .clickable { backgroundColorHex = hex },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSel) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = if (hex == "#FFFFFF") Color.Black else Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SAVE & PIN BUTTON
                Button(
                    onClick = {
                        if (isSaving) return@Button
                        if (packageName.isBlank()) {
                            Toast.makeText(context, "Silakan pilih aplikasi atau masukkan nama paket", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSaving = true
                        val finalDisplayName = if (displayName.isBlank()) packageName else displayName

                        val widgetToSave = CustomWidget(
                            id = editingWidget?.id ?: 0L,
                            displayName = finalDisplayName,
                            packageName = packageName.trim(),
                            customIconType = customIconType,
                            customIconValue = customIconValue,
                            backgroundColorHex = backgroundColorHex,
                            textColorHex = textColorHex,
                            shapeType = shapeType
                        )

                        viewModel.saveWidget(context, widgetToSave) { savedId ->
                            val updatedWidget = widgetToSave.copy(id = savedId)
                            viewModel.pinWidgetToHomeScreen(context, updatedWidget)
                            onDismiss()
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_widget_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan & Pasang Widget ✨", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }

    // FAST APP PICKER MODAL DIALOG
    if (showAppPickerModal) {
        AppPickerDialog(
            viewModel = viewModel,
            installedApps = installedApps,
            isLoadingApps = viewModel.isLoadingApps.collectAsState().value,
            onSelectApp = { app ->
                packageName = app.packageName
                if (displayName.isBlank() || editingWidget == null) {
                    displayName = app.label
                }
                customIconType = "SYSTEM_APP"
                showAppPickerModal = false
            },
            onDismiss = { showAppPickerModal = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPickerDialog(
    viewModel: WidgetViewModel,
    installedApps: List<InstalledApp>,
    isLoadingApps: Boolean,
    onSelectApp: (InstalledApp) -> Unit,
    onDismiss: () -> Unit
) {
    val appSearchQuery by viewModel.appSearchQuery.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pilih Aplikasi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${installedApps.size} aplikasi ditemukan",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pill Search Input
                OutlinedTextField(
                    value = appSearchQuery,
                    onValueChange = { viewModel.setAppSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_installed_apps_input"),
                    placeholder = { Text("Cari nama atau paket aplikasi...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Virtualized High-Performance Installed App List
                if (isLoadingApps) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Memuat daftar aplikasi...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else if (installedApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aplikasi tidak ditemukan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(installedApps, key = { it.packageName }) { app ->
                            InstalledAppRowItem(
                                app = app,
                                onSelect = { onSelectApp(app) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstalledAppRowItem(
    app: InstalledApp,
    onSelect: () -> Unit
) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val bitmap = remember(app.icon) {
                app.icon?.let { AppListManager.drawableToBitmap(it) }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = app.label,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.label.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Icon(
                Icons.Default.Add,
                contentDescription = "Pilih",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
