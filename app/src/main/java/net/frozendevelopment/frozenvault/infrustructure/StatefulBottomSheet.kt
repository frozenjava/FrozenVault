package net.frozendevelopment.frozenvault.infrustructure

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import net.frozendevelopment.frozenvault.R
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


@ExperimentalCoroutinesApi
abstract class StatefulBottomSheet<TState, TViewModel: StatefulViewModel<TState>>(private val layoutId: Int) : BottomSheetDialogFragment() {

    protected abstract val viewModel: TViewModel

    protected abstract fun applyStateToView(state: TState)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.stateFlow.collect { applyStateToView(it) }
        }

        KeyboardVisibilityEvent.setEventListener(requireActivity(), viewLifecycleOwner, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    onKeyboardOpened()
                } else {
                    onKeyboardClosed()
                }
            }
        })

        return inflater.inflate(this.layoutId, container, false)
    }

    protected fun dismissKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
        if(imm?.isAcceptingText == true) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        }
    }

    protected open fun onKeyboardOpened() {
        val d = dialog as BottomSheetDialog
        val bottomSheet = d.findViewById<FrameLayout>(R.id.design_bottom_sheet) as FrameLayout
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    protected open fun onKeyboardClosed() {

    }

}
