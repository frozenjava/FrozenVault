package net.frozendevelopment.frozenpasswords.modules.setup

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_setup_layout.*
import net.frozendevelopment.frozenpasswords.R
import net.frozendevelopment.frozenpasswords.extensions.applyText
import net.frozendevelopment.frozenpasswords.extensions.onTextChanged
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupFragment : StatefulFragment<SetupState, SetupViewModel>(R.layout.fragment_setup_layout) {

    override val viewModel: SetupViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPasswordLayout.editText?.onTextChanged { viewModel.password = it }
        setupConfirmLayout.editText?.onTextChanged { viewModel.confirm = it }
        setupButton.setOnClickListener { viewModel.performSetup(findNavController()) }
    }

    override fun applyStateToView(state: SetupState) {
        setupPasswordLayout.editText?.applyText(state.password)
        setupConfirmLayout.editText?.applyText(state.confirm)
        applyErrors(state.validationErrors)
    }

    private fun applyErrors(errors: List<SetupState.ValidationError>) {
        if (errors.isEmpty()) {
            setupPasswordLayout.error = null
            setupConfirmLayout.error = null
        }

        for (error in errors) {
            when(error) {
                SetupState.ValidationError.PASSWORD_REQUIRED -> {
                    setupPasswordLayout.error = requireContext().getString(error.errorStringResource)
                }
                SetupState.ValidationError.CONFIRM_REQUIRED -> {
                    setupConfirmLayout.error = requireContext().getString(error.errorStringResource)
                }
                SetupState.ValidationError.MISMATCH -> {
                    setupPasswordLayout.error = requireContext().getString(error.errorStringResource)
                    setupConfirmLayout.error = requireContext().getString(error.errorStringResource)
                }
                SetupState.ValidationError.TO_SHORT -> {
                    setupPasswordLayout.error = requireContext().getString(error.errorStringResource)
                }
            }
        }
    }

}
