package net.frozendevelopment.frozenvault

import android.app.Application
import net.frozendevelopment.frozenvault.infrustructure.appModule
import net.frozendevelopment.frozenvault.infrustructure.databaseModule
import net.frozendevelopment.frozenvault.infrustructure.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FrozenVaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@FrozenVaultApplication)
            modules(listOf(databaseModule, viewModelsModule, appModule))
        }
    }

}
