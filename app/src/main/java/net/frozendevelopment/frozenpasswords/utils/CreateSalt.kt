package net.frozendevelopment.frozenpasswords.utils

import android.util.Base64
import java.security.SecureRandom

fun createSalt(): String {
    val random: SecureRandom = SecureRandom()
    val bytes: ByteArray = ByteArray(32)
    random.nextBytes(bytes)
    return Base64.encodeToString(bytes, 0).trim()
}
