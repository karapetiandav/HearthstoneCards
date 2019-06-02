package ru.karapetiandav.hearthstonecards.di.modules

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsViewModel
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepositoryImpl
import ru.karapetiandav.hearthstonecards.main.viewmodel.MainViewModel
import ru.karapetiandav.hearthstonecards.services.ApiService
import ru.karapetiandav.hearthstonecards.services.ConnectivityService
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

object Modules {
    val appModule = module {
        single { Cicerone.create() }
        single<Router> { get<Cicerone<Router>>().router }
        single<NavigatorHolder> { get<Cicerone<Router>>().navigatorHolder }
        single { ApiService(get(), get()) }
        single { ConnectivityService(get()) }
    }

    val mainModule = module {
        viewModel { MainViewModel(get()) }
    }

    val cardsModule = module {
        single<CardsRepository> { CardsRepositoryImpl(get()) }
        viewModel { CardsViewModel(get()) }
    }
}