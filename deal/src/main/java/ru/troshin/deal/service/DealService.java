package ru.troshin.deal.service;


import ru.troshin.deal.dto.FinishRegistrationRequestDto;
import ru.troshin.deal.dto.LoanOfferDto;
import ru.troshin.deal.dto.LoanStatementRequestDto;

import java.util.List;

public interface DealService {
    List<LoanOfferDto> createStatement(LoanStatementRequestDto req);

    void selectOffer(LoanOfferDto offer);

    void calculateCredit(FinishRegistrationRequestDto finishDto, String statementId);
}
