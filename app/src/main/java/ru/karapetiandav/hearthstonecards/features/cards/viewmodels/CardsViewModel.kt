package ru.karapetiandav.hearthstonecards.features.cards.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsData
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsError
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsLoading
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsViewState
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.tinkoffintership.lifecycle.onNext

class CardsViewModel(cardsRepository: CardsRepository): BaseViewModel() {

    private val _state = MutableLiveData<CardsViewState>()
    val state: LiveData<CardsViewState>
        get() = _state

    init {
        cardsRepository.getCards()
            .toObservable()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .map<CardsViewState> { cards -> CardsData(cards) }
            .startWith(CardsLoading)
            .onErrorReturn(::CardsError)
            .subscribe(_state::onNext) { th -> Log.e(CardsViewModel::class.java.simpleName, "ERROR", th) }
            .disposeOnViewModelDestroy()
    }
}