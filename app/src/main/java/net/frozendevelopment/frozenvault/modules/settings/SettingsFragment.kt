package net.frozendevelopment.frozenvault.modules.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_settings_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.services.AppThemeService
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import net.frozendevelopment.frozenvault.modules.history.HistoryDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : StatefulFragment<SettingsState, SettingsViewModel>(R.layout.fragment_settings_layout) {

    override val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewLoginHistoryButton.setOnClickListener { showLoginHistory(false) }
        settingsViewFailedLoginAttemptsButton.setOnClickListener { showLoginHistory(true) }
        settingsChangePasswordButton.setOnClickListener { viewModel.goToChangePassword(findNavController()) }
        settingsRecoveryKeysButton.setOnClickListener { viewModel.goToCreateRecoveryKey(findNavController()) }

        settingsThemeToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (group.checkedButtonIds.size == 0 && !isChecked) {
                group.check(checkedId)
                return@addOnButtonCheckedListener
            }

            if (isChecked) {
                when (checkedId) {
                    settingsDarkThemeButton.id -> viewModel.toggleTheme(AppThemeService.AppTheme.DARK_MODE)
                    settingsLightThemeButton.id -> viewModel.toggleTheme(AppThemeService.AppTheme.LIGHT_MODE)
                    settingsSystemThemeButton.id -> viewModel.toggleTheme(AppThemeService.AppTheme.SYSTEM)
                }
            }
        }
    }

    override fun applyStateToView(state: SettingsState) {
        when(state.theme) {
            AppThemeService.AppTheme.DARK_MODE -> settingsThemeToggleGroup.check(settingsDarkThemeButton.id)
            AppThemeService.AppTheme.LIGHT_MODE -> settingsThemeToggleGroup.check(settingsLightThemeButton.id)
            AppThemeService.AppTheme.SYSTEM -> settingsThemeToggleGroup.check(settingsSystemThemeButton.id)
        }
    }

    private fun showLoginHistory(showFailedAttempts: Boolean) {
        val delegate = if (showFailedAttempts) {
            viewModel.buildFailedLoginHistoryDelegate()
        } else {
            viewModel.buildLoginHistoryDelegate()
        }

        val dialog = HistoryDialogFragment()
        dialog.delegate = delegate
        dialog.show(requireActivity().supportFragmentManager, "history dialog")
    }

}
