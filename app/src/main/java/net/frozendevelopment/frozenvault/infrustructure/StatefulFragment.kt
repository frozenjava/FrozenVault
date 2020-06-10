package net.frozendevelopment.frozenvault.infrustructure

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

@ExperimentalCoroutinesApi
abstract class StatefulFragment<TState, TViewModel: StatefulViewModel<TState>> : Fragment {

    private val optionsMenuId: Int?

    protected var optionsMenu: Menu? = null
        private set

    constructor(): super() {
        this.optionsMenuId = null
    }

    /**
     * @param layoutId: The id of the layout for this fragment.
     *      It will be inflated for you.
     * @param optionsMenuId: Optional id for the options menu, if you have one.
     *      It will be inflated for you.
     */
    constructor(layoutId: Int, optionsMenuId: Int? = null) : super(layoutId) {
        this.optionsMenuId = optionsMenuId
    }

    protected abstract val viewModel: TViewModel

    /**
     * Implement this method to render the view state `state` to the view.
     */
    protected abstract fun applyStateToView(state: TState)

    /**
     * Override onCreate and call setHasOptionsMenu.
     * If a value for optionsMenuId was passed to the constructor then
     * This will go ahead and tell the fragment that there is an options
     * menu that must be inflated.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(this.optionsMenuId != null)
    }

    /**
     * If a value for optionsMenuId was passed to the constructor,
     * that menu will be inflated here.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        optionsMenuId ?: return
        inflater.inflate(optionsMenuId, menu)
        optionsMenu = menu
    }

    /**
     * Bind the view lifecycle to the viewmodel.
     * Start observing the viewmodels state flow for changes.
     * Binds keyboard visibility callbacks to keyboard visibility event.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.stateFlow.collect { applyStateToView(it) }
        }

        KeyboardVisibilityEvent.setEventListener(requireActivity(), viewLifecycleOwner, object: KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    onKeyboardOpened()
                } else {
                    onKeyboardClosed()
                }
            }
        })

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Called when the soft keyboard has been shown
     */
    protected open fun onKeyboardOpened() { }

    /**
     * Called when the soft keyboard has been dismissed
     */
    protected open fun onKeyboardClosed() { }
}
