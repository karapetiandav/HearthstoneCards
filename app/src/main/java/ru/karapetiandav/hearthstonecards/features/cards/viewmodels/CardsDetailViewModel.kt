package ru.karapetiandav.hearthstonecards.features.cards.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import ru.karapetiandav.hearthstonecards.CardsScreen
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.*
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.hearthstonecards.lifecycle.onNext
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider
import ru.karapetiandav.hearthstonecards.storage.db.UserFavoriteDatabase
import ru.terrakok.cicerone.Router
import timber.log.Timber

class CardsDetailViewModel(
    private val cardsRepository: CardsRepository,
    private val router: Router,
    authClient: FirebaseAuth,
    private val userFavoriteDatabase: UserFavoriteDatabase,
    private val schedulers: SchedulersProvider
) : BaseViewModel() {

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
            .subscribe(_state::onNext) { th -> Timber.e(th) }
            .disposeOnViewModelDestroy()

        val selectedCardObservable = cardsRepository.getSelectedCard()
            .toObservable()
            .flatMap { card -> cardsRepository.getSingleCard(card.name).toObservable() }

        val isFavoriteObservable = userFavoriteDatabase.getFavoriteCardIds()
            .toObservable()

        Observable.zip<Card, List<String>, Card>(
            selectedCardObservable,
            isFavoriteObservable,
            BiFunction { card, favorites ->
                val isFavorite = favorites.contains(card.cardId)
                return@BiFunction card.copy(isFavorite = isFavorite)
            })
            .map<CardDetailsScreenState> { CardDetailsFullData(it) }
            .onErrorReturn(::CardDetailsError)
            .startWith(CardDetailsLoading)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.mainThread())
            .subscribe(_state::onNext) { th -> Timber.e(th) }
            .disposeOnViewModelDestroy()
    }

    fun onBackPressed() {
        router.backTo(CardsScreen)
    }

    fun onFavoriteClick() {
        cardsRepository.getSelectedCard()
            .flatMapCompletable { userFavoriteDatabase.saveFavoriteCardId(it.cardId!!) }
            .toObservable<Card>() // TODO: Что из этого вообще получится?? Completable -> Observable
            .observeOn(schedulers.mainThread())
            .subscribe({ Timber.d(it.toString()) }, { Timber.e(it) })
            .disposeOnViewModelDestroy()
    }


}