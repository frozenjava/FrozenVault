package net.frozendevelopment.frozenpasswords

import android.app.Application
import net.frozendevelopment.frozenpasswords.infrustructure.appModule
import net.frozendevelopment.frozenpasswords.infrustructure.databaseModule
import net.frozendevelopment.frozenpasswords.infrustructure.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FrozenPasswordsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@FrozenPasswordsApplication)
            modules(listOf(databaseModule, viewModelsModule, appModule))
        }
    }

}
