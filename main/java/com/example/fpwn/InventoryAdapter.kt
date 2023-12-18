package com.example.fpwn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InventoryAdapter(
    private var itemList: List<InventoryItem>,
    private val onItemDeleteListener: OnItemDeleteListener
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvUnit: TextView = itemView.findViewById(R.id.tvUnit)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }


    fun setItems(newItemList: List<InventoryItem>) {
        itemList = newItemList
        notifyDataSetChanged()
    }


    fun getItems(): List<InventoryItem> {
        return itemList
    }

    interface OnItemDeleteListener {
        fun onItemDelete(item: InventoryItem)
    }

    interface OnEditClickListener {
        fun onEditClick(item: InventoryItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: InventoryItem = itemList[position]
        holder.tvDescription.text = item.description
        holder.tvUnit.text = item.unit
        holder.tvQuantity.text = item.quantity.toString()


        holder.btnDelete.setOnClickListener {

            onItemDeleteListener.onItemDelete(item)
        }




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}