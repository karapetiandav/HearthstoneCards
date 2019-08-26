package ru.karapetiandav.hearthstonecards.features.cards.models

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement

data class Card(
    val cardId: String?,
    val name: String?,
    val cardSet: String?,
    val type: Type?,
    val faction: String?,
    val rarity: String?,
    val cost: Cost?,
    val attack: Int?,
    val health: Int?,
    val text: String?,
    val flavor: String?,
    val artist: String?,
    val collectible: Boolean?,
    val elite: Boolean?,
    val race: String?,
    val playerClass: PlayerClass?,
    val img: String?,
    val imgGold: String?,
    val locale: String?,
    val isFavorite: Boolean = false
)

class CardDeserializer : JsonDeserializer<Card> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): Card {
        val jsonObject = json?.asJsonObject
        return Card(
            jsonObject?.get("cardId")?.asString,
            jsonObject?.get("name")?.asString,
            jsonObject?.get("cardSet")?.asString,
            Type(jsonObject?.get("type")?.asString ?: ""),
            jsonObject?.get("faction")?.asString,
            jsonObject?.get("rarity")?.asString,
            Cost((jsonObject?.get("cost")?.asInt ?: 0).toString()),
            jsonObject?.get("attack")?.asInt,
            jsonObject?.get("health")?.asInt,
            jsonObject?.get("text")?.asString,
            jsonObject?.get("flavor")?.asString,
            jsonObject?.get("artist")?.asString,
            jsonObject?.get("collectible")?.asBoolean,
            jsonObject?.get("elite")?.asBoolean,
            jsonObject?.get("race")?.asString,
            PlayerClass(jsonObject?.get("playerClass")?.asString ?: "Unknown"),
            jsonObject?.get("img")?.asString,
            jsonObject?.get("imgGold")?.asString,
            jsonObject?.get("locale")?.asString
        )
    }
}

data class Type(override val value: String) : Filterable
data class Cost(override val value: String) : Filterable
data class PlayerClass(override val value: String) : Filterable

interface Filterable {
    val value: String
}