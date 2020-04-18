package net.frozendevelopment.frozenvault.modules.passwords.editable

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_password_editable_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.applyText
import net.frozendevelopment.frozenvault.extensions.markRequired
import net.frozendevelopment.frozenvault.extensions.onTextChanged
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPasswordFragment : StatefulFragment<EditPasswordState, EditPasswordViewModel>(R.layout.fragment_password_editable_layout) {

    override val viewModel: EditPasswordViewModel by viewModel {
        val passwordId = arguments?.getInt("passwordId", -1) ?: -1
        val workingMode = if (passwordId != -1) EditMode(passwordId) else CreateMode
        parametersOf(workingMode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editableServiceLayout.markRequired()
        editablePasswordLayout.markRequired()
        editableServiceName.onTextChanged { viewModel.state = viewModel.state.copy(serviceName = it) }
        editableUsername.onTextChanged { viewModel.state = viewModel.state.copy(username = it) }
        editablePassword.onTextChanged { viewModel.state = viewModel.state.copy(password = it) }

        editableRandomPasswordButton.setOnClickListener { viewModel.generateRandom() }
        editableIncludeSymbols.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.state = viewModel.state.copy(includeSymbols = isChecked)
        }
        editableIncludeNumbers.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.state = viewModel.state.copy(includeNumbers = isChecked)
        }

        editableCharLength.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < 8) {
                    seekBar?.progress = 8
                    return
                }
                editableCharLengthLabel.text = "$progress Characters"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return

                if (seekBar.progress < 8) {
                    seekBar.progress = 8
                    return
                }

                viewModel.state = viewModel.state.copy(randomLength = seekBar.progress)
            }

        })
    }

    override fun applyStateToView(state: EditPasswordState) {
        editableServiceName.applyText(state.serviceName)
        editableUsername.applyText(state.username)
        editablePassword.applyText(state.password)
        editableIncludeNumbers.isChecked = state.includeNumbers
        editableIncludeSymbols.isChecked = state.includeSymbols
        editableCharLength.progress = state.randomLength
        editableCharLengthLabel.text = "${state.randomLength} Characters"
        applyErrors(state.errors)

        when(state.workingMode) {
            is EditMode -> activity?.title = "Edit"
            is CreateMode -> activity?.title = "Create"
        }
    }

    private fun applyErrors(errors: List<EditPasswordState.EditStateError>) {
        if (errors.isEmpty()) {
            editableServiceLayout.error = null
            editableUsernameLayout.error = null
            editablePasswordLayout.error = null
        }

        for (error in errors) {
            when(error) {
                EditPasswordState.EditStateError.ServiceNameRequired -> editableServiceLayout.error = requireContext().getString(error.description)
                EditPasswordState.EditStateError.PasswordRequired -> editablePasswordLayout.error = requireContext().getString(error.description)
                EditPasswordState.EditStateError.PasswordToShort -> editablePasswordLayout.error = requireContext().getString(error.description)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveButton -> viewModel.save()
        }
        return super.onOptionsItemSelected(item)
    }

}