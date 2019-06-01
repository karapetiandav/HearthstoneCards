package ru.karapetiandav.hearthstonecards.main.viewmodel

import ru.karapetiandav.hearthstonecards.CardsScreen
import ru.karapetiandav.hearthstonecards.base.viewmodel.BaseViewModel
import ru.terrakok.cicerone.Router

class MainViewModel(private val router: Router) : BaseViewModel() {

    fun onActivityCreate() {
        router.newRootScreen(CardsScreen)
    }

}