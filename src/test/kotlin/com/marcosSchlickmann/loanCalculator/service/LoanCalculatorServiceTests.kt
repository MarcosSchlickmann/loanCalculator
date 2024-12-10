package com.marcosSchlickmann.loanCalculator.service

import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorRequestDTO
import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorResponseDTO
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@SpringBootTest
class LoanCalculatorServiceTests {
    @Autowired
    private lateinit var loanCalculatorService: LoanCalculatorService

    @ParameterizedTest
    @CsvSource(
        "1/1/1999",
        "01/01/1999",
        "01/1/1999",
        "1/01/1999",
    )
    fun `test calculateLoanDetails birthDate formats`(birthDate: String) {
        val loanRequestDTO =
            LoanCalculatorRequestDTO(
                loanAmount = 100.0,
                birthDate = birthDate,
                installments = 10,
            )

        val expectedResult =
            LoanCalculatorResponseDTO(
                totalRepaymentAmount = 102.31,
                totalInterest = 2.31,
                monthlyPaymentAmount = 10.23,
            )

        val result = loanCalculatorService.calculateLoanDetails(loanRequestDTO)

        assertEquals(expectedResult, result)
    }

    @ParameterizedTest
    @CsvSource(
        "18, 100.0, 10, 102.31, 2.31, 10.23",
        "25, 100.0, 10, 102.31, 2.31, 10.23",
        "26, 100.0, 10, 101.38, 1.38, 10.14",
        "40, 100.0, 10, 101.38, 1.38, 10.14",
        "41, 100.0, 10, 100.92, 0.92, 10.09",
        "60, 100.0, 10, 100.92, 0.92, 10.09",
        "61, 100.0, 10, 101.84, 1.84, 10.18",
        "99, 100.0, 10, 101.84, 1.84, 10.18",
    )
    fun `test calculateLoanDetails interest rate based on age`(
        borrowerAge: Int,
        loanAmount: Double,
        installments: Int,
        totalRepaymentAmount: Double,
        totalInterest: Double,
        monthlyPaymentAmount: Double,
    ) {
        val borrowerBirthDate = createBorrowerBirthDate(borrowerAge)

        val loanRequestDTO =
            LoanCalculatorRequestDTO(
                loanAmount = loanAmount,
                birthDate = borrowerBirthDate,
                installments = installments,
            )

        val expectedResult =
            LoanCalculatorResponseDTO(
                totalRepaymentAmount = totalRepaymentAmount,
                totalInterest = totalInterest,
                monthlyPaymentAmount = monthlyPaymentAmount,
            )

        val result = loanCalculatorService.calculateLoanDetails(loanRequestDTO)

        assertEquals(expectedResult, result)
    }

    @ParameterizedTest
    @CsvSource(
        "26, 100.0, 10, 102.31, 2.31, 10.23, 101.38, 1.38, 10.14",
        "41, 100.0, 10, 101.38, 1.38, 10.14, 100.92, 0.92, 10.09",
        "61, 100.0, 10, 100.92, 0.92, 10.09, 101.84, 1.84, 10.18"
    )
    fun `test interest rate change if birthdate is today or tomorrow`(
        borrowerAge: Int,
        loanAmount: Double,
        installments: Int,
        totalRepaymentAmountTomorrow: Double,
        totalInterestTomorrow: Double,
        monthlyPaymentAmountTomorrow: Double,
        totalRepaymentAmountToday: Double,
        totalInterestToday: Double,
        monthlyPaymentAmountToday: Double,
    ) {
        val borrowerBirthDateToday = createBorrowerBirthDate(borrowerAge)
        val borrowerBirthDateTomorrow = LocalDate.parse(
            borrowerBirthDateToday,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        ).plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val loanRequestDTOToday =
            LoanCalculatorRequestDTO(
                loanAmount = loanAmount,
                birthDate = borrowerBirthDateToday,
                installments = installments,
            )

        val loanRequestDTOTomorrow =
            LoanCalculatorRequestDTO(
                loanAmount = loanAmount,
                birthDate = borrowerBirthDateTomorrow,
                installments = installments,
            )

        val expectedResultToday =
            LoanCalculatorResponseDTO(
                totalRepaymentAmount = totalRepaymentAmountToday,
                totalInterest = totalInterestToday,
                monthlyPaymentAmount = monthlyPaymentAmountToday,
            )

        val expectedResultTomorrow =
            LoanCalculatorResponseDTO(
                totalRepaymentAmount = totalRepaymentAmountTomorrow,
                totalInterest = totalInterestTomorrow,
                monthlyPaymentAmount = monthlyPaymentAmountTomorrow,
            )

        val resultToday = loanCalculatorService.calculateLoanDetails(loanRequestDTOToday)
        val resultTomorrow = loanCalculatorService.calculateLoanDetails(loanRequestDTOTomorrow)

        assertEquals(expectedResultToday, resultToday)
        assertEquals(expectedResultTomorrow, resultTomorrow)
    }

    private fun createBorrowerBirthDate(borrowerAge: Int): String {
        val today = LocalDate.now()
        val birthYear = today.year - borrowerAge
        val dayOfMonth = today.dayOfMonth.toString().padStart(2, '0')
        val monthValue = today.monthValue.toString().padStart(2, '0')
        return "${dayOfMonth}/${monthValue}/$birthYear"
    }
}
