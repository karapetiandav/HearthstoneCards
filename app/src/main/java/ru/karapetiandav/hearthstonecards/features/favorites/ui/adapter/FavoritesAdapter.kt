package ru.karapetiandav.hearthstonecards.features.favorites.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card_details_content.*
import kotlinx.android.synthetic.main.item_favorite.view.*
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.inflate

class FavoritesAdapter(private val cards: List<Card>) :
    RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    override fun getItemCount(): Int = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = parent.inflate(R.layout.item_favorite)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(card: Card) {
            Glide.with(itemView)
                .load(card.img)
                .placeholder(R.drawable.cardback)
                .into(itemView.favorite_card_img)

            itemView.favorite_card_title.text = card.name
        }

    }

}