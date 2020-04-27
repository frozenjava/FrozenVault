package net.frozendevelopment.frozenvault.modules.recovery.manage.components.create

import android.graphics.Bitmap

data class RecoveryCreateState(
    val recoveryKey: String? = null,
    val bitmap: Bitmap? = null,
    val status: Status = Status.BUSY
) {

    enum class Status {
        BUSY,
        IDLE,
        DONE,
    }
}
