package net.frozendevelopment.frozenvault.modules.passwords.editable

import net.frozendevelopment.frozenvault.R

data class EditPasswordState(
    val serviceName: String? = null,
    val username: String? = null,
    val password: String? = null,
    val includeSymbols: Boolean = true,
    val includeNumbers: Boolean = true,
    val randomLength: Int = 50,
    val errors: List<EditStateError> = listOf(),
    val workingMode: WorkingMode,
    val status: Status = Status.Idle
) {

    enum class EditStateError(val description: Int) {
        ServiceNameRequired(R.string.field_required),
        PasswordRequired(R.string.field_required),
        PasswordToShort(R.string.password_to_short)
    }

    enum class Status {
        Idle,
        Busy,
        Done
    }

}

sealed class WorkingMode
object CreateMode : WorkingMode()
data class EditMode(val id: Int): WorkingMode()
