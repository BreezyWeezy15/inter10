package com.interview.exercise

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ItemAdapter(
    private var items: List<Item>,
    private var selectedId: Int?,
    private val onItemSelected: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    fun updateSelection(newSelectedId: Int?) {
        selectedId = newSelectedId
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
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
        holder.view.setBackgroundColor(
            if (item.id == selectedId) Color.LTGRAY else Color.WHITE
        )

        holder.view.setOnClickListener {
            onItemSelected(item)
        }
    }

    override fun getItemCount() = items.size
}