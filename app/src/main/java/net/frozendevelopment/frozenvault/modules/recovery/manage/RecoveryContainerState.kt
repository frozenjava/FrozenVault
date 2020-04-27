package net.frozendevelopment.frozenvault.modules.recovery.manage

data class RecoveryContainerState(val status: Status = Status.LIST) {

    enum class Status {
        LIST,
        CHALLENGE,
        CREATE
    }

}