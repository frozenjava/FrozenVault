package net.frozendevelopment.frozenvault.modules.settings

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.data.daos.UnlockEventDao
import net.frozendevelopment.frozenvault.data.models.UnlockEventModel
import net.frozendevelopment.frozenvault.services.AppThemeService
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenvault.modules.history.HistoryDialogFragment
import org.joda.time.DateTime

class SettingsViewModel(
    private val appSession: AppSession,
    private val unlockEventDao: UnlockEventDao,
    private val themeService: AppThemeService
) : StatefulViewModel<SettingsState>() {

    override fun getDefaultState(): SettingsState = SettingsState(theme = themeService.currentTheme)

    fun buildLoginHistoryDelegate() : HistoryDialogFragment.HistoryDelegate {
        return object: HistoryDialogFragment.HistoryDelegate {
            override fun getTitleStringResource(): Int = R.string.login_history
            override fun getHistoryData(): Flow<List<DateTime>> = unlockEventDao.getEventDatesByType(UnlockEventModel.UnlockEventType.SUCCESS)
        }
    }

    fun buildFailedLoginHistoryDelegate() : HistoryDialogFragment.HistoryDelegate{
        return object: HistoryDialogFragment.HistoryDelegate {
            override fun getTitleStringResource(): Int = R.string.failed_login_attempts
            override fun getHistoryData(): Flow<List<DateTime>> = unlockEventDao.getEventDatesByType(UnlockEventModel.UnlockEventType.FAILED)
        }
    }

    fun toggleTheme(newTheme: AppThemeService.AppTheme) = viewModelScope.launch{
        state = state.copy(theme = newTheme)
        themeService.currentTheme = newTheme
    }

    fun goToChangePassword(navController: NavController) {
        navController.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment())
    }

    fun goToCreateRecoveryKey(navController: NavController) {
        navController.navigate(SettingsFragmentDirections.actionSettingsFragmentToRecoveryContainerFragment())
    }

}