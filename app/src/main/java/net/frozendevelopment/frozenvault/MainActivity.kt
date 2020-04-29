package net.frozendevelopment.frozenvault

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import net.frozendevelopment.frozenvault.infrustructure.AppThemeService
import net.frozendevelopment.frozenvault.modules.passwords.list.PasswordListFragmentDirections
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val appThemeService: AppThemeService by inject()
    private val appSession: AppSession by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(appThemeService.loadDefaultTheme().theme)
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(appSession)

        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.passwordListFragment, R.id.setupFragment))
        mainToolbar.setupWithNavController(findNavController(R.id.mainFragmentContainer), appBarConfiguration)
        findNavController(R.id.mainFragmentContainer).addOnDestinationChangedListener { controller, destination, arguments ->
            dismissKeyboard()
            mainToolbar.isVisible = destination.id != R.id.setupFragment
        }

        lifecycleScope.launchWhenCreated {
            appThemeService.getThemeChangeEvents().collect {
                AppCompatDelegate.setDefaultNightMode(it.theme)
            }
        }

        lifecycleScope.launchWhenStarted {
            appSession.getSessionEvents()
                .filter { it == AppSession.SessionEvent.LOCKED }
                .flowOn(Dispatchers.Main)
                .collect {
                    with(findNavController(R.id.mainFragmentContainer)) {
                        popBackStack(R.id.passwordListFragment, false)
                        navigate(PasswordListFragmentDirections.actionPasswordListFragmentToSetupFragment())
                    }
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.mainFragmentContainer).navigateUp()

    private fun dismissKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
        if(imm?.isAcceptingText == true) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}
