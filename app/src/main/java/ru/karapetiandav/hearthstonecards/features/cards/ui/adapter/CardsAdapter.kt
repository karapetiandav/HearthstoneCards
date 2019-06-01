package ru.karapetiandav.hearthstonecards.features.cards.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.features.cards.models.Card

class CardsAdapter(private val cards: List<Card>): RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    override fun getItemCount(): Int = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = parent.inflate(R.layout.item_card)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(card: Card) {
            itemView.apply {
                card_name.text = card.name
                card_type.text = card.type
            }
        }
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}