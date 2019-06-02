package ru.karapetiandav.hearthstonecards.features.cards.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chips.view.*
import ru.karapetiandav.hearthstonecards.R

class ChipsAdapter(private val chipsText: Array<String>): RecyclerView.Adapter<ChipsAdapter.ChipsViewHolder>() {

    override fun getItemCount(): Int = chipsText.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipsViewHolder {
        return ChipsViewHolder(parent.inflate(R.layout.item_chips))
    }

    override fun onBindViewHolder(holder: ChipsViewHolder, position: Int) {
        holder.bind(chipsText[position])
    }

    class ChipsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(text: String) {
            itemView.chip.text = text
        }
    }
}