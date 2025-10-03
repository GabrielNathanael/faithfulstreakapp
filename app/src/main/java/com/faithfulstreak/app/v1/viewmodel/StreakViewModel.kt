package com.faithfulstreak.app.v1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faithfulstreak.app.v1.data.local.DatabaseProvider
import com.faithfulstreak.app.v1.data.local.HistoryEntity
import kotlinx.coroutines.launch
import java.time.LocalDate

class StreakViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = DatabaseProvider.getDatabase(app).historyDao()

    fun saveStreakToHistory(start: LocalDate, end: LocalDate, length: Int) {
        viewModelScope.launch {
            val history = HistoryEntity(
                startDate = start.toString(),
                endDate = end.toString(),
                length = length
            )
            dao.insert(history)
        }
    }

    fun getAllHistory(onResult: (List<HistoryEntity>) -> Unit) {
        viewModelScope.launch {
            val data = dao.getAll()
            onResult(data)
        }
    }
}
