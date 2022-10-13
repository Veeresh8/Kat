package com.droid.kat

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException
import javax.inject.Inject

@ExperimentalCoroutinesApi
class KatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `check if loading state is encountered while fetching cats`() = runTest {
        val viewModel = KatViewModel(mockk(), mockk(), mockk())

        val job = launch {
            viewModel.homeUiState.collect()
        }

        assertTrue(viewModel.homeUiState.value.loading)
        job.cancel()
    }

    @Test
    fun `check if error state is encountered while fetching cats`() = runTest {
        val catDataUseCase = mockk<CatDataUseCase>()
        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Error(Exception("Server is down today!"))

        val viewModel = KatViewModel(catDataUseCase, ExceptionMapper(), mockk(relaxed = true))

        val job = launch {
            viewModel.homeUiState.collect()
        }

        assertTrue(viewModel.homeUiState.value.loading)

        viewModel.getCatData()

        runCurrent()

        assertTrue(viewModel.homeUiState.value.error == "Could not reach our servers, try again!")
        assertTrue(viewModel.homeUiState.value.catList == null)

        job.cancel()
    }

    @Test
    fun `check if network error state is encountered while fetching cats`() = runTest {
        val catDataUseCase = mockk<CatDataUseCase>()
        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Error(
            SocketTimeoutException("Could not reach the server")
        )

        val viewModel = KatViewModel(catDataUseCase, ExceptionMapper(), mockk(relaxed = true))

        val job = launch {
            viewModel.homeUiState.collect()
        }

        assertTrue(viewModel.homeUiState.value.loading)

        viewModel.getCatData()

        runCurrent()

        assertTrue(viewModel.homeUiState.value.error == "Please check your network connection")
        assertTrue(viewModel.homeUiState.value.catList == null)

        job.cancel()
    }

    @Test
    fun `check if cat posts are fetched`() = runTest {
        val katConfig = mockk<KatConfig>(relaxed = true)
        every { katConfig.currentPage } returns 1
        every { katConfig.pageLimit } returns 10

        val catDataUseCase = mockk<CatDataUseCase>()
        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Success(
            Fake.buildFakeCatsList(10)
        )

        val viewModel = KatViewModel(catDataUseCase, ExceptionMapper(), katConfig)

        val job = launch {
            viewModel.homeUiState.collect()
        }

        assertTrue(viewModel.homeUiState.value.loading)

        viewModel.getCatData()

        runCurrent()

        assertTrue(viewModel.homeUiState.value.catList?.size == 10)
        assertTrue(viewModel.homeUiState.value.error == null)

        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Error(
            SocketTimeoutException("Could not reach the server")
        )

        viewModel.getCatData()

        runCurrent()

        assertTrue(viewModel.homeUiState.value.catList?.size == 10)
        assertTrue(viewModel.homeUiState.value.error == "Please check your network connection")

        every { katConfig.currentPage } returns 1
        every { katConfig.pageLimit } returns 40

        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Success(
            Fake.buildFakeCatsList(40)
        )

        viewModel.getCatData()

        runCurrent()

        assertTrue(viewModel.homeUiState.value.catList?.size == 40)

        job.cancel()
    }
}