package ru.karapetiandav.hearthstonecards.features.cards.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_card_details.*
import kotlinx.android.synthetic.main.fragment_cards.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.*
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsDetailViewModel
import ru.karapetiandav.tinkoffintership.lifecycle.observe

class CardDetailsFragment : Fragment() {

    private val cardsViewModel: CardsDetailViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_card_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(cardsViewModel.state, ::onStateChanged)
        val activity = activity as? AppCompatActivity ?: return
        with(activity) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun onStateChanged(screenState: CardDetailsScreenState) {
        when (screenState) {
            is CardDetailsData -> {
                    toolbar.title = screenState.card.name
                }
            is CardDetailsFullData -> {
                with(screenState.detailedCard) {
                    Glide.with(this@CardDetailsFragment)
                        .load(img)
                        .into(card_img)
                    Glide.with(this@CardDetailsFragment)
                        .load(imgGold)
                        .into(gold_card_img)
                }
            }
            is CardDetailsLoading -> {}
            is CardDetailsError -> {}
        }
    }

}
