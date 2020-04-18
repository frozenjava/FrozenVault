package net.frozendevelopment.frozenvault.modules.unlock

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_unlock_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.applyText
import net.frozendevelopment.frozenvault.extensions.onTextChanged
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UnlockFragment : StatefulFragment<UnlockState, UnlockViewModel>(R.layout.fragment_unlock_layout) {

    override val viewModel: UnlockViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        unlockPassword.onTextChanged { password -> viewModel.state = viewModel.state.copy(password=password) }
        unlockButton.setOnClickListener { viewModel.attemptUnlock(findNavController()) }
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
