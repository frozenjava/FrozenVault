package net.frozendevelopment.frozenvault.modules.recovery.manage.components.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recovery_list_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecoveryListFragment(private val addNewCallback: (() -> Unit)? = null) : StatefulFragment<RecoveryListState, RecoveryListViewModel>(R.layout.fragment_recovery_list_layout) {

    override val viewModel: RecoveryListViewModel by viewModel()

    private val adapter: RecoveryListAdapter by lazy {
        RecoveryListAdapter(requireContext(), viewModel.state.items.toMutableList(), viewModel::deleteItem)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recoveryListRecycler.layoutManager = LinearLayoutManager(requireContext())
        recoveryListRecycler.adapter = adapter
        recoveryListRecycler.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        recoveryListFilterToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (group.checkedButtonIds.isEmpty() && !isChecked) {
                group.check(checkedId)
                return@addOnButtonCheckedListener
            }

            if (isChecked) {
                when(checkedId) {
                    recoveryListAllToggle.id -> viewModel.filter = RecoveryListState.Filter.ALL
                    recoveryListUsedToggle.id -> viewModel.filter = RecoveryListState.Filter.USED
                    recoveryListUnusedToggle.id -> viewModel.filter = RecoveryListState.Filter.UNUSED
                }
            }
        }

        recoveryListCreateButton.setOnClickListener {
            addNewCallback?.invoke()
        }
    }

    override fun applyStateToView(state: RecoveryListState) {
        when(state.filter) {
            RecoveryListState.Filter.UNUSED -> recoveryListFilterToggleGroup.check(recoveryListUnusedToggle.id)
            RecoveryListState.Filter.USED -> recoveryListFilterToggleGroup.check(recoveryListUsedToggle.id)
            RecoveryListState.Filter.ALL -> recoveryListFilterToggleGroup.check(recoveryListAllToggle.id)
        }

        val filteredItems = state.items.filter { state.filter in it.includedWithFilters }
        recoveryListPlaceholder.isVisible = filteredItems.isEmpty()
        adapter.updateItems(filteredItems)
    }

}