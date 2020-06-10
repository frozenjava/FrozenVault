package net.frozendevelopment.frozenvault.modules.passwords.securityQuestions

import net.frozendevelopment.frozenvault.R
import java.util.*

typealias SecurityQuestionError = SecurityQuestionState.SecurityQuestionError

data class SecurityQuestionState(
    val uuid: UUID = UUID.randomUUID(),
    val question: String? = null,
    val answer: String? = null,
    val errors: List<SecurityQuestionError> = emptyList()
) {
    enum class SecurityQuestionError(val description: Int) {
        QuestionRequired(R.string.field_required),
        AnswerRequired(R.string.field_required)
    }
}
