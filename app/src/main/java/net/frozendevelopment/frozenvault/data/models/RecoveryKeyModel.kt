package net.frozendevelopment.frozenvault.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.DateTime

@Entity(tableName = "RecoveryKeys")
data class RecoveryKeyModel(
    val keyHash: String,
    val keySalt: String,
    val encryptedText: String,
    val created: DateTime,
    val usedDate: DateTime? = null,
    val used: Boolean = false,
    val recoveryKeyVersion: Int = 1
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
