package net.frozendevelopment.frozenpasswords.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

inline fun <TState> Fragment.observeLiveEvents(source: MutableLiveData<TState>, crossinline onUpdate: (TState) -> Unit) {
    source.observe(this.viewLifecycleOwner, Observer {
        it ?: return@Observer
        onUpdate(it)
    })
}
