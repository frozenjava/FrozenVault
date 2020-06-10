package net.frozendevelopment.frozenvault.extensions

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun Fragment.dismissKeyboard() {
    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
    if(imm?.isAcceptingText == true) { // verify if the soft keyboard is open
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }
}
