package net.frozendevelopment.frozenvault.modules.settings

import net.frozendevelopment.frozenvault.infrustructure.AppThemeService

data class SettingsState(
    val theme: AppThemeService.AppTheme = AppThemeService.AppTheme.SYSTEM
) {
}
