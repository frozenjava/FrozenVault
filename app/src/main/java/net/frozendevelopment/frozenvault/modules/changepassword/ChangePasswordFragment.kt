package net.frozendevelopment.frozenvault.modules.changepassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_change_password_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.applyText
import net.frozendevelopment.frozenvault.extensions.markRequired
import net.frozendevelopment.frozenvault.extensions.onTextChanged
import net.frozendevelopment.frozenvault.infrustructure.StatefulBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChangePasswordFragment : StatefulBottomSheet<ChangePasswordState, ChangePasswordViewModel>(R.layout.fragment_change_password_layout) {

    override val viewModel: ChangePasswordViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changePasswordCurrentLayout.markRequired()
        changePasswordNewLayout.markRequired()
        changePasswordConfLayout.markRequired()

        changePasswordCurrent.onTextChanged { viewModel.currentPassword = it }
        changePasswordNew.onTextChanged { viewModel.newPassword = it }
        changePasswordConf.onTextChanged { viewModel.confPassword = it }
        changePasswordButton.setOnClickListener { viewModel.preformPasswordChange() }
    }

    override fun applyStateToView(state: ChangePasswordState) {
        applyErrors(state.validationErrors)
        changePasswordCurrent.applyText(state.password)
        changePasswordNew.applyText(state.newPassword)
        changePasswordConf.applyText(state.confPassword)
        changePasswordButton.isVisible = state.status != ChangePasswordState.Status.IN_PROGRESS
        changePasswordSpinner.isVisible = state.status == ChangePasswordState.Status.IN_PROGRESS

        if (state.status == ChangePasswordState.Status.DONE) {
            Toast.makeText(requireContext(), R.string.password_changed, Toast.LENGTH_SHORT).show()
            this.dismiss()
        }
    }

    private fun applyErrors(validationErrors: List<ChangePasswordState.ValidationErrors>) {
        if (validationErrors.isEmpty()) {
            changePasswordCurrentLayout.error = null
            changePasswordNewLayout.error = null
            changePasswordConfLayout.error = null
        }

        for (error in validationErrors) {
            when(error) {
                ChangePasswordState.ValidationErrors.WRONG_CURRENT_PASSWORD -> {
                    changePasswordCurrentLayout.error = requireContext().getString(error.errorStringResource)
                }
                ChangePasswordState.ValidationErrors.PASSWORD_TO_SHORT -> {
                    changePasswordNewLayout.error = requireContext().getString(error.errorStringResource)
                }
                ChangePasswordState.ValidationErrors.PASSWORDS_DONT_MATCH -> {
                    changePasswordNewLayout.error = requireContext().getString(error.errorStringResource)
                    changePasswordConfLayout.error = requireContext().getString(error.errorStringResource)
                }
                ChangePasswordState.ValidationErrors.CURRENT_PASSWORD_REQUIRED -> {
                    changePasswordCurrentLayout.error = requireContext().getString(error.errorStringResource)
                }
                ChangePasswordState.ValidationErrors.NEW_PASSWORD_REQUIRED -> {
                    changePasswordNewLayout.error = requireContext().getString(error.errorStringResource)
                }
                ChangePasswordState.ValidationErrors.CONFIRM_PASSWORD_REQUIRED -> {
                    changePasswordConfLayout.error = requireContext().getString(error.errorStringResource)
                }
            }
        }
    }

}
