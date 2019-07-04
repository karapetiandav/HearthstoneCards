package ru.karapetiandav.hearthstonecards.features.cards.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import ru.karapetiandav.hearthstonecards.CardDetailsScreen
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsData
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsError
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsLoading
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsViewState
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider
import ru.karapetiandav.tinkoffintership.lifecycle.EventsQueue
import ru.karapetiandav.tinkoffintership.lifecycle.onNext
import ru.terrakok.cicerone.Router
import timber.log.Timber

class CardsViewModel(
    private val cardsRepository: CardsRepository,
    private val router: Router,
    private val schedulers: SchedulersProvider
) : BaseViewModel() {

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

    fun loadCards() {
        cardsRepository.getCards()
            .toObservable()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .doOnNext { cards ->
                allCards = cards
                allTypes = (cards.values.flatten().mapNotNull { it.type }.toSet().toList())
                currentSelectedTypes.addAll(allTypes)
            }
            .map<CardsViewState> { cards -> CardsData(cards.values.flatten()) }
            .startWith(CardsLoading)
            .onErrorReturn(::CardsError)
            .subscribe(_state::onNext) { th -> Timber.tag(TAG()).e(th) }
            .disposeOnViewModelDestroy()
    }

    var lastSearch: String? = null
        private set

    fun onSearchQuery(text: String) {
        lastSearch = text

        Observable.just(allCards)
            .map { allCards ->
                val findedCards = mutableMapOf<String, List<Card>>()
                allCards.forEach { (key, value) ->
                    findedCards[key] = value.filter { it.name?.toLowerCase()?.contains(text.toLowerCase()) ?: false }
                }
                findedCards
            }
            .subscribeOn(schedulers.computation())
            .observeOn(schedulers.mainThread())
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

    fun saveSearch(query: String) {
        if (query.isEmpty()) return
        lastSearch = query
    }
}