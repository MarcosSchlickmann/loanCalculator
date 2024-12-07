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
    fun `test high volume of requests`() {
        val requestDTO = LoanCalculatorRequestDTO(
            loanAmount = 100.0,
            birthDate = "01/01/1999",
            installments = 10
        )

        val headers = HttpHeaders()
        headers.set("Content-Type", "application/json")

        val url = "http://localhost:$port/api/loan-calculator"
        val request = HttpEntity(requestDTO, headers)

        // warm up
        repeat(100) {
            restTemplate.postForEntity(url, request, String::class.java)
        }

        val numberOfRequests = 1000
        val responseTimes = mutableListOf<Long>()
        val executor = Executors.newFixedThreadPool(10)


        repeat(numberOfRequests) {
            executor.submit {
                val responseTime = measureTimeMillis {
                    restTemplate.postForEntity(url, request, String::class.java)
                }
                synchronized(responseTimes) {
                    responseTimes.add(responseTime)
                }
            }
        }

        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.HOURS)

        println("Average response time: ${responseTimes.average()} ms")
    }

}
