package ru.karapetiandav.hearthstonecards

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsData
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsError
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsLoading
import ru.karapetiandav.hearthstonecards.features.cards.ui.state.CardsViewState
import ru.karapetiandav.hearthstonecards.features.cards.viewmodels.CardsViewModel
import ru.karapetiandav.hearthstonecards.features.shared.CardsRepository
import ru.terrakok.cicerone.Router

inline fun <reified T> mock() = Mockito.mock(T::class.java)

class CardsViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    val cardsRepository = Mockito.mock(CardsRepository::class.java)
    val router = Mockito.mock(Router::class.java)

    val stateObserver = mock<Observer<CardsViewState>>()

    val viewModel by lazy { CardsViewModel(cardsRepository, router, SchedulersProviderTest()) }

    @Before
    fun initTest() {
        reset(cardsRepository, router)
    }

    @Test
    fun `Test success data receiving`() {
        // mock data
        val response = mock<Map<String, List<Card>>>()
        // if this method called, return my mocked data
        whenever(cardsRepository.getCards()).thenReturn(Single.just(response))

        // test observe
        viewModel.state.observeForever(stateObserver)
        viewModel.loadCards()

        val argumentCaptor = ArgumentCaptor.forClass(CardsViewState::class.java)
        val expectedLoadingState = CardsLoading
        val expectedDataState = CardsData(response.values.flatten())

        argumentCaptor.run {
            // Verify that stateObserver called 2 times and save every change in allValues
            verify(stateObserver, times(2)).onChanged(capture())
            val (loading, data) = allValues
            // Check correct or not correct screen states received stateObserver
            assertEquals(loading, expectedLoadingState)
            assertEquals((data as CardsData).data, expectedDataState.data)
        }
    }

    @Test
    fun `Test error data receiving`() {
        val response = Throwable("Error")
        whenever(cardsRepository.getCards()).thenReturn(Single.error(response))

        viewModel.state.observeForever(stateObserver)
        viewModel.loadCards()

        val argumentCaptor = ArgumentCaptor.forClass(CardsViewState::class.java)
        val expectedLoadingState = CardsLoading
        val expectedErrorState = CardsError(response)

        argumentCaptor.run {
            verify(stateObserver, times(2)).onChanged(capture())
            val (loading, error) = allValues
            assertEquals(loading, expectedLoadingState)
            assertEquals((error as CardsError).error, expectedErrorState.error)
        }
    }
}