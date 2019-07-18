package ru.karapetiandav.hearthstonecards.features.cards.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chips.view.*
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.features.cards.models.Filterable

data class ChipsViewModel(val filterable: Filterable, val checked: Boolean)

class ChipsAdapter(
    var chipsModel: List<ChipsViewModel>,
    private val onItemCheckListener: OnItemCheckListener
) : RecyclerView.Adapter<ChipsAdapter.ChipsViewHolder>() {

    private val itemStateArray = mutableMapOf<Int, Boolean>()

    override fun getItemCount(): Int = chipsModel.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipsViewHolder {
        return ChipsViewHolder(parent.inflate(R.layout.item_chips))
    }

    override fun onBindViewHolder(holder: ChipsViewHolder, position: Int) {
        val currentItem = chipsModel[position]
        holder.bind(currentItem, onItemCheckListener)
    }

    inner class ChipsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(chipsViewModel: ChipsViewModel, onItemCheckListener: OnItemCheckListener) {
            itemView.chip.text = chipsViewModel.filterable.value
            itemView.chip.isChecked = itemStateArray[adapterPosition] != false

            itemView.setOnClickListener {
                if (itemStateArray[adapterPosition] == false) {
                    itemView.chip.isChecked = true
                    itemStateArray[adapterPosition] = true
                    onItemCheckListener.onItemCheck(chipsViewModel.filterable)
                } else {
                    itemView.chip.isChecked = false
                    itemStateArray[adapterPosition] = false
                    onItemCheckListener.onItemUncheck(chipsViewModel.filterable)
                }
            }
        }
    }
}

interface OnItemCheckListener {
    fun onItemCheck(filterable: Filterable)
    fun onItemUncheck(filterable: Filterable)
}