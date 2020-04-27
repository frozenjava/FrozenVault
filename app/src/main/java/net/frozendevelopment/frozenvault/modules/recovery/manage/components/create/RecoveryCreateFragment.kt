package net.frozendevelopment.frozenvault.modules.recovery.manage.components.create

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.print.PrintHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.recovery_create_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.infrustructure.StatefulBottomSheet
import net.frozendevelopment.frozenvault.infrustructure.StatefulFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.ref.WeakReference


class RecoveryCreateFragment(private val onCompleteCallback: (() -> Unit)? = null) :
    StatefulFragment<RecoveryCreateState, RecoveryCreateViewModel>(R.layout.recovery_create_layout) {

    override val viewModel: RecoveryCreateViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recoveryCreatePrintButton.setOnClickListener { viewModel.printRecoveryCode(PrintHelper(requireContext())) }
    }

    override fun applyStateToView(state: RecoveryCreateState) {
        recoveryCreateSpinner.isVisible = state.status == RecoveryCreateState.Status.BUSY
        recoveryCreateQRCode.isVisible = state.status != RecoveryCreateState.Status.BUSY
        recoveryCreatePrintButton.isVisible = state.status == RecoveryCreateState.Status.IDLE

        if (state.bitmap != null && state.status == RecoveryCreateState.Status.IDLE) {
            recoveryCreateQRCode.setImageBitmap(state.bitmap)
        }

        if (state.status == RecoveryCreateState.Status.DONE) {
            onCompleteCallback?.invoke()
        }
    }

}
