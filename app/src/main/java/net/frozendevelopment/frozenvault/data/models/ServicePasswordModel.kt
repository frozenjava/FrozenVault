package net.frozendevelopment.frozenvault.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "ServicePasswords")
data class ServicePasswordModel(
    val serviceName: String,
    val userName: String?,
    val password: String,
    val created: DateTime,
    val updateHistory: List<DateTime> = emptyList(),
    val accessHistory: List<DateTime> = emptyList(),
    val securityQuestions: List<SecurityQuestionModel> = emptyList()
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}

data class SecurityQuestionModel(
    val encryptedQuestion: String,
    val encryptedAnswer: String
)
