package net.frozendevelopment.frozenvault.modules.passwords.editable

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.data.daos.ServicePasswordDao
import net.frozendevelopment.frozenvault.data.models.SecurityQuestionModel
import net.frozendevelopment.frozenvault.data.models.ServicePasswordModel
import net.frozendevelopment.frozenvault.extensions.decryptAES
import net.frozendevelopment.frozenvault.extensions.encryptAES
import net.frozendevelopment.frozenvault.extensions.parallelMap
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionState
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.security.SecureRandom
import java.util.*

@ExperimentalCoroutinesApi
class EditPasswordViewModel(
    private var workingMode: WorkingMode,
    private val passwordDao: ServicePasswordDao,
    private val appSession: AppSession): StatefulViewModel<EditPasswordState>() {

    var formState: FormState
        get() = state.formState
        set(value) { state = state.copy(formState = value) }

    var generatorState: GeneratorState
        get() = state.generatorState
        set(value) { state = state.copy(generatorState = value)}

    var securityQuestions: List<SecurityQuestionState>
        get() = state.securityQuestions
        set(value) { state = state.copy(securityQuestions = value) }

    override fun getDefaultState(): EditPasswordState = EditPasswordState(workingMode = workingMode)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun load() = viewModelScope.launch(Dispatchers.IO) {
        if (state.workingMode !is EditMode) return@launch

        val dbModel = passwordDao.getItemById((state.workingMode as EditMode).id)

        formState = formState.copy(
            serviceName = dbModel.serviceName,
            username = dbModel.userName,
            password = dbModel.password.decryptAES(appSession.secret!!)
        )

        securityQuestions = dbModel.securityQuestions.parallelMap {
            SecurityQuestionState(
                question = it.encryptedQuestion.decryptAES(appSession.secret!!),
                answer = it.encryptedAnswer.decryptAES(appSession.secret!!)
            )
        }
    }

    fun generateRandom() = viewModelScope.launch(Dispatchers.Default) {
        val lowerAlphaPool : List<Char> = ('a'..'z').toList()
        val upperAlphaPool: List<Char> = ('A'..'Z').toList()
        val numberPool: List<Char> = ('0'..'9').toList()
        val charPool: List<String> = listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=")

        val random = SecureRandom()
        val passwordPool: MutableList<Char> = mutableListOf()

        val groupSize: Int = generatorState.randomLength / if (generatorState.includeSymbols && generatorState.includeNumbers) 4 else if (generatorState.includeNumbers || generatorState.includeSymbols) 3 else 2

        passwordPool.addAll((1..groupSize).map {
            lowerAlphaPool[random.nextInt(lowerAlphaPool.size)]
        })

        passwordPool.addAll((1..groupSize).map {
            upperAlphaPool[random.nextInt(upperAlphaPool.size)]
        })

        if (generatorState.includeNumbers) {
            passwordPool.addAll((1..groupSize).map {
                numberPool[random.nextInt(numberPool.size)]
            })
        }

        if (generatorState.includeSymbols) {
            passwordPool.addAll((1..groupSize).map {
                charPool[random.nextInt(charPool.size)].single()
            })
        }

        val randomPassword: String = (1..generatorState.randomLength)
            .map { i -> random.nextInt(passwordPool.size) }
            .map(passwordPool::get)
            .joinToString("")

        formState = formState.copy(password = randomPassword)
    }

    private fun isValidToSave(): Boolean {
        val errors: MutableList<FormError> = mutableListOf()

        if (formState.serviceName.isNullOrBlank())
            errors.add(FormError.ServiceNameRequired)

        if (formState.password.isNullOrBlank())
            errors.add(FormError.PasswordRequired)
        else if (formState.password?.length ?: 0 < 8)
            errors.add(FormError.PasswordToShort)

        formState = formState.copy(errors = errors)

        return errors.isEmpty()
    }

    private suspend fun createNew() {
        val securityQuestionModels = securityQuestions.map { question ->
            SecurityQuestionModel(
                encryptedQuestion = question.question!!.encryptAES(appSession.secret!!),
                encryptedAnswer = question.answer!!.encryptAES(appSession.secret!!)
            )
        }

        val model = ServicePasswordModel(
            serviceName = formState.serviceName!!,
            userName = formState.username,
            password = formState.password!!.encryptAES(appSession.secret!!),
            created = DateTime.now(DateTimeZone.UTC),
            updateHistory = emptyList(),
            accessHistory = emptyList(),
            securityQuestions = securityQuestionModels
        )

        passwordDao.insert(model)
    }

    private suspend fun updateExisting(id: Long) {
        val current = passwordDao.getItemById(id)
        val updateHistory = current.updateHistory.toMutableList()
        updateHistory.add(DateTime.now(DateTimeZone.UTC))

        val securityQuestionModels = securityQuestions.map { question ->
            SecurityQuestionModel(
                encryptedQuestion = question.question!!.encryptAES(appSession.secret!!),
                encryptedAnswer = question.answer!!.encryptAES(appSession.secret!!)
            )
        }

        val updated = current.copy(
            serviceName = formState.serviceName!!,
            userName = formState.username,
            password = formState.password!!.encryptAES(appSession.secret!!),
            updateHistory = updateHistory,
            securityQuestions = securityQuestionModels
        )

        updated.id = id
        passwordDao.update(updated)
    }

    fun save() = viewModelScope.launch(Dispatchers.IO) {
        if (!isValidToSave()) return@launch
        state = state.copy(status = EditPasswordState.Status.Busy)

        if (state.workingMode is EditMode) {
            updateExisting((state.workingMode as EditMode).id)
        } else {
            createNew()
        }

        state = state.copy(status = EditPasswordState.Status.Done)
    }

    fun addOrUpdateSecurityQuestion(securityQuestionState: SecurityQuestionState) {
        val mutableSecQuestions = securityQuestions.toMutableList()
        val index = securityQuestions.indexOfFirst { it.uuid == securityQuestionState.uuid }

        if (index != -1) {
            mutableSecQuestions[index] = securityQuestionState
        } else {
            mutableSecQuestions.add(securityQuestionState)
        }
        securityQuestions = mutableSecQuestions
    }

    fun deleteSecurityQuestion(securityQuestionState: SecurityQuestionState) {
        val mutableSecurityQuestions = securityQuestions.toMutableList()
        mutableSecurityQuestions.remove(securityQuestionState)
        securityQuestions = mutableSecurityQuestions
    }

}
