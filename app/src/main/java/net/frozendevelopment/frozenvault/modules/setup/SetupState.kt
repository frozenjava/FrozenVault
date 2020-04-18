package net.frozendevelopment.frozenvault.modules.setup

import net.frozendevelopment.frozenvault.R

data class SetupState(
    val password: String? = null,
    val confirm: String? = null,
    val validationErrors: List<ValidationError> = listOf()
) {

    enum class ValidationError(val errorStringResource: Int) {
        PASSWORD_REQUIRED(R.string.field_required),
        CONFIRM_REQUIRED(R.string.field_required),
        MISMATCH(R.string.password_mismatch),
        TO_SHORT(R.string.password_to_short)
    }

}