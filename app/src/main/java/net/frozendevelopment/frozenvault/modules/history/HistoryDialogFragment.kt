package net.frozendevelopment.frozenvault.modules.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_recycler_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.R
import org.joda.time.DateTime

class HistoryDialogFragment : BottomSheetDialogFragment() {

    interface HistoryDelegate {
        fun getTitleStringResource(): Int
        fun getHistoryData(): Flow<List<DateTime>>
    }

    var delegate: HistoryDelegate? = null

    private val adapter: HistoryAdapter by lazy { HistoryAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_recycler_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val delegate = delegate ?: return
        dialogTitle.text = requireContext().getString(delegate.getTitleStringResource())
        dialogRecycler.layoutManager = LinearLayoutManager(requireContext())
        dialogRecycler.adapter = adapter
        dialogRecycler.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        dialogPlaceholder.isVisible = adapter.itemCount == 0

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            delegate.getHistoryData()
                .distinctUntilChanged()
                .collect { historyItems ->
                    launch(Dispatchers.Main) {
                        adapter.updateItems(historyItems)
                        dialogPlaceholder.isVisible = historyItems.isEmpty()
                    }
                }
        }
    }
}
