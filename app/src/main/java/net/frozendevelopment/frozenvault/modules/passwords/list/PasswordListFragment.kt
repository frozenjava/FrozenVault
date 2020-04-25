package net.frozendevelopment.frozenvault.modules.passwords.list

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_password_list_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import net.frozendevelopment.frozenvault.modules.history.HistoryDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import org.jetbrains.anko.*
import org.joda.time.DateTime

class PasswordListFragment : StatefulFragment<PasswordListState, PasswordListViewModel>(R.layout.fragment_password_list_layout), PasswordListAdapter.PasswordItemDelegate {

    override val viewModel: PasswordListViewModel by viewModel()

    private val adapter: PasswordListAdapter by lazy {
        PasswordListAdapter(requireContext(), viewModel.state.passwords, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireContext().alert(R.string.lock_dialog, R.string.lock_confirmation_title) {
                    positiveButton(R.string.positive_action) { viewModel.goToLockScreen() }
                    negativeButton(R.string.negative_action) { it.dismiss() }
                }.show()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passwordsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        passwordsRecyclerView.adapter = adapter
        passwordsRecyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        passwordsPlaceholderButton.setOnClickListener { viewModel.goToAddPassword(findNavController()) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newPassword -> viewModel.goToAddPassword(findNavController())
            R.id.lock -> viewModel.goToLockScreen()
            R.id.account -> viewModel.goToSettings(findNavController())
        }

        return super.onOptionsItemSelected(item)
    }

    override fun applyStateToView(state: PasswordListState) {
        passwordsDataGroup.isVisible = state.passwords.isNotEmpty()
        passwordsPlaceholderGroup.isVisible = state.passwords.isEmpty()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            adapter.updateItems(state.passwords)
        }
    }

    override fun onElementClicked(
        element: PasswordListAdapter.PasswordItemDelegate.ClickedElement,
        item: PasswordListState.PasswordCellModel,
        index: Int
    ) {
        when(element) {
            PasswordListAdapter.PasswordItemDelegate.ClickedElement.Cell -> {
                val updatedItem = viewModel.expandOrCollapse(item)
                adapter.updateItem(index, updatedItem)
                viewModel.updateAccessHistory(item)
            }
            PasswordListAdapter.PasswordItemDelegate.ClickedElement.CopyButton -> {
                viewModel.updateAccessHistory(item)
                val clipboardManager: ClipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(item.service, item.password)
                clipboardManager.setPrimaryClip(clip)
                Snackbar.make(requireView(), R.string.copied_to_clipboard, Snackbar.LENGTH_SHORT).show()
            }
            PasswordListAdapter.PasswordItemDelegate.ClickedElement.EditButton -> {
                viewModel.updateAccessHistory(item)
                findNavController().navigate(
                    R.id.action_passwordListFragment_to_editPasswordFragment,
                    bundleOf("passwordId" to item.id)
                )
            }
            PasswordListAdapter.PasswordItemDelegate.ClickedElement.DeleteButton -> {
                requireContext().alert(R.string.delete_dialog, R.string.delete_confirmation_title) {
                    positiveButton(R.string.positive_action) { viewModel.delete(item) }
                    negativeButton(R.string.negative_action) { it.dismiss() }
                }.show()
            }
            PasswordListAdapter.PasswordItemDelegate.ClickedElement.UpdateHistoryButton -> {
                showHistoryDialog(buildUpdateHistoryDelegate(item))
            }
            PasswordListAdapter.PasswordItemDelegate.ClickedElement.AccessHistoryButton -> {
                showHistoryDialog(buildAccessHistoryDelegate(item))
            }
        }
    }

    private fun buildAccessHistoryDelegate(item: PasswordListState.PasswordCellModel) : HistoryDialogFragment.HistoryDelegate {
        return object: HistoryDialogFragment.HistoryDelegate {
            override fun getTitleStringResource(): Int = R.string.access_history
            override fun getHistoryData(): Flow<List<DateTime>> = viewModel.getAccessHistory(item)
        }
    }

    private fun buildUpdateHistoryDelegate(item: PasswordListState.PasswordCellModel) : HistoryDialogFragment.HistoryDelegate {
        return object: HistoryDialogFragment.HistoryDelegate {
            override fun getTitleStringResource(): Int = R.string.update_history
            override fun getHistoryData(): Flow<List<DateTime>> = viewModel.getUpdateHistory(item)
        }
    }

    private fun showHistoryDialog(delegate: HistoryDialogFragment.HistoryDelegate) {
        val dialog = HistoryDialogFragment()
        dialog.delegate = delegate
        dialog.show(requireActivity().supportFragmentManager, "history dialog")
    }

}
