package com.marcosSchlickmann.loanCalculator.controller
import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorRequestDTO
import com.marcosSchlickmann.loanCalculator.dto.LoanCalculatorResponseDTO
import com.marcosSchlickmann.loanCalculator.service.LoanCalculatorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/loan-calculator")
class LoanCalculatorController {
    @Autowired
    private lateinit var loanCalculatorService: LoanCalculatorService

    @PostMapping("calculate")
    fun calculate(
        @RequestBody loanRequestDTO: LoanCalculatorRequestDTO,
    ): LoanCalculatorResponseDTO {
        return loanCalculatorService.calculateLoanDetails(loanRequestDTO)
    }

    @PostMapping("/bulk-calculate")
    fun bulkCalculate(
        @RequestBody loanRequestDTOs: List<LoanCalculatorRequestDTO>,
    ): List<LoanCalculatorResponseDTO> {
        return loanCalculatorService.calculateLoanDetailsBulk(loanRequestDTOs)
    }

    @PostMapping("/bulk-calculate-sequential")
    fun bulkSimultaneousCalculate(
        @RequestBody loanRequestDTOs: List<LoanCalculatorRequestDTO>,
    ): List<LoanCalculatorResponseDTO> {
        return loanRequestDTOs.map { loanCalculatorService.calculateLoanDetails(it) }
    }
}
