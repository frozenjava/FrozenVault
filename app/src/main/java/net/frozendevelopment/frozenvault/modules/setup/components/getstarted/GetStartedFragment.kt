package net.frozendevelopment.frozenvault.modules.setup.components.getstarted

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_getstarted_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.modules.setup.SetupViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.ref.WeakReference

class GetStartedFragment(private val callback: () -> Unit) : Fragment(R.layout.fragment_getstarted_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getStartedButton.setOnClickListener {
            callback()
        }
    }
}
