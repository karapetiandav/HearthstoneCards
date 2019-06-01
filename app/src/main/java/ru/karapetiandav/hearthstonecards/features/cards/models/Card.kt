package ru.karapetiandav.hearthstonecards.features.cards.models

class Card(
    val cardId: String,
    val dbfId: String,
    val name: String,
    val cardSet: String,
    val type: String,
    val text: String,
    val playerClass: String,
    val locale: String
)