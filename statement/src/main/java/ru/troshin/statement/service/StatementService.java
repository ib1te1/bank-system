package ru.troshin.statement.service;

import ru.troshin.statement.dto.LoanOfferDto;
import ru.troshin.statement.dto.LoanStatementRequestDto;

import java.util.List;

public interface StatementService {
    void selectOffer(LoanOfferDto offer);

    List<LoanOfferDto> createStatement(LoanStatementRequestDto req);
}
