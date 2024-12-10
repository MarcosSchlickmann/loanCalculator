package com.marcosSchlickmann.loanCalculator.controller

import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorRequestDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["spring.main.allow-bean-definition-overriding=true"])
class LoanCalculatorControllerPerformanceTests {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `test calculate high volume of requests`() {
        val requestDTO =
            LoanCalculatorRequestDTO(
                loanAmount = 100.0,
                birthDate = "01/01/1999",
                installments = 10,
            )

        val headers = HttpHeaders()
        headers.set("Content-Type", "application/json")

        val url = "http://localhost:$port/api/loan-calculator/calculate"
        val request = HttpEntity(requestDTO, headers)

        // warm up the server to avoid cold starts
        repeat(1000) {
            restTemplate.postForEntity(url, request, String::class.java)
        }
        Thread.sleep(5000) // 5 seconds

        // Split the tests into threads to more accurately simulate concurrent requests
        val threadCounts = listOf(50, 100, 250, 500)
        val requestCounts = listOf(1000, 5000, 10000)

        for (threadCount in threadCounts) {
            for (requestCount in requestCounts) {
                testCalculatePerformance(url, request, threadCount, requestCount)
                Thread.sleep(2000)
            }
        }
    }

    private fun testCalculatePerformance(
        url: String,
        request: HttpEntity<LoanCalculatorRequestDTO>,
        threadCount: Int,
        requestCount: Int,
    ) {
        val executor = Executors.newFixedThreadPool(threadCount)
        val responseTimes = mutableListOf<Long>()

        repeat(requestCount) {
            executor.submit {
                val responseTime =
                    measureTimeMillis {
                        restTemplate.postForEntity(url, request, String::class.java)
                    }
                synchronized(responseTimes) {
                    responseTimes.add(responseTime)
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.HOURS)

        val averageResponseTime = responseTimes.average()

        println("threadCount: $threadCount, requestCount: $requestCount")
        println("Average response time: $averageResponseTime ms")
        println("")
    }
}
