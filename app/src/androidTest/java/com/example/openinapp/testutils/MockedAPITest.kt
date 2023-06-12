package com.example.openinapp.testutils


import com.example.openinapp.util.Constants
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class MockedAPITest {
    @get:Rule
    var mockWebServerRule = MockWebServerRule()

    @get:Rule
    var okHttpIdlingResourceRule = OkHttpIdlingResourceRule()

    @Before
    open fun setBaseUrl() {
        Constants.BASE_URL = mockWebServerRule.server.url("/").toString()
    }

    @After
    open fun updateBaseUrl() {
        Constants.BASE_URL = "https://api.inopenapp.com/"
    }

}