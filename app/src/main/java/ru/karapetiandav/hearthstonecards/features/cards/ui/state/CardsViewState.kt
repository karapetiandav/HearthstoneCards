package ru.karapetiandav.hearthstonecards.features.cards.ui.state

import ru.karapetiandav.hearthstonecards.features.cards.models.Card

open class CardsViewState
class CardsData(val data: List<Card>): CardsViewState()
object CardsLoading : CardsViewState()
class CardsError(val error: Throwable) : CardsViewState()