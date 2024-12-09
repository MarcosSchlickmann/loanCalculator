package com.marcosSchlickmann.loanCalculator.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorRequestDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["spring.main.allow-bean-definition-overriding=true"])
class LoanCalculatorControllerBulkCalculatePerformanceTests {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test compare bulk-calculate with bulk-calculate-simultaneously`() {
        val bulkSize = 10000
        val requestDTOs =
            (1..bulkSize).map {
                LoanCalculatorRequestDTO(
                    loanAmount = 100.0,
                    birthDate = "01/01/1999",
                    installments = 10,
                )
            }

        val headers = HttpHeaders()
        headers.set("Content-Type", "application/json")

        val urlBulkParallelCalculate = "http://localhost:$port/api/loan-calculator/bulk-calculate"
        val requestBulkParallelCalculate = HttpEntity(requestDTOs, headers)

        val urlBulkSequentialCalculate = "http://localhost:$port/api/loan-calculator/bulk-calculate-sequential"
        val requestBulkSequentialCalculate = HttpEntity(requestDTOs, headers)

        val responseBulkSequentialCalculate: ResponseEntity<String>
        val responseTimeBulkSequentialCalculate =
            measureTimeMillis {
                responseBulkSequentialCalculate =
                    restTemplate.postForEntity(
                        urlBulkSequentialCalculate,
                        requestBulkSequentialCalculate,
                        String::class.java,
                    )
            }

        val responseBulkParallelCalculate: ResponseEntity<String>
        val responseTimeBulkParallelCalculate =
            measureTimeMillis {
                responseBulkParallelCalculate =
                    restTemplate.postForEntity(
                        urlBulkParallelCalculate,
                        requestBulkParallelCalculate,
                        String::class.java,
                    )
            }

        println("Bulk size: $bulkSize")
        println("Response time bulk-calculate-sequential: $responseTimeBulkSequentialCalculate ms")
        println("Response time bulk-calculate-parallel: $responseTimeBulkParallelCalculate ms")

        assertEquals(HttpStatus.OK, responseBulkSequentialCalculate.statusCode)
        assertEquals(HttpStatus.OK, responseBulkParallelCalculate.statusCode)

        val responseDTOs = objectMapper.readValue(responseBulkSequentialCalculate.body, List::class.java)
        val responseSimultaneousDTOs = objectMapper.readValue(responseBulkParallelCalculate.body, List::class.java)

        assertEquals(bulkSize, responseDTOs.size)
        assertEquals(bulkSize, responseSimultaneousDTOs.size)
    }
}
