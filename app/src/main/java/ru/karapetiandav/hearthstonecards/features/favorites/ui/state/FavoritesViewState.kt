package ru.karapetiandav.hearthstonecards.features.favorites.ui.state

import ru.karapetiandav.hearthstonecards.features.cards.models.Card

sealed class FavoritesViewState
class FavoritesData(val data: List<Card>): FavoritesViewState()
object FavoritesLoading: FavoritesViewState()
class FavoritesError(val error: Throwable): FavoritesViewState()