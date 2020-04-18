package net.frozendevelopment.frozenvault.modules.setup

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel

class SetupViewModel(private val appSession: AppSession) : StatefulViewModel<SetupState>() {

    override fun getDefaultState(): SetupState = SetupState()

    var password: String?
        get() = state.password
        set(value) { state = state.copy(password = value) }

    var confirm: String?
        get() = state.confirm
        set(value) { state = state.copy(confirm = value) }

    private fun isValid(): Boolean {
        val validationErrors: MutableList<SetupState.ValidationError> = mutableListOf()

        with(state) {
            if (password.isNullOrBlank()) {
                validationErrors.add(SetupState.ValidationError.PASSWORD_REQUIRED)
            }

            if (confirm.isNullOrBlank()) {
                validationErrors.add(SetupState.ValidationError.CONFIRM_REQUIRED)
            }

            if (!password.isNullOrBlank() && password.length < 8) {
                validationErrors.add(SetupState.ValidationError.TO_SHORT)
            }

            if (!password.isNullOrBlank() && !confirm.isNullOrBlank() && password != confirm) {
                validationErrors.add(SetupState.ValidationError.MISMATCH)
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
