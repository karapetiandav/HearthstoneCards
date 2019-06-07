package ru.karapetiandav.hearthstonecards.features.cards.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_chips.view.*


class ChipsAdapter(
    private val chipsText: List<String>,
    private val onItemCheckListener: OnItemCheckListener
) : RecyclerView.Adapter<ChipsAdapter.ChipsViewHolder>() {

    override fun getItemCount(): Int = chipsText.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipsViewHolder {
        return ChipsViewHolder(parent.inflate(ru.karapetiandav.hearthstonecards.R.layout.item_chips))
    }

    override fun onBindViewHolder(holder: ChipsViewHolder, position: Int) {
        val currentItem = chipsText[position]
        holder.bind(currentItem)
        holder.setOnClickListener {
            if (holder.chip.isChecked) {
                onItemCheckListener.onItemCheck(currentItem)
            } else {
                onItemCheckListener.onItemUncheck(currentItem)
            }
        }
    }

    class ChipsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val chip: Chip = itemView.chip

        fun bind(text: String) {
            itemView.chip.isChecked = true
            itemView.chip.text = text
        }

        fun setOnClickListener(listener: () -> Unit) {
            itemView.chip.setOnClickListener { listener() }
        }
    }
}

interface OnItemCheckListener {
    fun onItemCheck(item: String)
    fun onItemUncheck(item: String)
}