package com.interview.exercise

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ItemAdapter(
    private var items: List<Item>,
    private var selectedIds: Set<Int>, // ✅ Accept immutable Set
    private val onItemSelected: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    fun updateSelection(newSelectedIds: Set<Int>) {
        selectedIds = newSelectedIds // ✅ Update with new set
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val itemID: TextView = view.findViewById(R.id.itemID)
        val text: TextView = view.findViewById(R.id.itemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.text.text = item.name
        holder.itemID.text = buildString {
            append("Item ID : ")
            append(item.id)
        }

        // ✅ Highlight if selected
        holder.view.setBackgroundColor(
            if (selectedIds.contains(item.id)) Color.GRAY else Color.WHITE
        )

        holder.view.setOnClickListener {
            onItemSelected(item)
        }
    }

    override fun getItemCount() = items.size
}
