package com.marcosSchlickmann.loanCalculator.dto

data class LoanCalculatorResponseDTO(
    val totalRepaymentAmount: Double,
    val totalInterest: Double,
    val monthlyPaymentAmount: Double,
)
