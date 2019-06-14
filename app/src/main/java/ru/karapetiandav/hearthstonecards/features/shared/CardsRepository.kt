package ru.karapetiandav.hearthstonecards.features.shared

import io.reactivex.Single
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.models.DetailedCard

interface CardsRepository {
    fun getCards(): Single<Map<String, List<Card>>>
    fun getSelectedCard(): Single<Card>
    fun setSelectedCard(card: Card)
    fun getSingleCard(name: String): Single<List<DetailedCard>>
}