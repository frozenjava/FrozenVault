package net.frozendevelopment.frozenpasswords.infrustructure

import net.frozendevelopment.frozenpasswords.Session
import net.frozendevelopment.frozenpasswords.data.AppDatabase
import net.frozendevelopment.frozenpasswords.data.daos.ServicePasswordDao
import net.frozendevelopment.frozenpasswords.modules.passwords.editable.EditPasswordViewModel
import net.frozendevelopment.frozenpasswords.modules.passwords.editable.WorkingMode
import net.frozendevelopment.frozenpasswords.modules.passwords.list.PasswordListViewModel
import net.frozendevelopment.frozenpasswords.modules.unlock.UnlockViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val databaseModule = module {
    single { AppDatabase.getDatabase(androidContext()).servicePasswordDao() }
}

val appModule = module {
    single { Session() }
}

val viewModelsModule = module {
    viewModel { PasswordListViewModel(get(), get()) }
    viewModel { (workingMode: WorkingMode) -> EditPasswordViewModel(workingMode, get(), get()) }
    viewModel { UnlockViewModel(get()) }
}
