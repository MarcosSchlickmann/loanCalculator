package com.marcosSchlickmann.loanCalculator.controller

import com.marcosSchlickmann.loanCalculator.service.LoanCalculatorService
import org.mockito.Mockito.spy
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestConfig {

    @Bean
    @Primary
    fun loanCalculatorServiceSpy(loanCalculatorService: LoanCalculatorService): LoanCalculatorService {
        return spy(loanCalculatorService)
    }
}
