package ru.karapetiandav.hearthstonecards.features.cards.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import ru.karapetiandav.hearthstonecards.CardDetailsScreen
import ru.karapetiandav.hearthstonecards.FavoritesScreen
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.extensions.isUserLogged
import ru.karapetiandav.hearthstonecards.features.cards.models.*
import ru.karapetiandav.hearthstonecards.features.cards.ui.FavoriteMenuItem
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.ChipsViewModel
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsData
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsError
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsLoading
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsViewState
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.hearthstonecards.lifecycle.onNext
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider
import ru.karapetiandav.tinkoffintership.lifecycle.EventsQueue
import ru.terrakok.cicerone.Router
import timber.log.Timber

data class FilterDTO(
    val type: List<ChipsViewModel>,
    val cost: List<ChipsViewModel>,
    val playerClass: List<ChipsViewModel>
)

class CardsViewModel(
    private val cardsRepository: CardsRepository,
    private val router: Router,
    private val schedulers: SchedulersProvider,
    private val firebaseAuth: FirebaseAuth
) : BaseViewModel() {

    private val _state = MutableLiveData<CardsViewState>()
    val state: LiveData<CardsViewState>
        get() = _state
    val events = EventsQueue()

    private val _filterData = MutableLiveData<FilterDTO>()
    val filterData: LiveData<FilterDTO>
        get() = _filterData

    private var allCards: Map<String, List<Card>> = emptyMap()

    var itemPosition = 0
        set(value) {
            if (value < 0) {
                field = 0
                return
            }
            field = value
        }

    val isFavoriteVisible: Boolean
    get() = firebaseAuth.isUserLogged()

    fun loadCards() {
        cardsRepository.getCards()
            .toObservable()
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .doOnNext { cards ->
                allCards = cards
                val allTypes = (cards.values.flatten().mapNotNull { it.type }.distinctBy { it })
                val allCosts = (cards.values.flatten().mapNotNull { it.cost }.distinctBy { it })
                val allClasses =
                    (cards.values.flatten().mapNotNull { it.playerClass }.distinctBy { it })

                if (selectedTypes.isEmpty()) selectedTypes.addAll(allTypes)
                if (selectedCosts.isEmpty()) selectedCosts.addAll(allCosts)
                if (selectedPlayerClasses.isEmpty()) selectedPlayerClasses.addAll(allClasses)

                _filterData.value = FilterDTO(
                    allTypes.map { ChipsViewModel(it) },
                    allCosts.map { ChipsViewModel(it) },
                    allClasses.map { ChipsViewModel(it) }
                )
            }
            .map { applyFilters() }
            .map<CardsViewState> { cards -> CardsData(cards) }
            .startWith(CardsLoading)
            .onErrorReturn(::CardsError)
            .subscribe(_state::onNext) { th -> Timber.e(th) }
            .disposeOnViewModelDestroy()
    }

    private fun applyFilters(): List<Card> {
        return allCards.values
            .flatten()
            .filter {
                selectedTypes.contains(it.type)
                        && selectedCosts.contains(it.cost)
                        && selectedPlayerClasses.contains(it.playerClass)
            }
            .sortedBy { it.name }
    }

    var lastSearch: String? = null
        private set

    fun onSearchQuery(text: String) {
        lastSearch = text

        Observable.just(allCards)
            .map { allCards ->
                val findedCards = mutableMapOf<String, List<Card>>()
                allCards.forEach { (key, value) ->
                    findedCards[key] = value.filter {
                        it.name?.toLowerCase()?.contains(text.toLowerCase()) ?: false
                    }
                }
                findedCards
            }
            .subscribeOn(schedulers.computation())
            .observeOn(schedulers.mainThread())
            .map<CardsViewState> { cards -> CardsData(cards.values.flatten().sortedBy { it.name }) }
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

    private val selectedTypes = mutableListOf<Type>()
    private val selectedCosts = mutableListOf<Cost>()
    private val selectedPlayerClasses = mutableListOf<PlayerClass>()
    fun onItemUncheck(filterable: Filterable) {
        when (filterable) {
            is Type -> selectedTypes.remove(filterable)
            is Cost -> selectedCosts.remove(filterable)
            is PlayerClass -> selectedPlayerClasses.remove(filterable)
        }

        _state.onNext(CardsData(applyFilters()))
    }

    fun onItemCheck(filterable: Filterable) {
        when (filterable) {
            is Type -> selectedTypes.add(filterable)
            is Cost -> selectedCosts.add(filterable)
            is PlayerClass -> selectedPlayerClasses.add(filterable)
        }

        _state.onNext(CardsData(applyFilters()))
    }

    fun onFavoriteClick() {
        router.navigateTo(FavoritesScreen)
    }
}