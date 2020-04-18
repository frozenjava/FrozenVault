package net.frozendevelopment.frozenvault.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.onTextChanged(onChange: (text: String?) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            onChange(s?.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

fun EditText.applyText(text: String?) {
    if (this.text?.toString() != text) {
        this.setText(text)
    }
}