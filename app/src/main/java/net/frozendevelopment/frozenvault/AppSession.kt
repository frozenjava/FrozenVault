package net.frozendevelopment.frozenvault

import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.frozendevelopment.frozenvault.data.daos.UnlockEventDao
import net.frozendevelopment.frozenvault.data.models.UnlockEventModel
import net.frozendevelopment.frozenvault.extensions.createHash
import net.frozendevelopment.frozenvault.utils.createSalt
import java.util.*

class AppSession(
    private val unlockEventDao: UnlockEventDao,
    private val sharedPreferences: SharedPreferences
): LifecycleObserver {

    private val hashedSecretKey: String = "SessionSecretHash"
    private val secretSaltKey: String = "SessionSecretSalt"

    private val sessionEventChannel: Channel<SessionEvent> = Channel()

    var secret: String? = null
        private set(value) {
            field = value
            if (!value.isNullOrBlank()) {
                unlockedAt = System.currentTimeMillis()
            } else {
                unlockedAt = null
            }
        }

    var unlockedAt: Long? = null
        private set

    var appPausedAt: Long? = null
        private set

    var sessionTimeoutMilliseconds: Long = 300000 // 5 minutes

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

    fun getSessionEvents(): Flow<SessionEvent> = flow {
        for (event in sessionEventChannel) {
            emit(event)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppResumed() {
        if (unlockedAt != null && appPausedAt != null) {
            if (System.currentTimeMillis() - unlockedAt!! >= sessionTimeoutMilliseconds) {
                lockSession()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppPaused() {
        if (unlockedAt != null) {
            appPausedAt = System.currentTimeMillis()
        }
    }

    suspend fun attemptUnlock(secret: String) : Boolean {
        return if ((secret+this.secretSalt).createHash() == this.hashedSecret) {
            this.unlockEventDao.insert(UnlockEventModel(
                Date(),
                UnlockEventModel.UnlockEventType.SUCCESS
            ))
            this.secret = secret
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
