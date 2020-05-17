package net.frozendevelopment.frozenvault.infrustructure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
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
            viewModel.stateFlow.collect { applyStateToView(it) }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
