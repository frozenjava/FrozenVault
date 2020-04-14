package net.frozendevelopment.frozenpasswords.modules.unlock

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenpasswords.Session
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulViewModel

class UnlockViewModel(private val session: Session) : StatefulViewModel<UnlockState>() {

    override fun getDefaultState(): UnlockState = UnlockState()

    fun attemptUnlock() = viewModelScope.launch {

    }

}