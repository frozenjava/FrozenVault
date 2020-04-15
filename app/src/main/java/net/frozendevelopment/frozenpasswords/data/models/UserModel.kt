package net.frozendevelopment.frozenpasswords.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*


@Entity(tableName = "User")
data class UserModel(
    val passwordHash: String,
    val passwordSalt: String,
    val loginHistory: List<LoginAttempt>
) {

    @PrimaryKey(autoGenerate = true) var id: Int = 0

    enum class LoginMode() {
        @SerializedName("0") FAILED_ATTEMPT,
        @SerializedName("1") SUCCESS_ATTEMPT
    }

    data class LoginAttempt(
        val loginMode: LoginMode,
        val attemptDate: Date
    )

    fun addToHistory(attempt: LoginAttempt): UserModel {
        val history = this.loginHistory.toMutableList()
        history.add(attempt)
        return this.copy(loginHistory = history)
    }

}
