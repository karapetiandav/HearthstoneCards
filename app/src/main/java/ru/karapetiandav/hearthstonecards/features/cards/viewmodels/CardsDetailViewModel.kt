package ru.karapetiandav.hearthstonecards.features.cards.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.karapetiandav.hearthstonecards.CardsScreen
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.*
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.hearthstonecards.lifecycle.onNext
import ru.terrakok.cicerone.Router
import timber.log.Timber

class CardsDetailViewModel(cardsRepository: CardsRepository, private val router: Router) : BaseViewModel() {

    private val _state = MutableLiveData<CardDetailsScreenState>()
    val state: LiveData<CardDetailsScreenState>
        get() = _state

    init {
        cardsRepository.getSelectedCard()
            .toObservable()
            .map<CardDetailsScreenState> { CardDetailsData(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn(::CardDetailsError)
            .startWith(CardDetailsLoading)
            .subscribe(_state::onNext) { th -> Timber.tag(TAG()).e(th) }
            .disposeOnViewModelDestroy()

        cardsRepository.getSelectedCard()
            .toObservable()
            .flatMap { card -> cardsRepository.getSingleCard(card.name).toObservable() }
            .map<CardDetailsScreenState> { CardDetailsFullData(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn(::CardDetailsError)
            .startWith(CardDetailsLoading)
            .subscribe(_state::onNext) { th -> Timber.tag(TAG()).e(th) }
            .disposeOnViewModelDestroy()
    }

    fun onBackPressed() {
        router.backTo(CardsScreen)
    }


}