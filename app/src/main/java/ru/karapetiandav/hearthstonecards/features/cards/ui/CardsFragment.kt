package ru.karapetiandav.hearthstonecards.features.cards.ui


import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.redmadrobot.lib.sd.LoadingStateDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_cards.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.base.fragment.BaseFragment
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.CardsAdapter
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsData
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsError
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsLoading
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsViewState
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsViewModel
import ru.karapetiandav.tinkoffintership.lifecycle.Event
import ru.karapetiandav.tinkoffintership.lifecycle.observe
import timber.log.Timber
import java.util.concurrent.TimeUnit


class CardsFragment : BaseFragment() {

    private lateinit var screenState: LoadingStateDelegate

    private val cardsViewModel: CardsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(ru.karapetiandav.hearthstonecards.R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            cardsViewModel.loadCards()
        }

        screenState = LoadingStateDelegate(cards_list, loading)

        observe(cardsViewModel.state, ::onStateChanged)
        observe(cardsViewModel.events, ::onEventReceived)
    }

    private fun onEventReceived(event: Event) {
    }

    private fun onStateChanged(viewState: CardsViewState) {
        when (viewState) {
            is CardsData -> {
                screenState.showContent()
                populateList(viewState.data)
            }
            is CardsLoading -> {
                screenState.showLoading()
            }
            is CardsError -> {
                Timber.tag("CardsFragment").e(viewState.error)
            }
        }
    }

    private val cardsListLayoutManager = LinearLayoutManager(context)
    private fun populateList(allCards: List<Card>) {
        cards_list.layoutManager = cardsListLayoutManager
        cards_list.adapter = CardsAdapter(allCards) {
            cardsViewModel.onCardClick(it)
        }
        // TODO: Подумать как сделать получше (неявная зависимость от ViewModel)
        cards_list.scrollToPosition(cardsViewModel.itemPosition)
    }

    private val MINIMAL_SEARCH_TEXT_LENGTH: Int = 3
    private val SEARCH_DELAY: Long = 300

    private lateinit var searchView: SearchView
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.cards_menu, menu)

        val searchManager = context?.getSystemService(SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.cards_search)
        searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

        searchView
            .queryTextChangeEvents()
            .skipInitialValue()
            .debounce(SEARCH_DELAY, TimeUnit.MILLISECONDS)
            .map { it.queryText.toString() }
            .filter { it.isEmpty() || it.length >= MINIMAL_SEARCH_TEXT_LENGTH }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { cardsViewModel.onSearchQuery(it) }
            .disposeOnViewDestroy()


        cardsViewModel.lastSearch?.let {
            searchItem.expandActionView()
            searchView.setQuery(it, true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        cardsViewModel.itemPosition = cardsListLayoutManager.findFirstVisibleItemPosition()
        cardsViewModel.saveSearch(searchView.query.toString())
    }
}