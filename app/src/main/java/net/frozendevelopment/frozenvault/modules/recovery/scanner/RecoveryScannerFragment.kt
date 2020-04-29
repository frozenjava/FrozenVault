package net.frozendevelopment.frozenvault.modules.recovery.scanner

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_recovery_scanner_layout.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.services.QRDetectionService
import java.util.concurrent.Executor

class RecoveryScannerFragment(private val onComplete: ((Boolean, String?) -> Unit)? = null) : BottomSheetDialogFragment() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recovery_scanner_layout, container, false)
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
            .setLensFacing(CameraX.LensFacing.BACK)
            .build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener {
            recoveryTextureView.surfaceTexture = it.surfaceTexture
        }

        CameraX.bindToLifecycle(viewLifecycleOwner, preview)

//        val imageAnalysisConfig = ImageAnalysisConfig.Builder().build()
//        val imageAnalysis = ImageAnalysis(imageAnalysisConfig)
//
//        val qrDetectionService = QRDetectionService { qrCodes ->
//            qrCodes.forEach {
//                Log.d("QrDetectionService", "QR Code Detected: ${it.rawValue}")
//            }
//        }
//
//        imageAnalysis.analyzer = qrDetectionService
//
//        CameraX.bindToLifecycle(this as LifecycleOwner, preview)
    }

    private fun hasCameraPermission(): Boolean {
        val selfPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

}
