package net.frozendevelopment.frozenvault.modules.passwords.list

import net.frozendevelopment.frozenvault.data.models.ServicePasswordModel
import net.frozendevelopment.frozenvault.extensions.decryptAES
import org.joda.time.DateTime
import java.util.*

data class PasswordListState(
    val passwords: MutableList<PasswordCellModel> = mutableListOf()
) {

    data class PasswordCellModel(
        val id: Long,
        val service: String,
        val username: String?,
        val password: String,
        val created: DateTime,
        val lastUpdated: DateTime?,
        val lastAccessed: DateTime?,
        val updateHistory: List<DateTime>,
        val accessHistory: List<DateTime>,
        val hasSecurityQuestions: Boolean,
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
                accessHistory = model.accessHistory,
                hasSecurityQuestions = model.securityQuestions.isNotEmpty()
            )
        }
    }
}
