package net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.list

import net.frozendevelopment.frozenvault.modules.passwords.securityQuestions.SecurityQuestionState

data class SecurityQuestionListState(
    val securityQuestions: List<SecurityQuestionState> = emptyList()
)
