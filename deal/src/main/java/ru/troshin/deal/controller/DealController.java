package ru.troshin.deal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.troshin.deal.dto.FinishRegistrationRequestDto;
import ru.troshin.deal.dto.LoanOfferDto;
import ru.troshin.deal.dto.LoanStatementRequestDto;
import ru.troshin.deal.service.impl.DealServiceImpl;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class DealController implements DealApi {

    private final DealServiceImpl dealServiceImpl;


    @Override
    public ResponseEntity<Void> dealCalculateStatementIdPost(String statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        log.info("POST /deal/calculate/{statementId}, statementId={} body={}", statementId, finishRegistrationRequestDto);
        dealServiceImpl.calculateCredit(finishRegistrationRequestDto, statementId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> dealOfferSelectPost(LoanOfferDto loanOfferDto) {
        log.info("POST /deal/offer/select, body={}", loanOfferDto);
        dealServiceImpl.selectOffer(loanOfferDto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<LoanOfferDto>> dealStatementPost(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("POST /deal/statement, body={}", loanStatementRequestDto);
        return ResponseEntity.ok(dealServiceImpl.createStatement(loanStatementRequestDto));
    }
}
