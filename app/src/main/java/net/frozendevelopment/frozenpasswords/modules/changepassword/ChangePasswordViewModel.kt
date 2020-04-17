package net.frozendevelopment.frozenpasswords.modules.changepassword

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenpasswords.AppSession
import net.frozendevelopment.frozenpasswords.data.daos.ServicePasswordDao
import net.frozendevelopment.frozenpasswords.data.daos.UserDao
import net.frozendevelopment.frozenpasswords.extensions.createHash
import net.frozendevelopment.frozenpasswords.extensions.decryptAES
import net.frozendevelopment.frozenpasswords.extensions.encryptAES
import net.frozendevelopment.frozenpasswords.extensions.parallelProcess
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenpasswords.utils.createSalt

class ChangePasswordViewModel(
    private val appSession: AppSession,
    private val userDao: UserDao,
    private val passwordDao: ServicePasswordDao
) : StatefulViewModel<ChangePasswordState>() {

    override fun getDefaultState(): ChangePasswordState = ChangePasswordState()

    var currentPassword: String?
        get() = state.password
        set(value) {
            state = state.copy(password = value)
        }

    var newPassword: String?
        get() = state.newPassword
        set(value) {
            state = state.copy(newPassword = value)
        }

    var confPassword: String?
        get() = state.confPassword
        set(value) {
            state = state.copy(confPassword = value)
        }

    var status: ChangePasswordState.Status
        get() = state.status
        set(value) {
            state = state.copy(status = value)
        }

    private fun isValid(): Boolean {
        val errors: MutableList<ChangePasswordState.ValidationErrors> = mutableListOf()

        with(state) {
            if (password.isNullOrBlank()) {
                errors.add(ChangePasswordState.ValidationErrors.CURRENT_PASSWORD_REQUIRED)
            }

            if (newPassword.isNullOrBlank()) {
                errors.add(ChangePasswordState.ValidationErrors.NEW_PASSWORD_REQUIRED)
            }

            if (confPassword.isNullOrBlank()) {
                errors.add(ChangePasswordState.ValidationErrors.CONFIRM_PASSWORD_REQUIRED)
            }

            if (password != appSession.secret && !password.isNullOrBlank()) {
                errors.add(ChangePasswordState.ValidationErrors.WRONG_CURRENT_PASSWORD)
            }

            if (newPassword?.length ?: 0 < 8 && !newPassword.isNullOrBlank()) {
                errors.add(ChangePasswordState.ValidationErrors.PASSWORD_TO_SHORT)
            }

            if (newPassword != state.confPassword && !newPassword.isNullOrBlank()) {
                errors.add(ChangePasswordState.ValidationErrors.PASSWORDS_DONT_MATCH)
            }
        }

        state = state.copy(validationErrors = errors)
        return errors.isEmpty()
    }

    fun preformPasswordChange() = viewModelScope.launch(Dispatchers.IO) {
        if (!isValid()) return@launch
        status = ChangePasswordState.Status.IN_PROGRESS

        val newSalt = createSalt()
        val hashedPass = (state.newPassword!!+newSalt).createHash()
        val userModel = userDao.getUser()
        val updatedUser = userModel.copy(passwordHash = hashedPass, passwordSalt = newSalt)
        updatedUser.id = userModel.id
        userDao.update(updatedUser)

        appSession.secret = state.newPassword!!

        passwordDao.getAllItems().parallelProcess { dbModel ->
            val plainText = dbModel.password.decryptAES(state.password!!)
            val updatedModel = dbModel.copy(password = plainText.encryptAES(state.newPassword!!))
            updatedModel.id = dbModel.id
            passwordDao.update(updatedModel)
        }

        status = ChangePasswordState.Status.DONE
    }

}
