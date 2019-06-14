package ru.karapetiandav.hearthstonecards.features.cards.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.*
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.tinkoffintership.lifecycle.onNext

class CardsDetailViewModel(cardsRepository: CardsRepository) : BaseViewModel() {

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
            .subscribe(_state::onNext) { th -> Log.e(this::class.java.simpleName, "ERROR", th) }
            .disposeOnViewModelDestroy()

        cardsRepository.getSelectedCard()
            .toObservable()
            .flatMap { card -> cardsRepository.getSingleCard(card.name).toObservable() }
            .map<CardDetailsScreenState> {
                val card = it.first()
                CardDetailsFullData(card)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn(::CardDetailsError)
            .startWith(CardDetailsLoading)
            .subscribe(_state::onNext) { th -> Log.e(this::class.java.simpleName, "ERROR", th) }
            .disposeOnViewModelDestroy()
    }


}