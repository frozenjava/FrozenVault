package net.frozendevelopment.frozenpasswords

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.unlockFragment, R.id.passwordListFragment))
        mainToolbar.setupWithNavController(findNavController(R.id.mainFragmentContainer), appBarConfiguration)

        findNavController(R.id.mainFragmentContainer).addOnDestinationChangedListener { controller, destination, arguments ->
            dismissKeyboard()
            mainToolbar.isVisible = destination.id != R.id.unlockFragment
        }
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.mainFragmentContainer).navigateUp()

//    override fun onBackPressed() {
//        if (findNavController(R.id.mainFragmentContainer).currentDestination?.id == R.id.passwordListFragment) {
//            return
//        }
//        super.onBackPressed()
//    }

    private fun dismissKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
        if(imm?.isAcceptingText == true) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}
