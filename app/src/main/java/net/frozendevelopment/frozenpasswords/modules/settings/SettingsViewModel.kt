package net.frozendevelopment.frozenpasswords.modules.settings

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenpasswords.R
import net.frozendevelopment.frozenpasswords.AppSession
import net.frozendevelopment.frozenpasswords.data.daos.UserDao
import net.frozendevelopment.frozenpasswords.data.models.UserModel
import net.frozendevelopment.frozenpasswords.infrustructure.AppThemeService
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenpasswords.modules.history.HistoryDialogFragment
import java.util.*

class SettingsViewModel(
    private val appSession: AppSession,
    private val userDao: UserDao,
    private val themeService: AppThemeService
) : StatefulViewModel<SettingsState>() {

    override fun getDefaultState(): SettingsState = SettingsState(theme = themeService.currentTheme)

    fun buildLoginHistoryDelegate() : HistoryDialogFragment.HistoryDelegate {
        return object: HistoryDialogFragment.HistoryDelegate {
            override fun getTitleStringResource(): Int = R.string.login_history

            override fun getHistoryData(): Flow<List<Date>> = userDao.getUserFlow().map { user ->
                user.loginHistory
                    .filter { it.loginMode == UserModel.LoginMode.SUCCESS_ATTEMPT }
                    .map { it.attemptDate }
            }
        }
    }

    fun buildFailedLoginHistoryDelegate() : HistoryDialogFragment.HistoryDelegate{
        return object: HistoryDialogFragment.HistoryDelegate {
            override fun getTitleStringResource(): Int = R.string.failed_login_attempts

            override fun getHistoryData(): Flow<List<Date>> = userDao.getUserFlow().map { user ->
                user.loginHistory
                    .filter { it.loginMode == UserModel.LoginMode.FAILED_ATTEMPT }
                    .map { it.attemptDate }
            }
        }
    }

    fun toggleTheme(newTheme: AppThemeService.AppTheme) = viewModelScope.launch{
        state = state.copy(theme = newTheme)
        themeService.currentTheme = newTheme
    }

    fun goToChangePassword(navController: NavController) {
        navController.navigate(SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment())
    }

}