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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields

data class UiStreak(
    val count: Int = 0,
    val target: Int = 7,
    val lastCheckIn: LocalDate? = null,
    val startDate: LocalDate = LocalDate.now(),
    val verse: Ayat = Ayat("Kejadian", 1, 1, "Pada mulanya Allah menciptakan langit dan bumi."),
    val weeklyDays: Set<Int> = emptySet(),
    val isActiveToday: Boolean = false
)

sealed interface UiEvent {
    data object ReachedTarget : UiEvent
    data object Relapsed : UiEvent
}

class StreakViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PrefsDataStore(app)
    private val db = DatabaseProvider.db(app)
    private val verseProvider = VerseProvider(app)

    private val _ui = MutableStateFlow(UiStreak())
    val ui: StateFlow<UiStreak> = _ui

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events

    init {
        viewModelScope.launch {
            prefs.flow.collectLatest { snap ->
                val today = LocalDate.now()
                val isActive = snap.last?.isEqual(today) == true
                _ui.value = UiStreak(
                    count = snap.count,
                    target = snap.target,
                    lastCheckIn = snap.last,
                    startDate = snap.start ?: today,
                    verse = verseProvider.randomVerse(),
                    weeklyDays = snap.weekDays,
                    isActiveToday = isActive
                )
            }
        }
    }

    /** Tombol “Berhasil Hari Ini” */
    fun checkInToday() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val snap = prefs.flow.replayCache.firstOrNull() ?: return@launch

            // bypass untuk testing
            if (!BuildConfig.DEBUG && snap.last == today) return@launch

            val newCount = snap.count + 1
            prefs.setStreak(
                count = newCount,
                last = today,
                target = snap.target,
                start = snap.start ?: today
            )
            prefs.markCheckedToday(today)

            db.historyDao().insert(
                HistoryEntity(
                    date = today.toString(),
                    action = "Check-in",
                    count = newCount
                )
            )

            if (newCount >= snap.target) {
                _events.emit(UiEvent.ReachedTarget)
            }
        }
    }

    /** Tombol “Relapse” */
    fun relapse() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val snap = prefs.flow.replayCache.firstOrNull()
            prefs.reset(last = today, keepTarget = true, currentTarget = snap?.target)
            db.historyDao().insert(
                HistoryEntity(
                    date = today.toString(),
                    action = "Relapse",
                    count = 0
                )
            )
            _events.emit(UiEvent.Relapsed)
        }
    }

    /** Naikkan target ke level berikut */
    fun extendTargetToNext() {
        viewModelScope.launch {
            val snap = prefs.flow.replayCache.firstOrNull() ?: return@launch
            val next = listOf(7, 14, 30, 60, 100, 365).firstOrNull { it > snap.target } ?: (snap.target + 365)
            prefs.setStreak(
                count = snap.count,
                last = snap.last ?: LocalDate.now(),
                target = next,
                start = snap.start ?: LocalDate.now()
            )
        }
    }

    /** Set target awal saat user pertama kali buka app */
    fun setInitialTarget(target: Int) {
        viewModelScope.launch {
            prefs.setStreak(
                count = 0,
                last = LocalDate.now(),
                target = target,
                start = LocalDate.now()
            )
        }
    }

    /** Bypass disable tombol check-in kalau BuildConfig.DEBUG aktif */
    val isCheckDisabled: Boolean
        get() = !BuildConfig.DEBUG && _ui.value.isActiveToday
}
