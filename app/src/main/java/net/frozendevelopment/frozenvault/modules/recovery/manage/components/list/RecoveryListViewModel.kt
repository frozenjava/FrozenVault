package net.frozendevelopment.frozenvault.modules.recovery.manage.components.list

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.data.daos.RecoveryKeyDao
import net.frozendevelopment.frozenvault.extensions.parallelMap
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel

class RecoveryListViewModel(private val recoveryKeyDao: RecoveryKeyDao) : StatefulViewModel<RecoveryListState>() {

    override fun getDefaultState(): RecoveryListState = RecoveryListState()

    var filter: RecoveryListState.Filter
        get() = state.filter
        set(value) {
            state = state.copy(filter = value)
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun observeDatabase() = viewModelScope.launch(Dispatchers.IO) {
        recoveryKeyDao.getAllRecoveryKeys()
            .distinctUntilChanged()
            .map { dbModels ->
                dbModels.parallelMap { model ->
                    RecoveryListState.RecoveryKeyCellModel(
                        id = model.id,
                        created = model.created,
                        used = model.used,
                        usedDate = model.usedDate
                    )
                }
            }
            .collect {
                state = state.copy(items = it)
            }
    }

    fun deleteItem(item: RecoveryListState.RecoveryKeyCellModel) {
        viewModelScope.launch(Dispatchers.IO) {
            recoveryKeyDao.delete(item.id)
        }
    }

}