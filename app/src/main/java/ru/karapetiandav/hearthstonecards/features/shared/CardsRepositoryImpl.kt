package ru.karapetiandav.hearthstonecards.features.shared

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.network.ApiService

class CardsRepositoryImpl(private val apiService: ApiService) : CardsRepository {
    override fun getCards(): Single<Map<String, List<Card>>> {
        return apiService.getCardsApi().getCards()
            .subscribeOn(Schedulers.io())
    }
}