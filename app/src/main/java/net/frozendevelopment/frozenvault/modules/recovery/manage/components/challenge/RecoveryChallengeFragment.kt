package net.frozendevelopment.frozenvault.modules.recovery.manage.components.challenge

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_recovery_challenge_layout.*
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.markRequired
import org.koin.android.ext.android.inject
import java.lang.ref.WeakReference

class RecoveryChallengeFragment(private val onSuccess: () -> Unit) : Fragment(R.layout.fragment_recovery_challenge_layout) {

    private val appSession: AppSession by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        challengePasswordLayout.markRequired()
        challengeAuthButton.setOnClickListener {
            authenticate(challengePasswordLayout.editText?.text?.toString())
        }
    }

    private fun authenticate(password: String?) {
        challengePasswordLayout.error = null

        if (password.isNullOrBlank()) {
            challengePasswordLayout.error = requireContext().getString(R.string.field_required)
            return
        }

        if (password != appSession.secret) {
            challengePasswordLayout.error = requireContext().getString(R.string.invalid_password)
            return
        }

        onSuccess.invoke()
    }

}
