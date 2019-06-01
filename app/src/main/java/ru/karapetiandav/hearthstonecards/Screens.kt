package ru.karapetiandav.hearthstonecards

import androidx.fragment.app.Fragment
import ru.karapetiandav.hearthstonecards.features.cards.ui.CardsFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object CardsScreen: SupportAppScreen() {
    override fun getFragment(): Fragment {
        return CardsFragment()
    }
}