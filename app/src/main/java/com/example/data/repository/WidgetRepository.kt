package com.example.data.repository

import android.content.Context
import com.example.data.db.CustomWidgetDao
import com.example.data.model.CustomWidget
import com.example.data.model.InstalledApp
import com.example.utils.AppListManager
import com.example.utils.BackupRestoreManager
import kotlinx.coroutines.flow.Flow

class WidgetRepository(
    private val dao: CustomWidgetDao
) {
    val allWidgets: Flow<List<CustomWidget>> = dao.getAllWidgets()

    suspend fun getWidgetById(id: Long): CustomWidget? = dao.getWidgetById(id)

    suspend fun saveWidget(widget: CustomWidget): Long {
        return if (widget.id == 0L) {
            dao.insertWidget(widget)
        } else {
            dao.updateWidget(widget)
            widget.id
        }
    }

    suspend fun deleteWidget(id: Long) {
        dao.deleteWidget(id)
    }

    suspend fun clearAllWidgets() {
        dao.deleteAll()
    }

    suspend fun restoreWidgets(widgets: List<CustomWidget>, appendMode: Boolean = true) {
        if (!appendMode) {
            dao.deleteAll()
        }
        dao.insertAll(widgets)
    }

    suspend fun loadInstalledApps(context: Context): List<InstalledApp> {
        return AppListManager.getInstalledApps(context)
    }

    fun exportBackup(widgets: List<CustomWidget>): String {
        return BackupRestoreManager.exportToJson(widgets)
    }

    suspend fun importBackup(jsonString: String, appendMode: Boolean): Result<Int> {
        val parseResult = BackupRestoreManager.importFromJson(jsonString)
        return parseResult.mapCatching { list ->
            if (!appendMode) {
                dao.deleteAll()
            }
            dao.insertAll(list)
            list.size
        }
    }
}
