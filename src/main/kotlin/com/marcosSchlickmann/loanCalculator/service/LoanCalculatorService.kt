package com.marcosSchlickmann.loanCalculator.service

import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorRequestDTO
import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorResponseDTO
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.pow

@Service
class LoanCalculatorService {

    companion object {
        private val BIRTH_DATE_FORMATS = arrayOf("dd/MM/yyyy", "d/MM/yyyy", "dd/M/yyyy", "d/M/yyyy")
    }

    fun calculateLoanDetails(requestDTO: LoanCalculatorRequestDTO): LoanCalculatorResponseDTO {
        val annualInterestRate = calculateAnnualInterestRate(requestDTO.birthDate)
        val monthlyInterestRate = calculateMonthlyInterestRate(annualInterestRate)

        val monthlyPaymentAmount = (requestDTO.loanAmount * monthlyInterestRate) / (1 - (1 + monthlyInterestRate).pow(-requestDTO.installments.toDouble()))
        val totalRepaymentAmount = monthlyPaymentAmount * requestDTO.installments
        val totalInterest = totalRepaymentAmount - requestDTO.loanAmount

        return LoanCalculatorResponseDTO(
            totalRepaymentAmount = roundToTwoDecimals(totalRepaymentAmount),
            totalInterest = roundToTwoDecimals(totalInterest),
            monthlyPaymentAmount = roundToTwoDecimals(monthlyPaymentAmount)
        )
    }

    private fun roundToTwoDecimals(amount: Double): Double {
        return amount.toBigDecimal().setScale(2, java.math.RoundingMode.HALF_UP).toDouble()
    }

    private fun calculateMonthlyInterestRate(annualInterestRate: Double): Double {
        return annualInterestRate / 12
    }

    private fun calculateAnnualInterestRate(birthDateString: String): Double {
        val birthDate = parseDate(birthDateString)
        val currentDate = LocalDate.now()
        val borrowerAge = Period.between(birthDate, currentDate).years

        return when {
            borrowerAge <= 25 -> 0.05
            borrowerAge in 26..40 -> 0.03
            borrowerAge in 41..60 -> 0.02
            else -> 0.04
        }
    }

    private fun parseDate(dateString: String): LocalDate {
        for (format in BIRTH_DATE_FORMATS) {
            try {
                return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format))
            } catch (e: Exception) {
                continue
            }
        }
        throw IllegalArgumentException("Invalid date format")
    }
}
