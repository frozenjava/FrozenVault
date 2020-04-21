package net.frozendevelopment.frozenvault.extensions

import android.widget.TextView

fun TextView.transitionText(
    newText: String,
    fadeOutDelay: Long = 0,
    fadeInDelay: Long = 100,
    fadeOutDuration: Long = 800,
    fadeInDuration: Long = 800) {
    if (this.text == newText) return

    this.animate()
        .setStartDelay(fadeOutDelay)
        .setDuration(fadeOutDuration)
        .alpha(0f)
        .withEndAction {
            this.text = newText
            this.animate()
                .setDuration(fadeInDuration)
                .alpha(1f)
                .setStartDelay(fadeInDelay)
                .start()
        }
        .start()
}
