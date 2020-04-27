package net.frozendevelopment.frozenvault.modules.recovery.manage

import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel

class RecoveryContainerViewModel : StatefulViewModel<RecoveryContainerState>() {

    override fun getDefaultState(): RecoveryContainerState = RecoveryContainerState()

    fun goToListView() {
        state = state.copy(status = RecoveryContainerState.Status.LIST)
    }

    fun goToChallenge() {
        state = state.copy(status = RecoveryContainerState.Status.CHALLENGE)
    }

    fun goToCreate() {
        state = state.copy(status = RecoveryContainerState.Status.CREATE)
    }

}
