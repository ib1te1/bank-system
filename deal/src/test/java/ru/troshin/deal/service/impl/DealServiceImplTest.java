package ru.troshin.deal.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import ru.troshin.deal.calculator.controller.CalculatorApi;
import ru.troshin.deal.dto.FinishRegistrationRequestDto;
import ru.troshin.deal.dto.LoanOfferDto;
import ru.troshin.deal.dto.LoanStatementRequestDto;
import ru.troshin.deal.entity.Client;
import ru.troshin.deal.entity.Passport;
import ru.troshin.deal.entity.Employment;
import ru.troshin.deal.entity.Statement;
import ru.troshin.deal.exception.NoSuchStatementException;
import ru.troshin.deal.mapper.*;
import ru.troshin.deal.repository.ClientRepository;
import ru.troshin.deal.repository.CreditRepository;
import ru.troshin.deal.repository.StatementRepository;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DealServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private StatementRepository statementRepository;
    @Mock
    private CreditRepository creditRepository;
    @Mock
    private CalculatorApi calculatorApi;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private StatementMapper statementMapper;
    @Mock
    private LoanOfferMapper loanOfferMapper;
    @Mock
    private ScoringMapper scoringMapper;
    @Mock
    private CreditMapper creditMapper;
    @Mock
    private EmploymentMapper employmentMapper;

    @InjectMocks
    private DealServiceImpl service;

    private LoanStatementRequestDto stmtDto;
    private ru.troshin.deal.calculator.dto.LoanStatementRequestDto calcDto;
    private ru.troshin.deal.calculator.dto.LoanOfferDto remoteOffer;
    private LoanOfferDto localOffer;

    @BeforeEach
    void setUp() {
        stmtDto = new LoanStatementRequestDto();
        stmtDto.setAmount(BigDecimal.valueOf(10000));
        stmtDto.setTerm(12);

        calcDto = new ru.troshin.deal.calculator.dto.LoanStatementRequestDto();
        when(statementMapper.toCalcLoanStatement(stmtDto)).thenReturn(calcDto);

        remoteOffer = new ru.troshin.deal.calculator.dto.LoanOfferDto();
        remoteOffer.setRequestedAmount(BigDecimal.valueOf(10000));
        remoteOffer.setTotalAmount(BigDecimal.valueOf(11000));
        remoteOffer.setTerm(12);
        remoteOffer.setMonthlyPayment(BigDecimal.valueOf(950));
        remoteOffer.setRate(BigDecimal.valueOf(5));
        remoteOffer.setIsInsuranceEnabled(true);
        remoteOffer.setIsSalaryClient(true);
        when(calculatorApi.calculatorOffersPost(calcDto))
                .thenReturn(ResponseEntity.ok(List.of(remoteOffer)));

        localOffer = new LoanOfferDto();
        when(loanOfferMapper.toDealLoanOffer(remoteOffer)).thenReturn(localOffer);
    }

    @Test
    void createStatement_returnsMappedOffers() {
        Client client = new Client();
        client.setId(UUID.randomUUID());
        when(clientMapper.toClientFromLoan(stmtDto)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);

        Statement stmt = Statement.builder().id(UUID.randomUUID()).build();
        when(statementRepository.save(any(Statement.class))).thenReturn(stmt);

        List<LoanOfferDto> offers = service.createStatement(stmtDto);

        assertEquals(1, offers.size());
        assertEquals(stmt.getId(), offers.get(0).getStatementId());
        verify(calculatorApi).calculatorOffersPost(calcDto);
    }

    @Test
    void selectOffer_updatesStatementStatus() {
        UUID id = UUID.randomUUID();
        LoanOfferDto offer = new LoanOfferDto();
        offer.setStatementId(id);

        Statement stmt = Statement.builder()
                .id(id)
                .status(null)
                .statusHistory(new ArrayList<>())
                .build();
        when(statementRepository.findById(id)).thenReturn(Optional.of(stmt));

        service.selectOffer(offer);

        assertEquals(offer, stmt.getAppliedOffer());
        assertEquals(ru.troshin.deal.dto.ApplicationStatus.APPROVED, stmt.getStatus());
        assertFalse(stmt.getStatusHistory().isEmpty());
        verify(statementRepository).save(stmt);
    }


    @Test
    void calculateCredit_nonExistingStatement_throwsNoSuchStatement() {
        String uuid = UUID.randomUUID().toString();
        when(statementRepository.findById(UUID.fromString(uuid)))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchStatementException.class,
                () -> service.calculateCredit(new FinishRegistrationRequestDto(), uuid));
    }

    @Test
    void calculateCredit_successfulFlow_savesCreditAndStatement() {
        UUID id = UUID.randomUUID();
        Client client = new Client();
        client.setPassport(new Passport());
        client.setEmployment(new Employment());

        Statement stmt = Statement.builder()
                .id(id)
                .client(client)
                .statusHistory(new ArrayList<>())
                .build();

        LoanOfferDto offer = new LoanOfferDto();
        stmt.setAppliedOffer(offer);

        when(statementRepository.findById(id)).thenReturn(Optional.of(stmt));

        when(scoringMapper.toCalcScoring(any()))
                .thenReturn(new ru.troshin.deal.calculator.dto.ScoringDataDto());
        ru.troshin.deal.calculator.dto.CreditDto remoteCredit = new ru.troshin.deal.calculator.dto.CreditDto();
        when(calculatorApi.calculatorCalcPost(any()))
                .thenReturn(ResponseEntity.ok(remoteCredit));
        when(creditMapper.toDealCredit(remoteCredit))
                .thenReturn(new ru.troshin.deal.dto.CreditDto());
        when(creditMapper.toCredit(any()))
                .thenReturn(new ru.troshin.deal.entity.Credit());

        service.calculateCredit(new FinishRegistrationRequestDto(), id.toString());

        verify(creditRepository).save(any());
        verify(statementRepository).save(stmt);
    }

}
