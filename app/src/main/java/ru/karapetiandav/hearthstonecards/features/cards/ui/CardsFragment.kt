package ru.karapetiandav.hearthstonecards.features.cards.ui


import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.redmadrobot.lib.sd.LoadingStateDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.cards_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_cards.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.base.ShowFilterSheet
import ru.karapetiandav.hearthstonecards.base.fragment.BaseFragment
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.CardsAdapter
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.ChipsAdapter
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.OnItemCheckListener
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
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        screenState = LoadingStateDelegate(cards_list, loading)

        observe(cardsViewModel.state, ::onStateChanged)
        observe(cardsViewModel.events, ::onEventReceived)
    }

    private fun onEventReceived(event: Event) {
        when (event) {
            is ShowFilterSheet -> {
                populateBottomSheet(event.types)
                BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
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

    private fun populateList(cardsWithCategories: Map<String, List<Card>>) {
        cards_list.layoutManager = LinearLayoutManager(context)
        val allCards = cardsWithCategories.values.flatten()
        cards_list.adapter = CardsAdapter(allCards) {
            cardsViewModel.onCardClick(it)
        }
    }

    private fun populateBottomSheet(types: List<String>) {
        if (card_type_list.adapter != null) return
        card_type_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        card_type_list.adapter = ChipsAdapter(types, object : OnItemCheckListener {
            override fun onItemCheck(item: String) {
                cardsViewModel.onItemCheck(item)
            }

            override fun onItemUncheck(item: String) {
                cardsViewModel.onItemUncheck(item)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.cards_menu, menu)
        val searchManager = context?.getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.cards_search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))

        searchView
            .queryTextChangeEvents()
            .skipInitialValue()
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { it.queryText.toString() }
            .filter { it.isEmpty() || it.length >= 3 }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { cardsViewModel.onSearchQuery(it) }
            .disposeOnViewDestroy()
    }
}
