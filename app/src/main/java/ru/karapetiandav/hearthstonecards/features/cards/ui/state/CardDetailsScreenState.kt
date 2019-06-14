package ru.karapetiandav.hearthstonecards.features.cards.ui.state

import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.models.DetailedCard

sealed class CardDetailsScreenState
class CardDetailsData(val card: Card): CardDetailsScreenState()
class CardDetailsFullData(val detailedCard: DetailedCard): CardDetailsScreenState()
object CardDetailsLoading: CardDetailsScreenState()
class CardDetailsError(val error: Throwable): CardDetailsScreenState()