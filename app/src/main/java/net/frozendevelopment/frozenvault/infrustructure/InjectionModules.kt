package net.frozendevelopment.frozenvault.infrustructure

import android.content.Context
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.data.AppDatabase
import net.frozendevelopment.frozenvault.modules.changepassword.ChangePasswordViewModel
import net.frozendevelopment.frozenvault.modules.passwords.editable.EditPasswordViewModel
import net.frozendevelopment.frozenvault.modules.passwords.editable.WorkingMode
import net.frozendevelopment.frozenvault.modules.passwords.list.PasswordListViewModel
import net.frozendevelopment.frozenvault.modules.recovery.manage.RecoveryContainerViewModel
import net.frozendevelopment.frozenvault.modules.recovery.manage.components.create.RecoveryCreateViewModel
import net.frozendevelopment.frozenvault.modules.recovery.manage.components.list.RecoveryListViewModel
import net.frozendevelopment.frozenvault.modules.settings.SettingsViewModel
import net.frozendevelopment.frozenvault.modules.setup.SetupViewModel
import net.frozendevelopment.frozenvault.modules.setup.components.register.RegistrationViewModel
import net.frozendevelopment.frozenvault.modules.setup.components.unlock.UnlockViewModel
import net.frozendevelopment.frozenvault.services.AppThemeService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val databaseModule = module {
    factory { AppDatabase.getDatabase(androidContext()).servicePasswordDao() }
    factory { AppDatabase.getDatabase(androidContext()).unlockEventDao() }
    factory { AppDatabase.getDatabase(androidContext()).recoveryKeyDao() }
    single { AppThemeService(get()) }
}

val appModule = module {
    factory { androidContext().getSharedPreferences("FrozenPasswordsPrefs", Context.MODE_PRIVATE) }
    single { AppSession(get(), get()) }
}

val viewModelsModule = module {
    viewModel { PasswordListViewModel(get(), get()) }
    viewModel { (workingMode: WorkingMode) -> EditPasswordViewModel(workingMode, get(), get()) }
    viewModel { UnlockViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get()) }
    viewModel { ChangePasswordViewModel(get(), get()) }
    viewModel { SetupViewModel(get()) }
    viewModel { RegistrationViewModel(get()) }
    viewModel { RecoveryCreateViewModel(get(), get()) }
    viewModel { RecoveryContainerViewModel() }
    viewModel { RecoveryListViewModel(get()) }
}
