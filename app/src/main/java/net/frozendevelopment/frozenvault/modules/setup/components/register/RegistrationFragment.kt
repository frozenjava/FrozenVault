package net.frozendevelopment.frozenvault.modules.setup.components.register

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_register_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.applyText
import net.frozendevelopment.frozenvault.extensions.onTextChanged
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationFragment : StatefulFragment<RegistrationState, RegistrationViewModel>(R.layout.fragment_register_layout) {

    override val viewModel: RegistrationViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerPasswordLayout.editText?.onTextChanged { viewModel.password = it }
        registerConfirmLayout.editText?.onTextChanged { viewModel.confirm = it }
        registerButton.setOnClickListener { viewModel.performSetup(findNavController()) }
    }

    override fun applyStateToView(state: RegistrationState) {
        registerPasswordLayout.editText?.applyText(state.password)
        registerConfirmLayout.editText?.applyText(state.confirm)
        applyErrors(state.validationErrors)
    }

    private fun applyErrors(errors: List<RegistrationState.ValidationError>) {
        if (errors.isEmpty()) {
            registerPasswordLayout.error = null
            registerConfirmLayout.error = null
        }

        for (error in errors) {
            when(error) {
                RegistrationState.ValidationError.PASSWORD_REQUIRED -> {
                    registerPasswordLayout.error = requireContext().getString(error.errorStringResource)
                }
                RegistrationState.ValidationError.CONFIRM_REQUIRED -> {
                    registerConfirmLayout.error = requireContext().getString(error.errorStringResource)
                }
                RegistrationState.ValidationError.MISMATCH -> {
                    registerPasswordLayout.error = requireContext().getString(error.errorStringResource)
                    registerConfirmLayout.error = requireContext().getString(error.errorStringResource)
                }
                RegistrationState.ValidationError.TO_SHORT -> {
                    registerPasswordLayout.error = requireContext().getString(error.errorStringResource)
                }
            }
        }
    }

}