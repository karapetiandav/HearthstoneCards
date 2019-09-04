package ru.karapetiandav.hearthstonecards.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.karapetiandav.hearthstonecards.extensions.toUser
import ru.karapetiandav.hearthstonecards.features.auth.AuthViewModel
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsDetailViewModel
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsViewModel
import ru.karapetiandav.hearthstonecards.features.favorites.FavoritesViewModel
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepositoryImpl
import ru.karapetiandav.hearthstonecards.main.viewmodel.MainViewModel
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProvider
import ru.karapetiandav.hearthstonecards.providers.rx.SchedulersProviderImpl
import ru.karapetiandav.hearthstonecards.services.ApiService
import ru.karapetiandav.hearthstonecards.services.ConnectivityService
import ru.karapetiandav.hearthstonecards.storage.db.RemoteDatabase
import ru.karapetiandav.hearthstonecards.storage.db.UserFavoriteDatabase
import ru.karapetiandav.hearthstonecards.storage.db.UserFavoriteReferenceProvider
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
        single {
            val db = FirebaseDatabase.getInstance()
            db.setPersistenceEnabled(true)
            db
        }
        single { UserFavoriteReferenceProvider(get<FirebaseAuth>().currentUser?.toUser(), get()) }
        single<RemoteDatabase> { UserFavoriteDatabase(get()) }
        viewModel { CardsViewModel(get(), get(), get(), get()) }
        viewModel { CardsDetailViewModel(get(), get(), get(), get()) }
    }

    val authModule = module {
        single { FirebaseAuth.getInstance() }
        viewModel { AuthViewModel(get(), get(), get()) }
    }

    val favoritesModule = module {
        viewModel { FavoritesViewModel(get(), get(), get(), get()) }
    }
}