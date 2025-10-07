package com.faithfulstreak.app.v1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.faithfulstreak.app.v1.data.local.DatabaseProvider
import com.faithfulstreak.app.v1.data.local.HistoryEntity
import com.faithfulstreak.app.v1.data.local.PrefsDataStore
import com.faithfulstreak.app.v1.util.Ayat
import com.faithfulstreak.app.v1.util.VerseProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface UiEvent {
    data object ReachedTarget : UiEvent
    data object Relapsed : UiEvent
}

class StreakViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PrefsDataStore(app)
    private val verseProvider = VerseProvider(app)
    private val dao = DatabaseProvider.db(app).historyDao()

    val ui: StateFlow<com.faithfulstreak.app.v1.data.local.PrefSnapshot> =
        prefs.flow.stateIn(viewModelScope, SharingStarted.Eagerly, com.faithfulstreak.app.v1.data.local.PrefSnapshot(0, null, 7, null, null, emptySet()))

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events

    fun checkInToday() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val snap = ui.value

            // skip kalau udah check-in hari ini
            if (!isDebugBuild() && !snap.bypass && snap.last == today) return@launch

            val newCount = snap.count + 1
            val newVerse = verseProvider.random()

            prefs.setStreak(newCount, today, snap.target, snap.start ?: today)
            prefs.markCheckedToday(today, bypassMode = snap.bypass)
            prefs.setVerse(newVerse)

            dao.insert(
                HistoryEntity(
                    startDate = (snap.start ?: today).toString(),
                    endDate = today.toString(),
                    length = newCount,
                    type = "Check-in"
                )
            )

            if (newCount >= snap.target) _events.emit(UiEvent.ReachedTarget)
        }
    }

    fun relapse() {
        viewModelScope.launch {
            val today = LocalDate.now()
            prefs.reset(today, true, ui.value.target)
            dao.insert(
                HistoryEntity(
                    startDate = today.toString(),
                    endDate = today.toString(),
                    length = 0,
                    type = "Relapse"
                )
            )
            _events.emit(UiEvent.Relapsed)
        }
    }

    fun extendTargetToNext() {
        viewModelScope.launch {
            val snap = ui.value
            val next = listOf(7, 14, 30, 60, 100, 365).firstOrNull { it > snap.target } ?: (snap.target + 365)
            prefs.setStreak(snap.count, snap.last ?: LocalDate.now(), next, snap.start ?: LocalDate.now())
        }
    }

    fun setInitialTarget(target: Int) {
        viewModelScope.launch {
            val today = LocalDate.now()
            prefs.setStreak(0, today, target, today)
        }
    }

    fun enableBypassTesting() {
        viewModelScope.launch { prefs.setBypassTesting(true) }
    }

    private fun isDebugBuild(): Boolean {
        return try {
            val pkg = getApplication<Application>().packageName
            val clazz = Class.forName("$pkg.BuildConfig")
            clazz.getField("DEBUG").getBoolean(null)
        } catch (_: Exception) {
            false
        }
    }
}
