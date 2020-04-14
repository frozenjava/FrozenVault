package net.frozendevelopment.frozenpasswords.modules.unlock

data class UnlockState(
    val password: String? = null,
    val errorMessageResource: Int? = null
)
