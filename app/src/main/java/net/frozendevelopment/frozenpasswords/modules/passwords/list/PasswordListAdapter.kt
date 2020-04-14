package net.frozendevelopment.frozenpasswords.modules.passwords.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.password_list_cell.view.*
import net.frozendevelopment.frozenpasswords.R
import net.frozendevelopment.frozenpasswords.extensions.toHumanDate

class PasswordListAdapter : RecyclerView.Adapter<PasswordListAdapter.CellViewHolder> {

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    private val context: Context
    private val passwords: MutableList<PasswordListState.PasswordCellModel>
    private val passwordItemDelegate: PasswordItemDelegate

    constructor(context: Context, passwords: List<PasswordListState.PasswordCellModel>, itemDelegate: PasswordItemDelegate): super() {
        this.context = context
        this.passwords = passwords.toMutableList()
        this.passwordItemDelegate = itemDelegate
    }

    inner class CellViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val serviceName: MaterialTextView = itemView.passwordCellService
        val created: MaterialTextView = itemView.passwordCellCreated
        val updated: MaterialTextView = itemView.passwordCellUpdated
        val copy: AppCompatImageView = itemView.passwordCellCopy
        val edit: AppCompatImageView = itemView.passwordCellEdit
        val delete: AppCompatImageView = itemView.passwordCellDelete
        val accessHistoryButton: MaterialButton = itemView.passwordCellAccessHistoryButton
        val updateHistoryButton: MaterialButton = itemView.passwordCellUpdateHistoryButton
        val username: TextInputEditText = itemView.passwordCellUsername
        val password: TextInputEditText = itemView.passwordCellPassword
        val toggleGroup: Group = itemView.passwordCellToggleItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val itemView = inflater.inflate(R.layout.password_list_cell, parent, false)
        return CellViewHolder(itemView)
    }

    override fun getItemCount(): Int = passwords.size

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        val item = passwords[position]
        with(holder) {
            itemView.setOnClickListener { passwordItemDelegate.onElementClicked(PasswordItemDelegate.ClickedElement.Cell, item, position) }
            copy.setOnClickListener { passwordItemDelegate.onElementClicked(PasswordItemDelegate.ClickedElement.CopyButton, item, position) }
            edit.setOnClickListener { passwordItemDelegate.onElementClicked(PasswordItemDelegate.ClickedElement.EditButton, item, position) }
            delete.setOnClickListener { passwordItemDelegate.onElementClicked(PasswordItemDelegate.ClickedElement.DeleteButton, item, position) }
            accessHistoryButton.setOnClickListener { passwordItemDelegate.onElementClicked(PasswordItemDelegate.ClickedElement.AccessHistoryButton, item, position) }
            updateHistoryButton.setOnClickListener { passwordItemDelegate.onElementClicked(PasswordItemDelegate.ClickedElement.UpdateHistoryButton, item, position) }
            serviceName.text = item.service
            created.text = "Created ${item.created.toHumanDate()}"
            updated.text = "Updated: ${item.lastUpdated?.toHumanDate()}"
            username.setText(item.username)
            password.setText(item.password)
            toggleGroup.isVisible = item.expanded
        }
    }

    fun updateItems(newItems: List<PasswordListState.PasswordCellModel>) {
        passwords.clear()
        passwords.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateItem(index: Int, newState: PasswordListState.PasswordCellModel) {
        passwords[index] = newState
        notifyDataSetChanged()
    }

    interface PasswordItemDelegate {
        fun onElementClicked(element: ClickedElement, item: PasswordListState.PasswordCellModel, index: Int)

        enum class ClickedElement {
            Cell,
            CopyButton,
            EditButton,
            DeleteButton,
            UpdateHistoryButton,
            AccessHistoryButton
        }
    }
}
