package ru.karapetiandav.hearthstonecards.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.models.DetailedCard

interface CardsApi {
    @GET("cards")
    // TODO: Положить ключ куда-нибудь
    @Headers("X-RapidAPI-Key:735b7bce73msh6fb4ab17b96daa1p14bd78jsnbd65e6ed3ce9")
    fun getCards(@Query("cost") cost: Int): Single<Map<String, List<Card>>>

    @GET("cards/{name}")
    @Headers("X-RapidAPI-Key:735b7bce73msh6fb4ab17b96daa1p14bd78jsnbd65e6ed3ce9")
    fun getSingleCard(@Path("name") name: String): Single<List<DetailedCard>>
}