package com.droid.kat

import androidx.test.rule.ActivityTestRule
import com.adevinta.android.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.adevinta.android.barista.assertion.BaristaListAssertions.assertListItemCount
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import java.net.SocketTimeoutException

@HiltAndroidTest
class MainActivityTest {

    @Rule
    @JvmField
    val mainActivity = ActivityTestRule(MainActivity::class.java, false, false)

    @BindValue
    @JvmField
    val catDataUseCase = mockk<CatDataUseCase>(relaxed = true)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkProgressBarOnLaunch() {
        mainActivity.launchActivity(null)
        assertDisplayed(R.id.progressBar)

        assertNotDisplayed(R.id.rvCats)
        assertNotDisplayed(R.id.errorLayout)
    }

    @Test
    fun checkErrorLayoutShown() {
        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Error(Exception("Boom!"))

        mainActivity.launchActivity(null)

        assertNotDisplayed(R.id.rvCats)
        assertNotDisplayed(R.id.progressBar)

        assertDisplayed(R.id.errorLayout)
        assertDisplayed(R.id.tvErrorMessage, "Could not reach our servers, try again!")
    }

    @Test
    fun checkIfNetworkConnectionErrorIsShown() {
        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Error(SocketTimeoutException("Boom!"))

        mainActivity.launchActivity(null)

        assertNotDisplayed(R.id.rvCats)
        assertNotDisplayed(R.id.progressBar)

        assertDisplayed(R.id.errorLayout)
        assertDisplayed(R.id.tvErrorMessage, "Please check your network connection")
    }

    @Test
    fun checkRetryFlow() {
        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Error(SocketTimeoutException("Boom!"))

        mainActivity.launchActivity(null)

        assertNotDisplayed(R.id.rvCats)
        assertNotDisplayed(R.id.progressBar)

        assertDisplayed(R.id.errorLayout)
        assertDisplayed(R.id.tvErrorMessage, "Please check your network connection")

        val catList = arrayListOf<CatData>()
        repeat(50) {
            val cat = mockk<CatData>()
            every { cat.id } returns it.toString()
            every { cat.url } returns "https://cdn2.thecatapi.com/images/Rl39SPjDO.png"
            every { cat.breeds } returns listOf(CatData.BreedData(name = "Breed $it"))
            catList.add(cat)
        }

        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Success(catList)

        clickOn(R.id.btnRetry)

        assertNotDisplayed(R.id.errorLayout)
        assertNotDisplayed(R.id.tvErrorMessage, "Please check your network connection")

        assertDisplayed(R.id.rvCats)
        assertListItemCount(R.id.rvCats, 50)
        assertDisplayedAtPosition(R.id.rvCats, 0, R.id.tvBreedName, "Breed 0")
    }

    @Test
    fun checkIfRecyclerViewIsPopulated() {
        val catList = arrayListOf<CatData>()
        repeat(50) {
            val cat = mockk<CatData>()
            every { cat.id } returns it.toString()
            every { cat.url } returns "https://cdn2.thecatapi.com/images/Rl39SPjDO.png"
            every { cat.breeds } returns listOf(CatData.BreedData(name = "Breed $it"))
            catList.add(cat)
        }

        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Success(catList)

        mainActivity.launchActivity(null)

        assertNotDisplayed(R.id.errorLayout)
        assertNotDisplayed(R.id.progressBar)

        assertDisplayed(R.id.rvCats)
        assertListItemCount(R.id.rvCats, 50)
        assertDisplayedAtPosition(R.id.rvCats, 0, R.id.tvBreedName, "Breed 0")
    }
}