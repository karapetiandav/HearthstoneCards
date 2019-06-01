package ru.karapetiandav.hearthstonecards.features.shared

import io.reactivex.Single
import ru.karapetiandav.hearthstonecards.features.cards.models.Card

interface CardsRepository {
    fun getCards(): Single<Map<String, List<Card>>>
}