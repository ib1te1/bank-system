package ru.troshin.calculator.service;


import ru.troshin.calculator.dto.CreditDto;
import ru.troshin.calculator.dto.LoanOfferDto;
import ru.troshin.calculator.dto.LoanStatementRequestDto;
import ru.troshin.calculator.dto.ScoringDataDto;

import java.util.List;

public interface CalculatorService {

    List<LoanOfferDto> generateOffers(LoanStatementRequestDto loanStatementRequestDto);

    CreditDto calculateCredit(ScoringDataDto scoringDataDto);


}
