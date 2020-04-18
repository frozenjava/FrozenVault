package net.frozendevelopment.frozenvault.modules.unlock

data class UnlockState(
    val password: String? = null,
    val errorMessageResource: Int? = null
)
