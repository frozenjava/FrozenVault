package net.frozendevelopment.frozenpasswords.modules.settings

import androidx.navigation.NavController
import net.frozendevelopment.frozenpasswords.Session
import net.frozendevelopment.frozenpasswords.data.daos.UserDao
import net.frozendevelopment.frozenpasswords.infrustructure.StatefulViewModel

class SettingsViewModel(
    navController: NavController,
    session: Session,
    userDao: UserDao
) : StatefulViewModel<SettingsState>() {

    override fun getDefaultState(): SettingsState = SettingsState()

}