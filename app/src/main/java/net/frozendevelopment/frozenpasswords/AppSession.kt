package net.frozendevelopment.frozenpasswords

import android.content.SharedPreferences
import kotlinx.coroutines.channels.Channel
import net.frozendevelopment.frozenpasswords.data.daos.UnlockEventDao
import net.frozendevelopment.frozenpasswords.data.models.UnlockEventModel
import net.frozendevelopment.frozenpasswords.extensions.createHash
import net.frozendevelopment.frozenpasswords.utils.createSalt
import java.util.*

class AppSession(
    private val unlockEventDao: UnlockEventDao,
    private val sharedPreferences: SharedPreferences
) {

    private val hashedSecretKey: String = "SessionSecretHash"
    private val secretSaltKey: String = "SessionSecretSalt"

    private val sessionEventChannel: Channel<SessionEvent> = Channel()

    var secret: String? = null
        private set

    var unlockedAt: Long? = System.currentTimeMillis()
        private set

    var hashedSecret: String? = null
        private set
    var secretSalt: String? = null
        private set

    val accountExists: Boolean
        get() = !hashedSecret.isNullOrBlank()

    init {
        load()
    }

    private fun load() {
        hashedSecret = sharedPreferences.getString(hashedSecretKey, null)?.trim()
        secretSalt = sharedPreferences.getString(secretSaltKey, null)?.trim()
        val x = 0
    }

    private fun save() {
        sharedPreferences.edit()
            .putString(hashedSecretKey, hashedSecret)
            .putString(secretSaltKey, secretSalt)
            .apply()
    }

    fun lockSession() {
        this.secret = null
        this.unlockedAt = null
        sessionEventChannel.offer(SessionEvent.LOCKED)
    }

    fun updateSecret(secret: String) {
        this.secret = secret
        val newSalt = createSalt()
        this.hashedSecret = (secret+newSalt).createHash()
        this.secretSalt = newSalt
        this.save()
        sessionEventChannel.offer(SessionEvent.SECRET_UPDATED)
    }

    suspend fun attemptUnlock(secret: String) : Boolean {
        return if ((secret+this.secretSalt).createHash() == this.hashedSecret) {
            this.unlockEventDao.insert(UnlockEventModel(
                Date(),
                UnlockEventModel.UnlockEventType.SUCCESS
            ))
            this.secret = secret
            this.unlockedAt = System.currentTimeMillis()
            sessionEventChannel.offer(SessionEvent.UNLOCKED)
            true
        } else {
            this.unlockEventDao.insert(UnlockEventModel(
                Date(),
                UnlockEventModel.UnlockEventType.FAILED
            ))
            false
        }
    }

    enum class SessionEvent {
        UNLOCKED,
        LOCKED,
        SECRET_UPDATED
    }
}
