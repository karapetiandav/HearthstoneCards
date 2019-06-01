package ru.karapetiandav.hearthstonecards.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import ru.karapetiandav.hearthstonecards.features.cards.models.Card

interface CardsApi {
    @GET("cards")
    // TODO: Положить ключ куда-нибудь
    @Headers("X-RapidAPI-Key:735b7bce73msh6fb4ab17b96daa1p14bd78jsnbd65e6ed3ce9")
    fun getCards(): Single<Map<String, List<Card>>>
}