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
import kotlinx.android.synthetic.main.fragment_history_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.R
import java.util.*

class HistoryDialogFragment : BottomSheetDialogFragment() {

    interface HistoryDelegate {
        fun getTitleStringResource(): Int
        fun getHistoryData(): Flow<List<Date>>
    }

    var delegate: HistoryDelegate? = null

    private val adapter: HistoryAdapter by lazy { HistoryAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val delegate = delegate ?: return
        historyTitle.text = requireContext().getString(delegate.getTitleStringResource())
        historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyRecycler.adapter = adapter
        historyRecycler.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        historyPlaceholder.isVisible = adapter.itemCount == 0

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            delegate.getHistoryData()
                .distinctUntilChanged()
                .collect { historyItems ->
                    launch(Dispatchers.Main) {
                        adapter.updateItems(historyItems)
                        historyPlaceholder.isVisible = historyItems.isEmpty()
                    }
                }
        }
    }
}
