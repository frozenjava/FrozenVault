package net.frozendevelopment.frozenvault.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "ServicePasswords")
data class ServicePasswordModel(
    val serviceName: String,
    val userName: String?,
    val password: String,
    val created: Date,
    val updateHistory: List<Date>,
    val accessHistory: List<Date>
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
