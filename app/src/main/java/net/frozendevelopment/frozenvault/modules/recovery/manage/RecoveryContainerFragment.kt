package net.frozendevelopment.frozenvault.modules.recovery.manage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.recovery_container_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.infrustructure.StatefulBottomSheet
import net.frozendevelopment.frozenvault.modules.recovery.manage.components.challenge.RecoveryChallengeFragment
import net.frozendevelopment.frozenvault.modules.recovery.manage.components.create.RecoveryCreateFragment
import net.frozendevelopment.frozenvault.modules.recovery.manage.components.list.RecoveryListFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecoveryContainerFragment : StatefulBottomSheet<RecoveryContainerState, RecoveryContainerViewModel>(R.layout.recovery_container_layout) {

    override val viewModel: RecoveryContainerViewModel by viewModel()

    private val adapter: ManagementAdapter by lazy {
        ManagementAdapter(
            listOf(
                RecoveryListFragment(viewModel::goToChallenge),
                RecoveryChallengeFragment(viewModel::goToCreate),
                RecoveryCreateFragment(::onRecoveryCodeSaved)),
            childFragmentManager)
    }

    private val onBackPressedCallback: OnBackPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recoveryManagementViewPager.adapter = adapter
    }

    override fun applyStateToView(state: RecoveryContainerState) {
        onBackPressedCallback.isEnabled = state.status != RecoveryContainerState.Status.LIST

        when(state.status) {
            RecoveryContainerState.Status.LIST -> recoveryManagementViewPager.setCurrentItem(0, false)
            RecoveryContainerState.Status.CHALLENGE -> recoveryManagementViewPager.setCurrentItem(1, true)
            RecoveryContainerState.Status.CREATE -> {
                dismissKeyboard()
                recoveryManagementViewPager.setCurrentItem(2, true)
            }
        }
    }

    private fun dismissKeyboard() {
        if (requireDialog().currentFocus != null) {
            val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireDialog().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun onRecoveryCodeSaved() {
        Snackbar.make(requireView(), "All done!", Snackbar.LENGTH_SHORT).show()
        viewModel.goToListView()
    }

    private inner class ManagementAdapter(val fragments: List<Fragment>, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size
    }

}