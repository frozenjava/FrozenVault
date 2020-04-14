package net.frozendevelopment.frozenpasswords

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        mainToolbar.setupWithNavController(findNavController(R.id.mainFragmentContainer))
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.mainFragmentContainer).navigateUp()
}
