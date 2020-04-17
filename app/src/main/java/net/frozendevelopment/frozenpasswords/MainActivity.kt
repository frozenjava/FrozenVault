package net.frozendevelopment.frozenpasswords

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import net.frozendevelopment.frozenpasswords.infrustructure.AppThemeService
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val appThemeService: AppThemeService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(appThemeService.loadDefaultTheme().theme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.unlockFragment, R.id.passwordListFragment))
        mainToolbar.setupWithNavController(findNavController(R.id.mainFragmentContainer), appBarConfiguration)

        findNavController(R.id.mainFragmentContainer).addOnDestinationChangedListener { controller, destination, arguments ->
            dismissKeyboard()
            mainToolbar.isVisible = destination.id != R.id.unlockFragment
        }

        lifecycleScope.launchWhenCreated {
            appThemeService.getThemeChangeEvents().collect {
                AppCompatDelegate.setDefaultNightMode(it.theme)
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
