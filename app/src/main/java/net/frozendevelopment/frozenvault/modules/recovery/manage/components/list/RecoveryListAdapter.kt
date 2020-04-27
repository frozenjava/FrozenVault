package net.frozendevelopment.frozenvault.modules.recovery.manage.components.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.recovery_key_list_cell.view.*
import net.frozendevelopment.frozenvault.R
import net.frozendevelopment.frozenvault.extensions.toHumanDateTime

class RecoveryListAdapter(
    context: Context,
    val recoveryKeys: MutableList<RecoveryListState.RecoveryKeyCellModel>,
    val onDeleteClicked: (RecoveryListState.RecoveryKeyCellModel) -> Unit
) : RecyclerView.Adapter<RecoveryListAdapter.CellViewHolder>() {

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    inner class CellViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val created: MaterialTextView = itemView.recoveryCellCreated
        val usedDate: MaterialTextView = itemView.recoveryCellUsed
        val deleteButton: MaterialButton = itemView.recoveryCellDeleteButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val itemView = inflater.inflate(R.layout.recovery_key_list_cell, parent, false)
        return CellViewHolder(itemView)
    }

    override fun getItemCount(): Int = recoveryKeys.size

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        val item = recoveryKeys[position]
        with(holder) {
            created.text = "Created: ${item.created.toHumanDateTime()}"

            usedDate.isVisible = item.used
            usedDate.text = "Used: ${item.usedDate?.toHumanDateTime()}"

            deleteButton.setOnClickListener {
                onDeleteClicked(item)
            }
        }
    }

    fun updateItems(items: List<RecoveryListState.RecoveryKeyCellModel>) {
        recoveryKeys.clear()
        recoveryKeys.addAll(items)
        notifyDataSetChanged()
    }

}