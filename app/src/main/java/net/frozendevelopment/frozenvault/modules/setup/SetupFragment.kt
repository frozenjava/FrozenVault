package net.frozendevelopment.frozenvault.modules.setup

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_register_layout.*
import kotlinx.android.synthetic.main.fragment_setup_layout.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.applyText
import net.frozendevelopment.frozenvault.extensions.onTextChanged
import net.frozendevelopment.frozenvault.extensions.transitionText
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import net.frozendevelopment.frozenvault.modules.setup.components.getstarted.GetStartedFragment
import net.frozendevelopment.frozenvault.modules.setup.components.register.RegistrationFragment
import net.frozendevelopment.frozenvault.modules.setup.components.unlock.UnlockFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.ref.WeakReference

@ExperimentalCoroutinesApi
class SetupFragment : StatefulFragment<SetupState, SetupViewModel>(R.layout.fragment_setup_layout) {

    override val viewModel: SetupViewModel by viewModel()

    private lateinit var pageAdapter: SetupAdapter

    private val fragments: MutableList<Fragment> = mutableListOf(
        GetStartedFragment(::getStartedCallback),
        RegistrationFragment(),
        UnlockFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageAdapter = SetupAdapter(childFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewPager.adapter = pageAdapter

        if (viewModel.currentStage == SetupState.SetupStage.LOGIN) {
            setupViewPager.currentItem = getFragmentIndex(UnlockFragment::class.java)
        }

        super.onViewCreated(view, savedInstanceState)

        setupMotionLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                viewModel.goToNextStage()
            }
        })
    }

    override fun applyStateToView(state: SetupState) {
        transitionToNext(state.setupStage)
    }

    private fun transitionToNext(stage: SetupState.SetupStage) = viewLifecycleOwner.lifecycleScope.launch {
        delay(800)
        when(stage) {
            SetupState.SetupStage.WELCOME -> setupMotionLayout.transitionToEnd()
            SetupState.SetupStage.GET_STARTED -> {
                setupTitle.transitionText(requireContext().getString(R.string.lets_get_started))
                delay(1600)
                setupMotionLayout.setTransition(R.id.initialLoadEnd, R.id.opened)
                setupMotionLayout.setTransitionDuration(500)
                setupMotionLayout.transitionToEnd()
                setupTitle.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
            }
            SetupState.SetupStage.INTRODUCTION -> {
                setupViewPager.setCurrentItem(getFragmentIndex(GetStartedFragment::class.java), true)
            }
            SetupState.SetupStage.REGISTER -> {
                setupViewPager.setCurrentItem(getFragmentIndex(RegistrationFragment::class.java), true)
            }
            SetupState.SetupStage.LOGIN -> {
                setupViewPager.currentItem = getFragmentIndex(UnlockFragment::class.java)
                setupTitle.setText(R.string.unlock_you_vault)
                setupMotionLayout.setTransition(R.id.initialLoadStart, R.id.opened)
                setupMotionLayout.setTransitionDuration(500)
                setupMotionLayout.transitionToEnd()
                setupTitle.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
            }
        }
    }

    private fun getStartedCallback() {
        viewModel.goToNextStage()
    }

    private fun <TFragment> getFragmentIndex(frag: Class<TFragment>) : Int {
        return fragments.indexOfFirst { it::class.java == frag }
    }

    private inner class SetupAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size
    }

}
