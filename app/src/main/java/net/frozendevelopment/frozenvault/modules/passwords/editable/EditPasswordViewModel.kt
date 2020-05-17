package net.frozendevelopment.frozenvault.modules.passwords.editable

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.data.daos.ServicePasswordDao
import net.frozendevelopment.frozenvault.data.models.ServicePasswordModel
import net.frozendevelopment.frozenvault.extensions.decryptAES
import net.frozendevelopment.frozenvault.extensions.encryptAES
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.security.SecureRandom
import java.util.*

@ExperimentalCoroutinesApi
class EditPasswordViewModel(
    private var workingMode: WorkingMode,
    private val dao: ServicePasswordDao,
    private val appSession: AppSession): StatefulViewModel<EditPasswordState>() {

    override fun getDefaultState(): EditPasswordState {
        return EditPasswordState(workingMode = workingMode)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun load() = viewModelScope.launch(Dispatchers.IO) {
        if (state.workingMode !is EditMode) return@launch

        val dbModel = dao.getItemById((state.workingMode as EditMode).id)

        state = state.copy(
            serviceName = dbModel.serviceName,
            username = dbModel.userName,
            password = dbModel.password.decryptAES(appSession.secret!!)
        )
    }

    fun generateRandom() = viewModelScope.launch(Dispatchers.Default) {
        val lowerAlphaPool : List<Char> = ('a'..'z').toList()
        val upperAlphaPool: List<Char> = ('A'..'Z').toList()
        val numberPool: List<Char> = ('0'..'9').toList()
        val charPool: List<String> = listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=")

        val random = SecureRandom()
        val passwordPool: MutableList<Char> = mutableListOf()

        val groupSize: Int = state.randomLength / if (state.includeSymbols && state.includeNumbers) 4 else if (state.includeNumbers || state.includeSymbols) 3 else 2

        passwordPool.addAll((1..groupSize).map {
            lowerAlphaPool[random.nextInt(lowerAlphaPool.size)]
        })

        passwordPool.addAll((1..groupSize).map {
            upperAlphaPool[random.nextInt(upperAlphaPool.size)]
        })

        if (state.includeNumbers) {
            passwordPool.addAll((1..groupSize).map {
                numberPool[random.nextInt(numberPool.size)]
            })
        }

        if (state.includeSymbols) {
            passwordPool.addAll((1..groupSize).map {
                charPool[random.nextInt(charPool.size)].single()
            })
        }

        val randomPassword: String = (1..state.randomLength)
            .map { i -> random.nextInt(passwordPool.size) }
            .map(passwordPool::get)
            .joinToString("")

        state = state.copy(password = randomPassword)
    }

    private fun isValidToSave(): Boolean {
        val errors: MutableList<EditPasswordState.EditStateError> = mutableListOf()

        if (state.serviceName.isNullOrBlank())
            errors.add(EditPasswordState.EditStateError.ServiceNameRequired)

        if (state.password.isNullOrBlank())
            errors.add(EditPasswordState.EditStateError.PasswordRequired)
        else if (state.password?.length ?: 0 < 8)
            errors.add(EditPasswordState.EditStateError.PasswordToShort)

        state = state.copy(errors = errors)

        return errors.isEmpty()
    }

    private suspend fun createNew() {
        val model = ServicePasswordModel(
            serviceName = state.serviceName!!,
            userName = state.username,
            password = state.password!!.encryptAES(appSession.secret!!),
            created = DateTime.now(DateTimeZone.UTC),
            updateHistory = listOf(),
            accessHistory = listOf()
        )

        dao.insert(model)
    }

    private suspend fun updateExisting(id: Int) {
        val current = dao.getItemById(id)
        val updateHistory = current.updateHistory.toMutableList()
        updateHistory.add(DateTime.now(DateTimeZone.UTC))

        val updated = current.copy(
            serviceName = state.serviceName!!,
            userName = state.username,
            password = state.password!!.encryptAES(appSession.secret!!),
            updateHistory = updateHistory
        )

        updated.id = id
        dao.update(updated)
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

}
