package ru.karapetiandav.hearthstonecards.features.cards.ui


import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.redmadrobot.lib.sd.LoadingStateDelegate
import kotlinx.android.synthetic.main.cards_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_cards.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.features.cards.models.Card
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.CardsAdapter
import ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.ChipsAdapter
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsData
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsError
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsLoading
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsViewState
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsViewModel
import ru.karapetiandav.tinkoffintership.lifecycle.observe

class CardsFragment : Fragment() {

    private lateinit var screenState: LoadingStateDelegate

    private val cardsViewModel: CardsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        screenState = LoadingStateDelegate(cards_list, loading)

        observe(cardsViewModel.state, ::onStateChanged)
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
                TODO("Implement")
            }
        }
    }

    private fun populateList(cardsWithCategories: Map<String, List<Card>>) {
        cards_list.layoutManager = LinearLayoutManager(context)
        val allCards = cardsWithCategories.values.flatten()
        cards_list.adapter = CardsAdapter(allCards)
        card_type_list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val chips = allCards.map { it.type }.toSet().toTypedArray()
        card_type_list.adapter = ChipsAdapter(chips)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.cards_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.cards_filter -> {
                BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_HALF_EXPANDED
                true
            }
            else -> {
                false
            }
        }
    }

}
