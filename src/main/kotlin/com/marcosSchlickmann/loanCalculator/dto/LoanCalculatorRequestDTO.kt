package com.marcosSchlickmann.loanCalculator.dto

data class LoanCalculatorRequestDTO(
    val loanAmount: Double,
    val birthDate: String,
    val installments: Int,
)
