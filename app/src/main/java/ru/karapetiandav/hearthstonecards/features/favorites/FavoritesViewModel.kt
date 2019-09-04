package ru.karapetiandav.hearthstonecards.features.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import ru.karapetiandav.hearthstonecards.CardsScreen
import ru.karapetiandav.hearthstonecards.base.BackPressHandler
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.features.favorites.ui.state.FavoritesData
import ru.karapetiandav.hearthstonecards.features.favorites.ui.state.FavoritesError
import ru.karapetiandav.hearthstonecards.features.favorites.ui.state.FavoritesLoading
import ru.karapetiandav.hearthstonecards.features.favorites.ui.state.FavoritesViewState
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.hearthstonecards.lifecycle.onNext
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider
import ru.karapetiandav.hearthstonecards.storage.db.RemoteDatabase
import ru.terrakok.cicerone.Router

class FavoritesViewModel(
    private val favoritesDatabase: RemoteDatabase,
    private val cardsRepository: CardsRepository,
    private val schedulersProvider: SchedulersProvider,
    private val router: Router
) : BaseViewModel() {

    private val _state = MutableLiveData<FavoritesViewState>()
    val state: LiveData<FavoritesViewState>
        get() = _state

    fun loadFavorites() {
        _state.value = FavoritesLoading

        favoritesDatabase.getFavoriteCardIds()
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMap { name -> cardsRepository.getSingleCard(name).toObservable() }
            .toList()
            .map<FavoritesViewState>(::FavoritesData)
            .onErrorReturn(::FavoritesError)
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.mainThread())
            .subscribe(_state::onNext)
            .disposeOnViewModelDestroy()
    }

    fun onBackPressed() {
        router.backTo(CardsScreen)
    }

}