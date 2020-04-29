package net.frozendevelopment.frozenvault.modules.settings

import net.frozendevelopment.frozenvault.services.AppThemeService

data class SettingsState(
    val theme: AppThemeService.AppTheme = AppThemeService.AppTheme.SYSTEM
) {
}
