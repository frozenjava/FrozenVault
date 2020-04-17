package net.frozendevelopment.frozenpasswords.modules.unlock

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenpasswords.R
import net.frozendevelopment.frozenpasswords.AppSession
import net.frozendevelopment.frozenpasswords.data.daos.UserDao
import net.frozendevelopment.frozenpasswords.data.models.UserModel
import net.frozendevelopment.frozenpasswords.extensions.createHash
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenpasswords.utils.createSalt
import java.util.*

class UnlockViewModel(
    private val appSession: AppSession,
    private val dao: UserDao
) : StatefulViewModel<UnlockState>() {

    override fun getDefaultState(): UnlockState = UnlockState()

//    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
//    fun register() = viewModelScope.launch {
//        val salt = createSalt()
//        val hash = "Dolphins$salt".createHash()
//        val userModel = UserModel(
//            passwordHash = hash,
//            passwordSalt = salt,
//            loginHistory = listOf()
//        )
//        dao.register(userModel)
//    }

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

        val user = dao.getUser()

        if ((state.password + user.passwordSalt).createHash() == user.passwordHash) {
            unlock(user)
            launch(Dispatchers.Main) {
                navController.navigate(UnlockFragmentDirections.actionUnlockFragmentToPasswordListFragment())
            }
        } else {
            unlockFailed(user)
        }
    }

    private suspend fun unlock(user: UserModel) {
        val updated = user.addToHistory(UserModel.LoginAttempt(
                UserModel.LoginMode.SUCCESS_ATTEMPT,
                Date()
            ))
        updated.id = user.id
        dao.update(updated)

        appSession.secret = state.password
    }

    private suspend fun unlockFailed(user: UserModel) {
        val updated = user.addToHistory(UserModel.LoginAttempt(
                UserModel.LoginMode.FAILED_ATTEMPT,
                Date()
            ))
        updated.id = user.id
        dao.update(updated)
    }

}
