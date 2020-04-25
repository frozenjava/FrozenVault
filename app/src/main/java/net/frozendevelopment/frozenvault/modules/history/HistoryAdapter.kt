package net.frozendevelopment.frozenvault.modules.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.frozendevelopment.frozenvault.extensions.toHumanDateTime
import org.joda.time.DateTime
import java.util.*

class HistoryAdapter(context: Context) : RecyclerView.Adapter<HistoryAdapter.CellViewHolder>() {

    private val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    private val historyItems: MutableList<DateTime> = mutableListOf()

    inner class CellViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById<TextView>(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val itemView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        return CellViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return historyItems.size
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        holder.textView.text = historyItems[position].toHumanDateTime()
    }

    fun updateItems(newItems: List<DateTime>) {
        historyItems.clear()
        historyItems.addAll(newItems)
        notifyDataSetChanged()
    }

}