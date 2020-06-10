package net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.form

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.dialog_security_question_form.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.applyText
import net.frozendevelopment.frozenvault.extensions.onTextChanged
import net.frozendevelopment.frozenvault.infrustructure.StatefulBottomSheet
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionError
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionState
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


@ExperimentalCoroutinesApi
class SecurityQuestionFormDialog(
    initialState: SecurityQuestionState?,
    private val onSave: (SecurityQuestionState) -> Unit
) : StatefulBottomSheet<SecurityQuestionState, SecurityQuestionFormViewModel>(R.layout.dialog_security_question_form) {

    override val viewModel: SecurityQuestionFormViewModel by inject {
        parametersOf(initialState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        securityQuestionInput.editText?.onTextChanged { viewModel.state = viewModel.state.copy(question = it) }
        securityQuestionAnswerInput.editText?.onTextChanged { viewModel.state = viewModel.state.copy(answer = it) }
        securityQuestionCancel.setOnClickListener { dismiss() }
        securityQuestionSave.setOnClickListener {
            viewModel.save {
                dismissKeyboard()
                onSave(it)
                dismiss()
            }
        }
    }

    override fun applyStateToView(state: SecurityQuestionState) {
        securityQuestionInput.editText?.applyText(state.question)
        securityQuestionAnswerInput.editText?.applyText(state.answer)
        applyErrors(state.errors)
    }

    private fun applyErrors(errors: List<SecurityQuestionError>) {
        if (errors.isEmpty()) {
            securityQuestionInput.error = null
            securityQuestionAnswerInput.error = null
        }

        for (error in errors) {
            when(error) {
                SecurityQuestionError.QuestionRequired -> securityQuestionInput.error = requireContext().getText(SecurityQuestionError.QuestionRequired.description)
                SecurityQuestionError.AnswerRequired -> securityQuestionAnswerInput.error = requireContext().getText(SecurityQuestionError.AnswerRequired.description)
            }
        }
    }

}
