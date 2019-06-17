package ru.karapetiandav.hearthstonecards.features.cards.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.redmadrobot.lib.sd.LoadingStateDelegate
import kotlinx.android.synthetic.main.fragment_card_details.*
import kotlinx.android.synthetic.main.fragment_cards.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.*
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsDetailViewModel
import ru.karapetiandav.tinkoffintership.lifecycle.observe

class CardDetailsFragment : Fragment() {

    private lateinit var screenState: LoadingStateDelegate

    private val cardsViewModel: CardsDetailViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_card_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        screenState = LoadingStateDelegate(detailsGroup, loading)

        observe(cardsViewModel.state, ::onStateChanged)
        val activity = activity as? AppCompatActivity ?: return
        with(activity) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { cardsViewModel.onBackPressed() }
        }
        Glide.with(this)
            .load(R.drawable.cardback)
            .into(card_img)
        Glide.with(this)
            .load(R.drawable.cardback)
            .into(gold_card_img)
    }

    private fun onStateChanged(state: CardDetailsScreenState) {
        when (state) {
            is CardDetailsData -> {
                toolbar.title = state.card.name
            }
            is CardDetailsFullData -> {
                screenState.showContent()
                with(state.detailedCard) {
                    Glide.with(this@CardDetailsFragment)
                        .load(img)
                        .placeholder(R.drawable.cardback)
                        .into(card_img)
                    Glide.with(this@CardDetailsFragment)
                        .load(imgGold)
                        .placeholder(R.drawable.cardback)
                        .into(gold_card_img)
                    card_name.text = name
                    card_set_text.text = cardSet
                    card_type_text.text = type
                    card_faction_text.text = faction
                    card_rarity_text.text = rarity
                    card_player_class_text.text = playerClass
                    card_cost_text.text = cost.toString()
                }
            }
            is CardDetailsLoading -> {
                screenState.showLoading()
            }
            is CardDetailsError -> {
                Toast.makeText(context, state.error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
