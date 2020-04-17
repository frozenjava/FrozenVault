package net.frozendevelopment.frozenpasswords.modules.changepassword

import net.frozendevelopment.frozenpasswords.R

data class ChangePasswordState(
    val password: String? = null,
    val newPassword: String? = null,
    val confPassword: String? = null,
    val status: Status = Status.IDLE,
    val validationErrors: List<ValidationErrors> = listOf()
) {

    enum class Status {
        IDLE,
        IN_PROGRESS,
        DONE
    }

    enum class ValidationErrors(val errorStringResource: Int) {
        CURRENT_PASSWORD_REQUIRED(R.string.field_required),
        WRONG_CURRENT_PASSWORD(R.string.invalid_password),
        NEW_PASSWORD_REQUIRED(R.string.field_required),
        PASSWORD_TO_SHORT(R.string.password_to_short),
        PASSWORDS_DONT_MATCH(R.string.password_mismatch),
        CONFIRM_PASSWORD_REQUIRED(R.string.field_required)
    }

}
