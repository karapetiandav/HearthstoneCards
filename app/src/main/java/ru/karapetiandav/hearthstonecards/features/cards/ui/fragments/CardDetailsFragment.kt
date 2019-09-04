package ru.karapetiandav.hearthstonecards.features.cards.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding3.view.clicks
import com.redmadrobot.lib.sd.LoadingStateDelegate
import kotlinx.android.synthetic.main.card_details_content.*
import kotlinx.android.synthetic.main.fragment_card_details.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.karapetiandav.hearthstonecards.R
import ru.karapetiandav.hearthstonecards.base.BackPressHandler
import ru.karapetiandav.hearthstonecards.base.fragment.BaseFragment
import ru.karapetiandav.hearthstonecards.base.layout.ErrorLayoutData
import ru.karapetiandav.hearthstonecards.extensions.setVisible
import ru.karapetiandav.hearthstonecards.extensions.setupBackButton
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.*
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsDetailViewModel
import ru.karapetiandav.hearthstonecards.lifecycle.observe
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CardDetailsFragment : BaseFragment(), BackPressHandler {

    private lateinit var screenState: LoadingStateDelegate

    private val cardsViewModel: CardsDetailViewModel by viewModel()

    private val authClient: FirebaseAuth by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_card_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setupBackButton(toolbar, cardsViewModel::onBackPressed)

        screenState = LoadingStateDelegate(loadingView = loading, stubView = error_layout_details)

        observe(cardsViewModel.state, ::onStateChanged)

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
                with(state.card) {
                    Glide.with(this@CardDetailsFragment)
                        .load(img)
                        .placeholder(R.drawable.cardback)
                        .into(card_img)
                    Glide.with(this@CardDetailsFragment)
                        .load(imgGold)
                        .placeholder(R.drawable.cardback)
                        .into(gold_card_img)
                    card_name.text = name
                    cardSet?.let {
                        card_set.visibility = VISIBLE
                        card_set.text = getString(R.string.card_detail_set, cardSet)
                    }

                    cardSet?.let {
                        card_title.visibility = VISIBLE
                        card_title.text = getString(R.string.card_detail_type, type?.value)
                    }

                    faction?.let {
                        card_faction.visibility = VISIBLE
                        card_faction.text = getString(R.string.card_detail_faction, it)
                    }

                    rarity?.let {
                        card_rarity.visibility = VISIBLE
                        card_rarity.text = getString(R.string.card_detail_rarity, rarity)
                    }

                    cost?.let {
                        card_type.visibility = VISIBLE
                        card_type.text = getString(R.string.card_detail_cost, cost.value)
                    }

                    playerClass?.let {
                        card_player_class.visibility = VISIBLE
                        card_player_class.text =
                            getString(R.string.card_detail_player_class, playerClass.value)
                    }

                    favorite_btn.setVisible(!isFavorite && (authClient.currentUser != null))
                    favorite_btn.clicks()
                        .throttleFirst(1000, TimeUnit.MILLISECONDS)
                        .subscribe({ cardsViewModel.onFavoriteClick() }, { Timber.e(it) })
                }
            }
            is CardDetailsLoading -> {
                screenState.showLoading()
            }
            is CardDetailsError -> {
                screenState.showStub(
                    ErrorLayoutData(
                        R.drawable.ic_error_24dp,
                        getString(R.string.error_title),
                        getString(R.string.error_description)
                    )
                )
            }
        }
    }

    override fun onBackPressed(): Boolean = false

}
