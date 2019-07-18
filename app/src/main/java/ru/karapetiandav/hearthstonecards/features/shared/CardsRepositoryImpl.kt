package ru.karapetiandav.hearthstonecards.features.shared

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.services.ApiService

class CardsRepositoryImpl(private val apiService: ApiService) : CardsRepository {
    override fun getCards(): Single<Map<String, List<Card>>> {
        return apiService.getCardsApi().getCards()
            .subscribeOn(Schedulers.io())
    }

    private var card: Card? = null

    override fun setSelectedCard(card: Card) {
        this.card = card
    }

    override fun getSelectedCard(): Single<Card> {
        return Single.just(card)
    }

    override fun getSingleCard(name: String?): Single<Card> {
        return apiService.getCardsApi().getSingleCard(name!!)
            .map { list -> list.first() }
            .subscribeOn(Schedulers.io())
    }
}