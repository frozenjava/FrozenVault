package net.frozendevelopment.frozenvault.modules.passwords.list

import net.frozendevelopment.frozenvault.data.models.ServicePasswordModel
import net.frozendevelopment.frozenvault.extensions.decryptAES
import java.util.*

data class PasswordListState(
    val passwords: MutableList<PasswordCellModel> = mutableListOf()
) {

    data class PasswordCellModel(
        val id: Int,
        val service: String,
        val username: String?,
        val password: String,
        val created: Date,
        val lastUpdated: Date?,
        val lastAccessed: Date?,
        val updateHistory: List<Date>,
        val accessHistory: List<Date>,
        val expanded: Boolean = false
    ) {
        companion object {
            fun fromServicePasswordModel(model: ServicePasswordModel, sessionSecret: String): PasswordCellModel = PasswordCellModel(
                id = model.id,
                service = model.serviceName,
                username = model.userName,
                password = model.password.decryptAES(sessionSecret),
                created = model.created,
                lastUpdated = model.updateHistory.firstOrNull(),
                lastAccessed = model.accessHistory.firstOrNull(),
                updateHistory = model.updateHistory,
                accessHistory = model.accessHistory
            )
        }
    }
}
