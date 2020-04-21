package net.frozendevelopment.frozenvault.modules.setup.components.register

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenvault.modules.setup.SetupFragmentDirections

class RegistrationViewModel(private val appSession: AppSession) : StatefulViewModel<RegistrationState>() {

    override fun getDefaultState(): RegistrationState = RegistrationState()

    var password: String?
        get() = state.password
        set(value) { state = state.copy(password = value) }

    var confirm: String?
        get() = state.confirm
        set(value) { state = state.copy(confirm = value) }

    private fun isValid(): Boolean {
        val validationErrors: MutableList<RegistrationState.ValidationError> = mutableListOf()

        with(state) {
            if (password.isNullOrBlank()) {
                validationErrors.add(RegistrationState.ValidationError.PASSWORD_REQUIRED)
            }

            if (confirm.isNullOrBlank()) {
                validationErrors.add(RegistrationState.ValidationError.CONFIRM_REQUIRED)
            }

            if (!password.isNullOrBlank() && password.length < 8) {
                validationErrors.add(RegistrationState.ValidationError.TO_SHORT)
            }

            if (!password.isNullOrBlank() && !confirm.isNullOrBlank() && password != confirm) {
                validationErrors.add(RegistrationState.ValidationError.MISMATCH)
            }
        }

        state = state.copy(validationErrors = validationErrors)
        return validationErrors.isEmpty()
    }

    fun performSetup(navController: NavController) = viewModelScope.launch(Dispatchers.IO) {
        if (!isValid()) return@launch

        appSession.updateSecret(secret = state.password!!)
        appSession.attemptUnlock(state.password!!)

        launch(Dispatchers.Main) {
            navController.navigate(SetupFragmentDirections.actionSetupFragmentToPasswordListFragment())
        }
    }

}