package net.frozendevelopment.frozenvault.infrustructure

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
abstract class StatefulViewModel<TState> : ViewModel(), LifecycleObserver {

    private val _stateFlow: MutableStateFlow<TState> by lazy {
        MutableStateFlow(getDefaultState())
    }

    val stateFlow: StateFlow<TState> by lazy {
        _stateFlow
    }

    var state: TState
        get() = stateFlow.value ?: getDefaultState()
        set(value) {
            viewModelScope.launch {
                _stateFlow.value = value
            }
        }

    protected abstract fun getDefaultState(): TState
}
