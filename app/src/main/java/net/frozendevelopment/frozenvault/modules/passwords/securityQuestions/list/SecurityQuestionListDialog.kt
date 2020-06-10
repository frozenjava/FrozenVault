package net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_recycler_layout.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.infrustructure.StatefulBottomSheet
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionRecyclerAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
class SecurityQuestionListDialog : StatefulBottomSheet<SecurityQuestionListState, SecurityQuestionListViewModel>(R.layout.dialog_recycler_layout) {

    override val viewModel: SecurityQuestionListViewModel by viewModel {
        val servicePasswordId: Long = arguments?.getLong("passwordId", -1) ?: -1
        parametersOf(servicePasswordId)
    }

    private val securityQuestionAdapter by lazy {
        SecurityQuestionRecyclerAdapter(
            requireContext(),
            false,
            viewModel.state.securityQuestions.toMutableList()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogRecycler.adapter = securityQuestionAdapter
        dialogRecycler.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        dialogTitle.text = requireContext().getText(R.string.security_questions)
        dialogTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_security_24, 0, 0, 0)
        dialogPlaceholder.text = requireContext().getText(R.string.no_security_questions)
    }

    override fun applyStateToView(state: SecurityQuestionListState) {
        dialogPlaceholder.isVisible = state.securityQuestions.isEmpty()
        securityQuestionAdapter.updateSecurityQuestions(state.securityQuestions)
    }

}
