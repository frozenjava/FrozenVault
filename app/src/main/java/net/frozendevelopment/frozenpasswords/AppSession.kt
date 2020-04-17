package net.frozendevelopment.frozenpasswords

import android.content.SharedPreferences

class AppSession(private val sharedPreferences: SharedPreferences) {

    private val hashedSecretKey: String = ""
    private val secretSaltKey: String = ""

    var secret: String? = "Dolphins"
    var hashedSecret: String? = null
    var secretSalt: String? = null

    init {
        load()
    }

    private fun load() {
        hashedSecret = sharedPreferences.getString(hashedSecretKey, null)
        secretSalt = sharedPreferences.getString(secretSaltKey, null)
    }

    suspend fun save() {
        sharedPreferences.edit()
            .putString(hashedSecretKey, hashedSecret)
            .putString(secretSaltKey, secretSalt)
            .apply()
    }

}
