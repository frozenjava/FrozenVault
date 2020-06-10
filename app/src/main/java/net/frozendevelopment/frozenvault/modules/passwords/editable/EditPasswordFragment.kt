package net.frozendevelopment.frozenvault.modules.passwords.editable

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_password_editable_layout.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.applyText
import net.frozendevelopment.frozenvault.extensions.markRequired
import net.frozendevelopment.frozenvault.extensions.onTextChanged
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.form.SecurityQuestionFormDialog
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionRecyclerAdapter
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionState
import org.jetbrains.anko.alert
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class EditPasswordFragment : StatefulFragment<EditPasswordState, EditPasswordViewModel>(R.layout.fragment_password_editable_layout, R.menu.edit_menu) {

    override val viewModel: EditPasswordViewModel by viewModel {
        val passwordId: Long = arguments?.getLong("passwordId", -1L) ?: -1L
        val workingMode = if (passwordId != -1L) EditMode(passwordId) else CreateMode
        parametersOf(workingMode)
    }

    private val securityQuestionAdapter: SecurityQuestionRecyclerAdapter by lazy {
        SecurityQuestionRecyclerAdapter(
            requireContext(),
            true,
            viewModel.state.securityQuestions.toMutableList(),
            { _, item -> showSecurityQuestionForm(item, viewModel::addOrUpdateSecurityQuestion) },
            { _, item ->
                requireContext().alert(R.string.delete_dialog, R.string.delete_confirmation_title) {
                    positiveButton(R.string.positive_action) { viewModel.deleteSecurityQuestion(item) }
                    negativeButton(R.string.negative_action) { it.dismiss() }
                }.show()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editableServiceLayout.markRequired()
        editablePasswordLayout.markRequired()
        editableServiceName.onTextChanged { viewModel.formState = viewModel.formState.copy(serviceName = it) }
        editableUsername.onTextChanged { viewModel.formState = viewModel.formState.copy(username = it) }
        editablePassword.onTextChanged { viewModel.formState = viewModel.formState.copy(password = it) }

        editableSecurityQuestionRecycler.apply {
            adapter = securityQuestionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        editableRandomPasswordButton.setOnClickListener {
            val dialog =
                PasswordGeneratorDialog(
                    viewModel.generatorState
                ) {
                    viewModel.generatorState = it
                    viewModel.generateRandom()
                }
            dialog.show(childFragmentManager, "generator_dialog")
        }

        editableAddSecQuestion.setOnClickListener {
            showSecurityQuestionForm(null, viewModel::addOrUpdateSecurityQuestion)
        }

        observeSecurityQuestions()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveButton -> viewModel.save()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun applyStateToView(state: EditPasswordState) {
        editableServiceName.applyText(state.formState.serviceName)
        editableUsername.applyText(state.formState.username)
        editablePassword.applyText(state.formState.password)
        applyErrors(state.formState.errors)

        when(state.workingMode) {
            is EditMode -> activity?.title = "Edit"
            is CreateMode -> activity?.title = "Create"
        }

        if (state.status == EditPasswordState.Status.Done) {
            Snackbar.make(requireView(), R.string.password_saved, Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun applyErrors(errors: List<FormError>) {
        if (errors.isEmpty()) {
            editableServiceLayout.error = null
            editableUsernameLayout.error = null
            editablePasswordLayout.error = null
        }

        for (error in errors) {
            when(error) {
                FormError.ServiceNameRequired -> editableServiceLayout.error = requireContext().getString(error.description)
                FormError.PasswordRequired -> editablePasswordLayout.error = requireContext().getString(error.description)
                FormError.PasswordToShort -> editablePasswordLayout.error = requireContext().getString(error.description)
            }
        }
    }

    private fun observeSecurityQuestions() = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
        viewModel.stateFlow
            .map { it.securityQuestions }
            .distinctUntilChanged()
            .collect { questions ->
                if (questions.isNotEmpty()) {
                    editablePasswordMotionLayout.transitionToEnd()
                }

                editableSecurityQuestionPlaceHolder.isVisible = questions.isEmpty()
                securityQuestionAdapter.updateSecurityQuestions(questions)
            }
    }

    private fun showSecurityQuestionForm(securityQuestionState: SecurityQuestionState? = null, onSave: (SecurityQuestionState) -> Unit) {
        val dialog =
            SecurityQuestionFormDialog(
                securityQuestionState,
                onSave
            )
        dialog.show(childFragmentManager, "security_question_form_dialog")
    }

}