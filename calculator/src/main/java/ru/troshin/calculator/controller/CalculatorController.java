package ru.troshin.calculator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.troshin.calculator.dto.CreditDto;
import ru.troshin.calculator.dto.LoanOfferDto;
import ru.troshin.calculator.dto.LoanStatementRequestDto;
import ru.troshin.calculator.dto.ScoringDataDto;
import ru.troshin.calculator.service.CalculatorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CalculatorController implements CalculatorApi {

    private final CalculatorService calculatorService;

    @Override
    public ResponseEntity<List<LoanOfferDto>> calculatorOffersPost(LoanStatementRequestDto body) {
        List<LoanOfferDto> offers = calculatorService.generateOffers(body);
        return ResponseEntity.ok(offers);
    }

    @Override
    public ResponseEntity<CreditDto> calculatorCalcPost(ScoringDataDto body) {
        log.info("POST /calculator/calc, body={}", body);
        CreditDto credit = calculatorService.calculateCredit(body);
        log.info("Calculated credit: {}", credit);
        return ResponseEntity.ok(credit);

    }
}
