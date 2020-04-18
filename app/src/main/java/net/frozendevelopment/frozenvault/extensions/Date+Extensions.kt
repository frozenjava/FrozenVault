package net.frozendevelopment.frozenvault.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.toHumanDate() : String {
    val formatter = SimpleDateFormat("MMMM dd, yyyy")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(this)
}

fun Date.toHumanDateTime() : String {
    val formatter = SimpleDateFormat("MMMM dd, yyyy m:h a Z")
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(this)
}
