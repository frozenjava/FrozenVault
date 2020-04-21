package net.frozendevelopment.frozenvault.modules.setup

data class SetupState(
    val setupStage: SetupStage = SetupStage.WELCOME
) {

    enum class SetupStage {
        WELCOME,
        GET_STARTED,
        INTRODUCTION,
        REGISTER,
        LOGIN
    }

}