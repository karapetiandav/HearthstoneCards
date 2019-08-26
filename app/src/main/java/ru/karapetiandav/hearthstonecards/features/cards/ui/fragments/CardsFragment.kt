package ru.karapetiandav.hearthstonecards.features.cards.ui.fragments


import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jakewharton.rxbinding3.appcompat.queryTextChangeEvents
import com.redmadrobot.lib.sd.LoadingStateDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.cards_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_cards.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.base.BackPressHandler
import ru.karapetiandav.hearthstonecards.base.fragment.BaseFragment
import ru.karapetiandav.hearthstonecards.base.layout.ErrorLayoutData
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.models.Filterable
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.CardsAdapter
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.ChipsAdapter
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.OnItemCheckListener
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.dividers.HorizontalSpaceItemDecoration
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsData
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsError
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsLoading
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsViewState
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsViewModel
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.FilterDTO
import ru.karapetiandav.hearthstonecards.lifecycle.observe
import ru.karapetiandav.hearthstonecards.lifecycle.Event
import timber.log.Timber
import java.util.concurrent.TimeUnit


class CardsFragment : BaseFragment(), BackPressHandler {

    private lateinit var screenState: LoadingStateDelegate

    private val cardsViewModel: CardsViewModel by viewModel()

    private var isOpen = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(bottom_app_bar)

        if (savedInstanceState == null) {
            cardsViewModel.loadCards()
        }

        screenState = LoadingStateDelegate(cards_list, loading, error_layout)

        observe(cardsViewModel.state, ::onStateChanged)
        observe(cardsViewModel.events, ::onEventReceived)
        observe(cardsViewModel.filterData, ::onFilterDataReceived)

        initBottomSheetLists()

        val bottomSheet = BottomSheetBehavior.from(bottom_sheet)
        onSlideBehavior(bottomSheet)
    }

    private val itemCheckListener = object : OnItemCheckListener {
        override fun onItemCheck(filterable: Filterable) {
            cardsViewModel.onItemCheck(filterable)
        }

        override fun onItemUncheck(filterable: Filterable) {
            cardsViewModel.onItemUncheck(filterable)
        }
    }

    private val typeAdapter = ChipsAdapter(itemCheckListener)
    private val costAdapter = ChipsAdapter(itemCheckListener)
    private val playerClassAdapter = ChipsAdapter(itemCheckListener)
    private fun initBottomSheetLists() {
        card_type_list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = typeAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(8, 16, 16))
        }
        card_cost_list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = costAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(8, 16, 16))
        }
        card_player_class_list.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = playerClassAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(8, 16, 16))
        }
    }

    private fun onFilterDataReceived(filterDTO: FilterDTO) {
        typeAdapter.setItems(filterDTO.type.sortedBy { it.filterable.value })
        costAdapter.setItems(filterDTO.cost.sortedBy { it.filterable.value.toIntOrNull() })
        playerClassAdapter.setItems(filterDTO.playerClass.sortedBy { it.filterable.value })
    }

    private fun onSlideBehavior(bottomSheet: BottomSheetBehavior<CardView>) {
        filter_fab.setOnClickListener {
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                isOpen = newState != BottomSheetBehavior.STATE_COLLAPSED
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                filter_fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
            }
        })
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
                screenState.showStub(
                    ErrorLayoutData(
                        R.drawable.ic_error_24dp,
                        getString(R.string.error_title),
                        getString(R.string.error_description)
                ))
                Timber.tag("CardsFragment").e(viewState.error)
            }
        }
    }

    private lateinit var cardsListLayoutManager: LinearLayoutManager
    private fun populateList(allCards: List<Card>) {
        // TODO: Workaround (Error LayoutManager is already attached)
        cardsListLayoutManager = LinearLayoutManager(context)
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
        activity?.menuInflater?.inflate(R.menu.bottom_menu, menu)

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

    override fun onPause() {
        super.onPause()

        cardsViewModel.itemPosition = cardsListLayoutManager.findFirstVisibleItemPosition()
        cardsViewModel.saveSearch(searchView.query.toString())
    }

    override fun onBackPressed(): Boolean {
        return if (isOpen) {
            val bottomSheet = BottomSheetBehavior.from(bottom_sheet)
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            true
        } else {
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            android.R.id.home -> {
                val bottomNavDrawerFragment = BottomMenuDialogFragment()
                bottomNavDrawerFragment.show(childFragmentManager, bottomNavDrawerFragment.tag)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}