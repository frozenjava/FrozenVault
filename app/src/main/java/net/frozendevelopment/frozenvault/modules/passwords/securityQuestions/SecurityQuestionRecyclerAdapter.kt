package net.frozendevelopment.frozenvault.modules.passwords.securityQuestions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.security_question_list_cell.view.*
import net.frozendevelopment.frozenvault.R

class SecurityQuestionRecyclerAdapter(
    context: Context,
    private val showControls: Boolean,
    private val securityQuestions: MutableList<SecurityQuestionState>,
    private val onEditClicked: ((index: Int, item: SecurityQuestionState) -> Unit)? = null,
    private val onDeleteClicked: ((index: Int, item: SecurityQuestionState) -> Unit)? = null
) : RecyclerView.Adapter<SecurityQuestionRecyclerAdapter.SecurityQuestionViewHolder>() {

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }

    inner class SecurityQuestionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val editButton: AppCompatImageView = itemView.securityQuestionCellEdit
        val deleteButton: AppCompatImageView = itemView.securityQuestionCellDelete
        val question: MaterialTextView = itemView.securityQuestionCellQuestion
        val answer: MaterialTextView = itemView.securityQuestionCellAnswer
    }

    override fun getItemCount(): Int = securityQuestions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecurityQuestionViewHolder {
        val view = this.inflater.inflate(R.layout.security_question_list_cell, parent, false)
        return SecurityQuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SecurityQuestionViewHolder, position: Int) {
        val item: SecurityQuestionState = securityQuestions[position]

        holder.question.text = item.question
        holder.answer.text = item.answer

        holder.editButton.isVisible = showControls
        holder.editButton.setOnClickListener {
            onEditClicked?.invoke(position, item)
        }

        holder.deleteButton.isVisible = showControls
        holder.deleteButton.setOnClickListener {
            onDeleteClicked?.invoke(position, item)
        }
    }

    fun updateSecurityQuestions(newItems: List<SecurityQuestionState>) {
        this.securityQuestions.clear()
        this.securityQuestions.addAll(newItems)
        this.notifyDataSetChanged()
    }

}
