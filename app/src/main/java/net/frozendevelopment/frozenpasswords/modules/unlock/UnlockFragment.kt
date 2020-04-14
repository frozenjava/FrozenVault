package net.frozendevelopment.frozenpasswords.modules.unlock

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_unlock_layout.*
import net.frozendevelopment.frozenpasswords.extensions.applyText
import net.frozendevelopment.frozenpasswords.extensions.onTextChanged
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UnlockFragment : StatefulFragment<UnlockState, UnlockViewModel>() {

    override val viewModel: UnlockViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unlockPassword.onTextChanged { password -> viewModel.state = viewModel.state.copy(password=password) }
    }

    override fun applyStateToView(state: UnlockState) {
        unlockPassword.applyText(state.password)
        if (state.errorMessageResource == null) {
            unlockPasswordLayout.error = null
        } else {
            unlockPasswordLayout.error = requireContext().getString(state.errorMessageResource)
        }
    }
}