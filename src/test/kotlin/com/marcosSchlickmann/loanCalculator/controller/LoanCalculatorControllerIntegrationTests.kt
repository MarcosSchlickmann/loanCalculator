package com.marcosSchlickmann.loanCalculator.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorRequestDTO
import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorResponseDTO
import com.marcosSchlickmann.loanCalculator.service.LoanCalculatorService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["spring.main.allow-bean-definition-overriding=true"])
@ContextConfiguration(classes = [TestConfig::class])
class LoanCalculatorControllerIntegrationTests {
    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var loanCalculatorServiceSpy: LoanCalculatorService

    @Test
    fun `test calculate integration`() {
        val requestDTO =
            LoanCalculatorRequestDTO(
                loanAmount = 100.0,
                birthDate = "01/01/1999",
                installments = 10,
            )

        val headers = HttpHeaders()
        headers.set("Content-Type", "application/json")

        val url = "http://localhost:$port/api/loan-calculator"
        val request = HttpEntity(requestDTO, headers)
        val response: ResponseEntity<String> =
            restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String::class.java,
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        val responseDTO = objectMapper.readValue(response.body, LoanCalculatorResponseDTO::class.java)

        assertEquals(102.31, responseDTO.totalRepaymentAmount)
        assertEquals(2.31, responseDTO.totalInterest)
        assertEquals(10.23, responseDTO.monthlyPaymentAmount)

        verify(loanCalculatorServiceSpy).calculateLoanDetails(requestDTO)
    }

    @Test
    fun `test bulk-calculate integration`() {
        val requestDTOs = listOf(
            LoanCalculatorRequestDTO(
                loanAmount = 100.0,
                birthDate = "01/01/1999",
                installments = 10,
            ),
            LoanCalculatorRequestDTO(
                loanAmount = 200.0,
                birthDate = "01/01/1999",
                installments = 20,
            ),
        )

        val headers = HttpHeaders()
        headers.set("Content-Type", "application/json")

        val url = "http://localhost:$port/api/loan-calculator/bulk-calculate"
        val request = HttpEntity(requestDTOs, headers)
        val response: ResponseEntity<String> =
            restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String::class.java,
            )

        assertEquals(HttpStatus.OK, response.statusCode)

        val responseDTOs = objectMapper.readValue(response.body, Array<LoanCalculatorResponseDTO>::class.java).toList()

        assertEquals(2, responseDTOs.size)

        assertEquals(102.31, responseDTOs[0].totalRepaymentAmount)
        assertEquals(2.31, responseDTOs[0].totalInterest)
        assertEquals(10.23, responseDTOs[0].monthlyPaymentAmount)

        assertEquals(208.87, responseDTOs[1].totalRepaymentAmount)
        assertEquals(8.87, responseDTOs[1].totalInterest)
        assertEquals(10.44, responseDTOs[1].monthlyPaymentAmount)

        verify(loanCalculatorServiceSpy, times(2)).calculateLoanDetails(requestDTOs[0])
    }
}
