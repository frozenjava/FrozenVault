package net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.form

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionError
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionState

@ExperimentalCoroutinesApi
class SecurityQuestionFormViewModel(
    private val initialState: SecurityQuestionState?
): StatefulViewModel<SecurityQuestionState>() {

    override fun getDefaultState(): SecurityQuestionState = initialState ?: SecurityQuestionState()

    private fun validate(): Boolean {
        val errors: MutableList<SecurityQuestionError> = mutableListOf()

        if (state.question.isNullOrBlank()) {
            errors.add(SecurityQuestionError.QuestionRequired)
        }

        if (state.answer.isNullOrBlank()) {
            errors.add(SecurityQuestionError.AnswerRequired)
        }

        state = state.copy(errors = errors.toList())
        return errors.isEmpty()
    }

    fun save(onSuccess: (SecurityQuestionState) -> Unit) {
        if (!validate()) return
        onSuccess(state)
    }

}
