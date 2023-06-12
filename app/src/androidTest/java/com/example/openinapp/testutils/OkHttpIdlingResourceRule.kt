package com.example.openinapp.testutils

import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.IdlingResource
import com.example.openinapp.api.RetrofitInstance
import com.jakewharton.espresso.OkHttp3IdlingResource
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class OkHttpIdlingResourceRule: TestRule {

    private val resource : IdlingResource = OkHttp3IdlingResource.create("okhttp", RetrofitInstance.getOkHttpClient())

    override fun apply(base: Statement, description: Description): Statement {
        return object: Statement() {
            override fun evaluate() {
                IdlingRegistry.getInstance().register(resource)
                base.evaluate()
                IdlingRegistry.getInstance().unregister(resource)
            }
        }
    }
}