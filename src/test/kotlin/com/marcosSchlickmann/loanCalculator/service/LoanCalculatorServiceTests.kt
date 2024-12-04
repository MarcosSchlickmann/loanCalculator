package com.marcosSchlickmann.loanCalculator.service

import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorRequestDTO
import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorResponseDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals

@SpringBootTest
class LoanCalculatorServiceTests {

    @Autowired
    private lateinit var loanCalculatorService: LoanCalculatorService

    @Test
    fun `test calculateLoanDetails`() {
        val loanRequestDTO = LoanCalculatorRequestDTO(
            loanAmount = 100.0,
            birthDate = "13/01/1999",
            installments = 10
        )

        val expectedResult = LoanCalculatorResponseDTO(
            totalRepaymentAmount = 102.31,
            totalInterest = 2.31,
            monthlyPaymentAmount = 10.23
        )

        val result = loanCalculatorService.calculateLoanDetails(loanRequestDTO)

        assertEquals(expectedResult, result, "The result should be $expectedResult")
    }
}
