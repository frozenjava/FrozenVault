package net.frozendevelopment.frozenpasswords.modules.passwords.list

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenpasswords.Session
import net.frozendevelopment.frozenpasswords.data.daos.ServicePasswordDao
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulViewModel
import java.util.*

class PasswordListViewModel(private val dao: ServicePasswordDao, private val session: Session) : StatefulViewModel<PasswordListState>() {

    override fun getDefaultState(): PasswordListState = PasswordListState()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun observeData() = viewModelScope.launch(Dispatchers.IO) {
        dao.getAllFlow()
            .map { dbModels ->
                dbModels.map {
                    val currentState = state.passwords.firstOrNull { statePassword -> statePassword.id == it.id }
                    var new = PasswordListState.PasswordCellModel.fromServicePasswordModel(it, session.secret!!)
                    if (currentState != null) {
                        new = new.copy(expanded = currentState.expanded)
                    }
                    new
                }
            }
            .distinctUntilChanged()
            .collect { passwords ->
                state = state.copy(passwords=passwords.toMutableList())
            }
    }

    fun expandOrCollapse(item: PasswordListState.PasswordCellModel): PasswordListState.PasswordCellModel {
        val indexForItem = state.passwords.indexOf(item)
        val updated = state.passwords[indexForItem].copy(expanded = !state.passwords[indexForItem].expanded)
        state.passwords[indexForItem] = updated
        return updated
    }

    fun delete(item: PasswordListState.PasswordCellModel) = viewModelScope.launch(Dispatchers.IO) {
        dao.deleteById(item.id)
    }

    fun updateAccessHistory(item: PasswordListState.PasswordCellModel) = viewModelScope.launch(Dispatchers.IO) {
        val current = dao.getItemById(item.id)
        val accessHistory = current.accessHistory.toMutableList()
        accessHistory.add(Date())
        val updated = current.copy(accessHistory = accessHistory)
        updated.id = item.id
        dao.update(updated)
    }

    fun getAccessHistory(item: PasswordListState.PasswordCellModel): Flow<List<Date>> = dao.getFlowById(item.id).map { it.accessHistory }.distinctUntilChanged()

    fun getUpdateHistory(item: PasswordListState.PasswordCellModel): Flow<List<Date>> = dao.getFlowById(item.id).map { it.updateHistory }.distinctUntilChanged()

}
