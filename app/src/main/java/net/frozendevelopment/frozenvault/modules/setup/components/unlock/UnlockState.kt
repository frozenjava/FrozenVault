package net.frozendevelopment.frozenvault.modules.setup.components.unlock

data class UnlockState(
    val password: String? = null,
    val errorMessageResource: Int? = null,
    val hasRecoveryKeys: Boolean = false
)
