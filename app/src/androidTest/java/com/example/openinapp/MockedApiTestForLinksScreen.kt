package com.example.openinapp

import android.os.SystemClock
import android.support.test.espresso.matcher.ViewMatchers.withId
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.openinapp.testutils.AssetReaderUtil
import com.example.openinapp.testutils.MockedAPITest
import com.example.openinapp.ui.dashboard.DashboardActivity
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MockedApiTestForLinksScreen: MockedAPITest() {
    @Before
    fun setup() {

    }

    @After
    fun after() {

    }

    @Test
    fun launchLinksTab(){
        SystemClock.sleep(1000)
        mockWebServerRule.server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody(
                    AssetReaderUtil.asset(
                        "dashboard_api_response_success"
                    )
                )
        )
        SystemClock.sleep(1000)
        val scenario = launchActivity<DashboardActivity>()
        SystemClock.sleep(1000)

        Espresso.onView(withId(R.id.tvGreetings))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario.close()
        SystemClock.sleep(1000)
    }
}