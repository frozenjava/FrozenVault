package net.frozendevelopment.frozenpasswords.modules.settings

import androidx.appcompat.app.AppCompatDelegate
import net.frozendevelopment.frozenpasswords.infrustructure.AppThemeService

data class SettingsState(
    val theme: AppThemeService.AppTheme = AppThemeService.AppTheme.SYSTEM
) {
}
