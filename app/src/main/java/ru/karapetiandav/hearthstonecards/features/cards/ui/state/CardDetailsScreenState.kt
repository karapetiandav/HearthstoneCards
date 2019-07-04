package ru.karapetiandav.hearthstonecards.features.cards.ui.state

import ru.karapetiandav.hearthstonecards.features.cards.models.Card

sealed class CardDetailsScreenState
class CardDetailsData(val card: Card): CardDetailsScreenState()
class CardDetailsFullData(val card: Card): CardDetailsScreenState()
object CardDetailsLoading: CardDetailsScreenState()
class CardDetailsError(val error: Throwable): CardDetailsScreenState()