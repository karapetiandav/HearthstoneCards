package ru.karapetiandav.hearthstonecards.di.modules

import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.karapetiandav.hearthstonecards.features.auth.AuthViewModel
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsDetailViewModel
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsViewModel
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepositoryImpl
import ru.karapetiandav.hearthstonecards.main.viewmodel.MainViewModel
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProviderImpl
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
        single<SchedulersProvider> { SchedulersProviderImpl() }
    }

    val mainModule = module {
        viewModel { MainViewModel(get()) }
    }

    val cardsModule = module {
        single<CardsRepository> { CardsRepositoryImpl(get()) }
        viewModel { CardsViewModel(get(), get(), get()) }
        viewModel { CardsDetailViewModel(get(), get()) }
    }

    val authModule = module {
        single { FirebaseAuth.getInstance() }
        viewModel { AuthViewModel(get(), get(), get()) }
    }
}