package net.frozendevelopment.frozenpasswords.infrustructure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

abstract class StatefulFragment<TState, TViewModel: StatefulViewModel<TState>> : Fragment {

    constructor(): super()
    constructor(layoutId: Int) : super(layoutId) {}


    protected abstract val viewModel: TViewModel

    protected abstract fun applyStateToView(state: TState)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.stateChannel.asFlow()
                .distinctUntilChanged()
                .collect {
                    applyStateToView(it)
                }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
