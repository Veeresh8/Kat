//package com.droid.kat
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.test.core.app.launchActivity
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.rule.ActivityTestRule
//import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
//import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
//import dagger.hilt.android.testing.BindValue
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import io.mockk.coEvery
//import io.mockk.mockk
//import kotlinx.coroutines.runBlocking
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//
//@HiltAndroidTest
//@RunWith(AndroidJUnit4::class)
//class MainActivityTest {
//
//    @Rule
//    @JvmField
//    val mainActivity = ActivityTestRule(MainActivity::class.java, false, false)
//
//    @BindValue
//    @JvmField
//    val viewModel = mockk<KatViewModel>(relaxed = true)
//
//    @BindValue
//    @JvmField
//    val catDataUseCase = mockk<CatDataUseCase>(relaxed = true)
//
//    @Rule
//    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @Test
//    fun checkProgressBarOnLaunch() {
//        val scenario = launchActivity<MainActivity>()
//        assertDisplayed(R.id.progressBar)
//
//        assertNotDisplayed(R.id.rvCats)
//        assertNotDisplayed(R.id.errorLayout)
//    }
//
//    @Test
//    fun checkErrorLayoutShown() = runBlocking {
//        val scenario = launchActivity<MainActivity>()
//        coEvery { catDataUseCase.fetchCatData(any(), any()) } returns Result.Error(Exception("Boom!"))
//
//        assertDisplayed(R.id.progressBar)
//
//        assertNotDisplayed(R.id.rvCats)
//        assertNotDisplayed(R.id.progressBar)
//
//        assertDisplayed(R.id.errorLayout)
//    }
//}