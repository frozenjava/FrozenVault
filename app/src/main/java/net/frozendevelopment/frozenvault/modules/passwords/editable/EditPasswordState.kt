package net.frozendevelopment.frozenvault.modules.passwords.editable

import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionState
import java.util.*

typealias FormState = EditPasswordState.FormState
typealias FormError = EditPasswordState.FormState.FormError
typealias GeneratorState = EditPasswordState.GeneratorState

data class EditPasswordState(
    val formState: FormState = FormState(),
    val generatorState: GeneratorState = GeneratorState(),
    val securityQuestions: List<SecurityQuestionState> = emptyList(),
    val status: Status = Status.Idle,
    val workingMode: WorkingMode
) {

    data class FormState(
        val serviceName: String? = null,
        val username: String? = null,
        val password: String? = null,
        val errors: List<FormError> = emptyList()
    ) {
        enum class FormError(val description: Int) {
            ServiceNameRequired(R.string.field_required),
            PasswordRequired(R.string.field_required),
            PasswordToShort(R.string.password_to_short)
        }
    }

    data class GeneratorState(
        val includeSymbols: Boolean = true,
        val includeNumbers: Boolean = true,
        val randomLength: Int = 10
    )

    enum class Status {
        Idle,
        Busy,
        Done
    }

}

sealed class WorkingMode
object CreateMode : WorkingMode()
data class EditMode(val id: Long): WorkingMode()
