package net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.list

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.data.daos.ServicePasswordDao
import net.frozendevelopment.frozenvault.extensions.decryptAES
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionState

@ExperimentalCoroutinesApi
class SecurityQuestionListViewModel(
    private val servicePasswordId: Long,
    private val appSession: AppSession,
    private val servicePasswordDao: ServicePasswordDao
) : StatefulViewModel<SecurityQuestionListState>() {

    override fun getDefaultState(): SecurityQuestionListState =
        SecurityQuestionListState()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun loadSecurityQuestions() = viewModelScope.launch(Dispatchers.IO) {
        state = state.copy(securityQuestions = servicePasswordDao.getItemById(servicePasswordId).securityQuestions.map {
            SecurityQuestionState(
                question = it.encryptedQuestion.decryptAES(appSession.secret!!),
                answer = it.encryptedAnswer.decryptAES(appSession.secret!!)
            )
        })
    }

}