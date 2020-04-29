package net.frozendevelopment.frozenvault.services

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppThemeService(private val sharedPreferences: SharedPreferences) {

    private val themePreferencesKey: String = "FrozenPasswordsTheme"

    private val themeChangeChannel: Channel<AppTheme> = Channel()

    var currentTheme: AppTheme =
        AppTheme.SYSTEM
        set(value) {
            field = value
            themeChangeChannel.offer(value)
            saveCurrentTheme()
        }

    fun getThemeChangeEvents(): Flow<AppTheme> = flow {
        for (theme in themeChangeChannel) {
            emit(theme)
        }
    }

    private fun saveCurrentTheme() {
        sharedPreferences.edit()
            .putString(themePreferencesKey, currentTheme.name)
            .apply()
    }

    fun loadDefaultTheme() : AppTheme {
        val storedTheme = sharedPreferences.getString(themePreferencesKey, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        val appTheme = AppTheme.valueOf(storedTheme)
        currentTheme = appTheme
        return appTheme
    }

    enum class AppTheme(val theme: Int) {
        DARK_MODE(theme = AppCompatDelegate.MODE_NIGHT_YES),
        LIGHT_MODE(theme = AppCompatDelegate.MODE_NIGHT_NO),
        SYSTEM(theme = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}
