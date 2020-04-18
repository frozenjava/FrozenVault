package net.frozendevelopment.frozenpasswords.modules.unlock

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenpasswords.R
import net.frozendevelopment.frozenpasswords.AppSession
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulViewModel

class UnlockViewModel(private val appSession: AppSession) : StatefulViewModel<UnlockState>() {

    override fun getDefaultState(): UnlockState = UnlockState()

    private fun validate() : Boolean {
        if (state.password.isNullOrBlank()) {
            state = state.copy(errorMessageResource = R.string.field_required)
            return false
        }

        state = state.copy(errorMessageResource = null)
        return true
    }

    fun attemptUnlock(navController: NavController) = viewModelScope.launch(Dispatchers.IO) {
        if (!validate()) return@launch

        if (appSession.attemptUnlock(state.password!!)) {
            launch(Dispatchers.Main) {
                navController.navigate(UnlockFragmentDirections.actionUnlockFragmentToPasswordListFragment())
            }
        } else {
            state = state.copy(errorMessageResource = R.string.invalid_password)
        }
    }

}
