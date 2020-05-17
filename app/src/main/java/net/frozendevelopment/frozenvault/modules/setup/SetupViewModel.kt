package net.frozendevelopment.frozenvault.modules.setup

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel

@ExperimentalCoroutinesApi
class SetupViewModel(private val appSession: AppSession) : StatefulViewModel<SetupState>() {

    val currentStage: SetupState.SetupStage
        get() = state.setupStage

    override fun getDefaultState(): SetupState {
        return if (!appSession.accountExists) {
            SetupState(setupStage = SetupState.SetupStage.WELCOME)
        } else {
            SetupState(setupStage = SetupState.SetupStage.LOGIN)
        }
    }

    fun goToNextStage(): Unit = when(state.setupStage) {
        SetupState.SetupStage.WELCOME -> state = state.copy(setupStage = SetupState.SetupStage.GET_STARTED)
        SetupState.SetupStage.GET_STARTED -> state = state.copy(setupStage = SetupState.SetupStage.INTRODUCTION)
        SetupState.SetupStage.INTRODUCTION -> state = state.copy(setupStage = SetupState.SetupStage.REGISTER)
        SetupState.SetupStage.REGISTER -> {}
        SetupState.SetupStage.LOGIN -> {}
    }

}
