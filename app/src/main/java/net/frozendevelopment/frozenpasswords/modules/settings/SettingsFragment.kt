package net.frozendevelopment.frozenpasswords.modules.settings

import androidx.navigation.fragment.findNavController
import net.frozendevelopment.frozenpasswords.R
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulFragment
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : StatefulFragment<SettingsState, SettingsViewModel>(R.layout.fragment_settings_layout) {

    override val viewModel: SettingsViewModel by viewModel { parametersOf(findNavController()) }

    override fun applyStateToView(state: SettingsState) {
    }

}
