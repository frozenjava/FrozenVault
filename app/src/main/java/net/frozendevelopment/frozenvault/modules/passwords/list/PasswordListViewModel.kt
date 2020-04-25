package net.frozendevelopment.frozenvault.modules.passwords.list

import androidx.lifecycle.*
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.data.daos.ServicePasswordDao
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.util.*

class PasswordListViewModel(
    private val dao: ServicePasswordDao,
    private val appSession: AppSession
) : StatefulViewModel<PasswordListState>() {

    override fun getDefaultState(): PasswordListState = PasswordListState()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun observeData() = viewModelScope.launch(Dispatchers.IO) {
        dao.getAllFlow()
            .map { dbModels ->
                dbModels.map {
                    val currentState = state.passwords.firstOrNull { statePassword -> statePassword.id == it.id }
                    var new = PasswordListState.PasswordCellModel.fromServicePasswordModel(it, appSession.secret!!)
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

    fun goToLockScreen() {
        appSession.lockSession()
    }

    fun goToAddPassword(navController: NavController) {
        navController.navigate(PasswordListFragmentDirections.actionPasswordListFragmentToEditPasswordFragment())
    }

    fun goToSettings(navController: NavController) {
        navController.navigate(PasswordListFragmentDirections.actionPasswordListFragmentToSettingsFragment())
    }

    fun updateAccessHistory(item: PasswordListState.PasswordCellModel) = viewModelScope.launch(Dispatchers.IO) {
        val current = dao.getItemById(item.id)
        val accessHistory = current.accessHistory.toMutableList()
        accessHistory.add(DateTime.now(DateTimeZone.UTC))
        val updated = current.copy(accessHistory = accessHistory)
        updated.id = item.id
        dao.update(updated)
    }

    fun getAccessHistory(item: PasswordListState.PasswordCellModel): Flow<List<DateTime>> = dao.getFlowById(item.id).map { it.accessHistory }.distinctUntilChanged()

    fun getUpdateHistory(item: PasswordListState.PasswordCellModel): Flow<List<DateTime>> = dao.getFlowById(item.id).map { it.updateHistory }.distinctUntilChanged()

}
