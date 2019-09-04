package ru.karapetiandav.hearthstonecards

import androidx.fragment.app.Fragment
import ru.karapetiandav.hearthstonecards.features.auth.AuthFragment
import ru.karapetiandav.hearthstonecards.features.cards.ui.fragments.CardsFragment
import ru.karapetiandav.hearthstonecards.features.cards.ui.fragments.CardDetailsFragment
import ru.karapetiandav.hearthstonecards.features.favorites.FavoritesFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object CardsScreen: SupportAppScreen() {
    override fun getFragment(): Fragment {
        return CardsFragment()
    }
}

object CardDetailsScreen: SupportAppScreen() {
    override fun getFragment(): Fragment {
        return CardDetailsFragment()
    }
}

object AuthScreen: SupportAppScreen() {
    override fun getFragment(): Fragment {
        return AuthFragment()
    }
}

object FavoritesScreen: SupportAppScreen() {
    override fun getFragment(): Fragment {
        return FavoritesFragment()
    }
}