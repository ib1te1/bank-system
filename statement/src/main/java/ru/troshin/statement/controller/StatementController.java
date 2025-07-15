package ru.troshin.statement.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.troshin.statement.dto.LoanOfferDto;
import ru.troshin.statement.dto.LoanStatementRequestDto;
import ru.troshin.statement.service.impl.StatementServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatementController implements StatementApi {

    private final StatementServiceImpl statementService;

    @Override
    public ResponseEntity<Void> statementOfferPost(LoanOfferDto dto) {
        log.info("POST /statement/offer, body={}", dto);
        statementService.selectOffer(dto);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<List<LoanOfferDto>> statementPost(LoanStatementRequestDto dto) {
        log.info("POST /statement, body={}", dto);
        return ResponseEntity.ok(statementService.createStatement(dto));
    }
}
