package ru.karapetiandav.hearthstonecards.features.cards.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.features.cards.models.Card

class CardsAdapter(private val cards: List<Card>, private val itemClick: (Card) -> Unit) :
    RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {

    override fun getItemCount(): Int = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = parent.inflate(R.layout.item_card)
        return CardViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    class CardViewHolder(itemView: View, private val itemClick: (Card) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(card: Card) {
            itemView.apply {
                setOnClickListener { itemClick(card) }
                card_name.text = card.name
                card_type.text = card.type?.value
                card_player_class.text = card.playerClass?.value
                card_cost.text = card.cost?.value
            }
        }
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}