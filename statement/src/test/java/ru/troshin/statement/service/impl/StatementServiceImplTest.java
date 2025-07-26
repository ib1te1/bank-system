package ru.troshin.statement.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import ru.troshin.statement.dto.LoanOfferDto;
import ru.troshin.statement.dto.LoanStatementRequestDto;
import ru.troshin.statement.mapper.LoanOfferMapper;
import ru.troshin.statement.mapper.LoanReqMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.same;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StatementServiceImplTest {

    @Mock
    private ru.troshin.statement.deal.controller.DealApi dealApi;

    @Mock
    private LoanOfferMapper loanOfferMapper;

    @Mock
    private LoanReqMapper loanReqMapper;

    @InjectMocks
    private StatementServiceImpl service;

    private LoanOfferDto inputOffer;
    private ru.troshin.statement.deal.dto.LoanOfferDto mappedDealOffer;

    private LoanStatementRequestDto inputReq;
    private ru.troshin.statement.deal.dto.LoanStatementRequestDto mappedDealReq;
    private ru.troshin.statement.deal.dto.LoanOfferDto dealOffer1, dealOffer2;
    private LoanOfferDto outOffer1, outOffer2;

    @BeforeEach
    void setUp() {
        inputOffer = new LoanOfferDto();
        mappedDealOffer = new ru.troshin.statement.deal.dto.LoanOfferDto();
        when(loanOfferMapper.toDealDto(inputOffer)).thenReturn(mappedDealOffer);

        inputReq = new LoanStatementRequestDto();
        mappedDealReq = new ru.troshin.statement.deal.dto.LoanStatementRequestDto();
        when(loanReqMapper.toDealLoanReq(inputReq)).thenReturn(mappedDealReq);

        dealOffer1 = new ru.troshin.statement.deal.dto.LoanOfferDto();
        dealOffer1.setRate(BigDecimal.valueOf(1));
        dealOffer2 = new ru.troshin.statement.deal.dto.LoanOfferDto();
        dealOffer2.setRate(BigDecimal.valueOf(2));
        when(dealApi.dealStatementPost(mappedDealReq))
                .thenReturn(ResponseEntity.ok(List.of(dealOffer1, dealOffer2)));

        outOffer1 = new LoanOfferDto();
        outOffer1.setRate(BigDecimal.valueOf(9.5));
        outOffer1.setTotalAmount(BigDecimal.valueOf(200000));

        outOffer2 = new LoanOfferDto();
        outOffer2.setRate(BigDecimal.valueOf(7.3));
        outOffer2.setTotalAmount(BigDecimal.valueOf(250000));
        when(loanOfferMapper.toStatementOffer(same(dealOffer1))).thenReturn(outOffer1);
        when(loanOfferMapper.toStatementOffer(same(dealOffer2))).thenReturn(outOffer2);
    }

    @Test
    void selectOffer_ShouldCallDealApiWithMappedDto() {
        service.selectOffer(inputOffer);

        verify(loanOfferMapper).toDealDto(inputOffer);
        verify(dealApi).dealOfferSelectPost(mappedDealOffer);
    }

    @Test
    void createStatement_ShouldReturnMappedOffers() {
        List<LoanOfferDto> result = service.createStatement(inputReq);

        verify(loanReqMapper).toDealLoanReq(inputReq);
        verify(dealApi).dealStatementPost(mappedDealReq);

        assertThat(result)
                .hasSize(2)
                .containsExactly(outOffer1, outOffer2);
    }
}
