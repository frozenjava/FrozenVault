package net.frozendevelopment.frozenpasswords.infrustructure

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch

abstract class StatefulViewModel<TState> : ViewModel(), LifecycleObserver {

    val stateChannel: ConflatedBroadcastChannel<TState> by lazy {
        ConflatedBroadcastChannel(getDefaultState())
    }

    var state: TState
        get() = stateChannel.valueOrNull ?: getDefaultState()
        set(value) {
            viewModelScope.launch {
                stateChannel.send(value)
            }
        }

    protected abstract fun getDefaultState(): TState
}
