package net.frozendevelopment.frozenvault.modules.recovery.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.camera.camera2.internal.PreviewConfigProvider
import androidx.camera.core.CameraX
import androidx.camera.core.*
import androidx.camera.core.impl.PreviewConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_recovery_scanner_layout.*
import net.frozendevelopment.frozenvault.R

class RecoveryScannerFragment(private val onComplete: ((Boolean, String?) -> Unit)? = null) : Fragment(R.layout.fragment_recovery_scanner_layout) {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (hasCameraPermission()) {
            recoveryTextureView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (hasCameraPermission()) {
                recoveryTextureView.post { startCamera() }
            } else {
                Snackbar.make(requireView(), "Camera Permission Required", Snackbar.LENGTH_SHORT).show()
                onComplete?.invoke(false, null)
            }
        }
    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder()
            // We want to show input from back camera of the device
            .setLensFacing(CameraX.LensFacing.BACK)
            .build()

        val preview = Preview(previewConfig)

    }

    private fun hasCameraPermission(): Boolean {
        val selfPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

}
