package ru.troshin.statement.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.troshin.statement.deal.controller.DealApi;
import ru.troshin.statement.dto.LoanOfferDto;
import ru.troshin.statement.dto.LoanStatementRequestDto;
import ru.troshin.statement.mapper.LoanOfferMapper;
import ru.troshin.statement.mapper.LoanReqMapper;
import ru.troshin.statement.service.StatementService;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {

    private final DealApi dealApi;
    private final LoanOfferMapper loanOfferMapper;
    private final LoanReqMapper loanReqMapper;

    @Override
    public void selectOffer(LoanOfferDto offer) {
        log.info("Selecting offer from Statement service for statement {}", offer.getStatementId());
        dealApi.dealOfferSelectPost(loanOfferMapper.toDealDto(offer));
    }

    @Override
    public List<LoanOfferDto> createStatement(LoanStatementRequestDto req) {
        log.info("Creating statement from Statement service for client: {} {}", req.getFirstName(), req.getLastName());
        var list = dealApi.dealStatementPost(loanReqMapper.toDealLoanReq(req)).getBody();
        return list.stream()
                .map(loanOfferMapper::toStatementOffer)
                .toList();
    }
}
