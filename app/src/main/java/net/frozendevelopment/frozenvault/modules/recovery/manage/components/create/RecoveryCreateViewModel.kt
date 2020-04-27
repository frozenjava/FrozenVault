package net.frozendevelopment.frozenvault.modules.recovery.manage.components.create

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.viewModelScope
import androidx.print.PrintHelper
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.frozendevelopment.frozenvault.AppSession
import net.frozendevelopment.frozenvault.data.daos.RecoveryKeyDao
import net.frozendevelopment.frozenvault.data.models.RecoveryKeyModel
import net.frozendevelopment.frozenvault.extensions.createHash
import net.frozendevelopment.frozenvault.extensions.encryptAES
import net.frozendevelopment.frozenvault.infrustructure.StatefulViewModel
import net.frozendevelopment.frozenvault.utils.createSalt
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.security.SecureRandom


class RecoveryCreateViewModel(
    private val recoveryKeyDao: RecoveryKeyDao,
    private val appSession: AppSession
) : StatefulViewModel<RecoveryCreateState>() {

    override fun getDefaultState(): RecoveryCreateState = RecoveryCreateState()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createRecoveryKey() = viewModelScope.launch(Dispatchers.IO) {
        val randomKey = generateRecoveryKey()
        val keySalt = createSalt()
        val keyHash = (randomKey + keySalt).createHash()
        val encryptedText: String = appSession.secret!!.encryptAES(randomKey)

        val modelId = recoveryKeyDao.insert(RecoveryKeyModel(
            keyHash = keyHash,
            keySalt = keySalt,
            encryptedText = encryptedText,
            created = DateTime.now(DateTimeZone.UTC)
        ))

        state = state.copy(
            recoveryKey = randomKey,
            bitmap = generateRecoveryQR(modelId, randomKey),
            status = RecoveryCreateState.Status.IDLE)
    }

    fun printRecoveryCode(printer: PrintHelper) {
        val qrCode = state.bitmap ?: return
        printer.scaleMode = PrintHelper.SCALE_MODE_FIT
        printer.printBitmap("Frozen Vault - Recovery Key", qrCode) {
            state = state.copy(status = RecoveryCreateState.Status.DONE)
        }
    }

    fun printingComplete() {
        state = state.copy(status = RecoveryCreateState.Status.DONE)
    }

    private fun generateRecoveryKey(): String {
        val pool: MutableList<String> = mutableListOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=")
        pool.addAll((('a'..'z') + ('A'..'Z') + ('0'..'9')).map { it.toString() })
        val random = SecureRandom()
        return (1..100).joinToString("") { pool[random.nextInt(pool.size)] }
    }

    private fun generateRecoveryQR(keyId: Long, key: String): Bitmap? {
        val data: Map<String, String> = mapOf("id" to keyId.toString(), "key" to key)
        val qrData = Gson().toJson(data)

        val writer = QRCodeWriter()
        return try {
            val bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bmp
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }
}
