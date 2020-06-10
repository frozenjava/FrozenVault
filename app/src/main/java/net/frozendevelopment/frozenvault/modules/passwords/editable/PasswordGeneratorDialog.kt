package net.frozendevelopment.frozenvault.modules.passwords.editable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_password_generator.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.modules.passwords.editable.GeneratorState

class PasswordGeneratorDialog(
    private var state: GeneratorState,
    private val onComplete: (GeneratorState) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_password_generator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        generatorIncludeNumbers.isChecked = state.includeNumbers
        generatorIncludeSymbols.isChecked = state.includeSymbols
        generatorCharLength.progress = state.randomLength
        generatorCharLengthLabel.text = "${state.randomLength} Characters"

        generatorIncludeSymbols.setOnCheckedChangeListener { _, isChecked ->
            state = state.copy(includeSymbols = isChecked)
        }
        generatorIncludeNumbers.setOnCheckedChangeListener { _, isChecked ->
            state = state.copy(includeNumbers = isChecked)
        }

        generatorCharLength.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress < 8) {
                    seekBar?.progress = 8
                    return
                }
                generatorCharLengthLabel.text = "$progress Characters"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return

                if (seekBar.progress < 8) {
                    seekBar.progress = 8
                    return
                }

                state = state.copy(randomLength = seekBar.progress)
            }

        })

        generatorCancel.setOnClickListener {
            dismiss()
        }

        generatorDone.setOnClickListener {
            onComplete(state)
            dismiss()
        }
    }

}
