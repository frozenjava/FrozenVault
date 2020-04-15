package net.frozendevelopment.frozenpasswords.extensions

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun String.encryptAES(secret: String) : String {
    val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val keyBytes = ByteArray(16)
    val b: ByteArray = secret.toByteArray(Charsets.UTF_8)
    var len = b.size
    if (len > keyBytes.size) len = keyBytes.size

    System.arraycopy(b, 0, keyBytes, 0, len)
    val keySpec = SecretKeySpec(keyBytes, "AES")
    val ivParameterSpec = IvParameterSpec(keyBytes)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
    val results: ByteArray = cipher.doFinal(this.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(results, 0)
}


fun String.decryptAES(secret: String) : String {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val keyBytes = ByteArray(16)
    val b: ByteArray = secret.toByteArray(Charsets.UTF_8)
    var len = b.size
    if (len > keyBytes.size) len = keyBytes.size

    System.arraycopy(b, 0, keyBytes, 0, len)
    val keySpec = SecretKeySpec(keyBytes, "AES")
    val ivParameterSpec =
        IvParameterSpec(keyBytes)
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)
    val results = cipher.doFinal(Base64.decode(this, 0))
    return String(results, Charsets.UTF_8)
}


@Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
fun String.createHash() : String {
    val messageDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(this.toByteArray(Charsets.UTF_8))
    val digest: ByteArray = messageDigest.digest()
    return String(digest, Charsets.UTF_8)
}
