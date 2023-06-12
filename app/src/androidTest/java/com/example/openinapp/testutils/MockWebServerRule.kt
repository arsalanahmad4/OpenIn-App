package com.example.openinapp.testutils

import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class MockWebServerRule : TestRule {
    open val server = MockWebServer()
    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                server.start(8080)
                base?.evaluate()
                server.shutdown()
            }
        }

    }
}