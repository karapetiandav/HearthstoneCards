package ru.karapetiandav.hearthstonecards.features.cards.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.karapetiandav.hearthstonecards.CardDetailsScreen
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsData
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsError
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsLoading
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsViewState
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.tinkoffintership.lifecycle.EventsQueue
import ru.karapetiandav.tinkoffintership.lifecycle.onNext
import ru.terrakok.cicerone.Router

class CardsViewModel(private val cardsRepository: CardsRepository, private val router: Router) : BaseViewModel() {

    private val _state = MutableLiveData<CardsViewState>()
    val state: LiveData<CardsViewState>
        get() = _state
    val events = EventsQueue()

    private var allTypes: List<String> = emptyList()
    private val currentSelectedTypes = mutableListOf<String>()

    private var allCards: Map<String, List<Card>> = emptyMap()

    var itemPosition = 0
        set(value) {
            if (value < 0) {
                field = 0
                return
            }
            field = value
        }

    init {
        cardsRepository.getCards()
            .toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { cards ->
                allCards = cards
                allTypes = (cards.values.flatten().map { it.type }.toSet().toList())
                currentSelectedTypes.addAll(allTypes)
            }
            .map<CardsViewState> { cards -> CardsData(cards.values.flatten()) }
            .startWith(CardsLoading)
            .onErrorReturn(::CardsError)
            .subscribe(_state::onNext) { th -> Log.e(CardsViewModel::class.java.simpleName, "ERROR", th) }
            .disposeOnViewModelDestroy()
    }

    fun onSearchQuery(text: String) {
        Observable.just(allCards)
            .map { allCards ->
                val findedCards = mutableMapOf<String, List<Card>>()
                allCards.forEach { (key, value) ->
                    findedCards[key] = value.filter { it.name.toLowerCase().contains(text.toLowerCase()) }
                }
                findedCards
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .map<CardsViewState> { cards -> CardsData(cards.values.flatten()) }
            .onErrorReturn(::CardsError)
            .startWith(CardsLoading)
            .subscribe(_state::onNext)
            .disposeOnViewModelDestroy()
    }

    fun onCardClick(card: Card) {
        cardsRepository.setSelectedCard(card)
        router.navigateTo(CardDetailsScreen)
    }
}