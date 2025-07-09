package ru.troshin.deal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.troshin.deal.calculator.controller.CalculatorApi;
import ru.troshin.deal.dto.*;
import ru.troshin.deal.entity.*;
import ru.troshin.deal.exception.NoSuchStatementException;
import ru.troshin.deal.mapper.*;
import ru.troshin.deal.repository.ClientRepository;
import ru.troshin.deal.repository.CreditRepository;
import ru.troshin.deal.repository.StatementRepository;
import ru.troshin.deal.service.DealService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DealServiceImpl implements DealService {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CreditRepository creditRepository;
    private final CalculatorApi calculatorApi;

    private final ClientMapper clientMapper;
    private final CreditMapper creditMapper;
    private final ScoringMapper scoringMapper;
    private final StatementMapper statementMapper;
    private final LoanOfferMapper loanOfferMapper;
    private final EmploymentMapper employmentMapper;

    @Override
    public List<LoanOfferDto> createStatement(LoanStatementRequestDto req) {
        log.info("Starting createStatement for client: {} {}", req.getFirstName(), req.getLastName());
        Client client = saveNewClient(req);
        log.debug("Saved new client with ID: {}", client.getId());

        Statement statement = createAndSaveStatement(client);
        log.debug("Created statement with ID: {}", statement.getId());

        List<LoanOfferDto> offers = fetchLoanOffers(req, statement.getId());
        log.info("Fetched {} loan offers for statement {}", offers.size(), statement.getId());

        return offers;
    }

    @Override
    public void selectOffer(LoanOfferDto offer) {
        log.info("Selecting offer {}", offer.getStatementId());
        Statement statement = findStatementOrThrow(offer.getStatementId());
        updateStatementWithOffer(offer, statement);
        log.info("Offer selected, statement {} updated to status {}", statement.getId(), statement.getStatus());
    }

    @Override
    public void calculateCredit(FinishRegistrationRequestDto finishDto, String statementId) {
        log.info("Starting credit calculation for statement {}", statementId);
        Statement statement = findStatementOrThrow(UUID.fromString(statementId));

        updateClientFinishData(statement.getClient(), finishDto);
        log.debug("Client data updated for client ID: {}", statement.getClient().getId());

        ScoringDataDto scoring = buildScoringData(statement);
        log.debug("Scoring data built: {}", scoring);

        Credit credit = callCalculatorAndSaveCredit(scoring);
        log.info("Credit calculated with ID: {}", credit.getId());

        finalizeStatementWithCredit(statement, credit);
        log.info("Statement {} finalized with credit status {}", statement.getId(), statement.getStatus());
    }

    private Client saveNewClient(LoanStatementRequestDto req) {
        Client client = clientMapper.toClientFromLoan(req);
        return clientRepository.save(client);
    }

    private Statement createAndSaveStatement(Client client) {
        Statement statement = Statement.builder()
                .client(client)
                .status(ApplicationStatus.PREAPPROVAL)
                .creationDate(LocalDateTime.now())
                .statusHistory(new ArrayList<>())
                .build();
        return statementRepository.save(statement);
    }

    private List<LoanOfferDto> fetchLoanOffers(LoanStatementRequestDto req, UUID statementId) {
        var calcLoanStatement = statementMapper.toCalcLoanStatement(req);
        var calcOffers = calculatorApi.calculatorOffersPost(calcLoanStatement).getBody();
        List<LoanOfferDto> offers = calcOffers.stream().map(loanOfferMapper::toDealLoanOffer).toList();
        offers.forEach(loan -> loan.setStatementId(statementId));
        return offers;
    }

    private Statement findStatementOrThrow(UUID id) {
        return statementRepository.findById(id)
                .orElseThrow(() -> new NoSuchStatementException("Statement not found: " + id));
    }

    private void updateStatementWithOffer(LoanOfferDto offer, Statement statement) {
        ApplicationStatus prev = statement.getStatus();
        statement.setAppliedOffer(offer);
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.getStatusHistory().add(new StatementStatusHistoryDto(prev, LocalDateTime.now(), ChangeType.AUTOMATIC));

        statementRepository.save(statement);
    }

    private void updateClientFinishData(Client client, FinishRegistrationRequestDto dto) {
        Passport passport = client.getPassport();
        passport.setIssueBranch(dto.getPassportIssueBranch());
        passport.setIssueDate(dto.getPassportIssueDate());
        client.setPassport(passport);
        client.setEmployment(employmentMapper.toEmployment(dto.getEmployment()));
        client.setAccountNumber(dto.getAccountNumber());
        clientRepository.save(client);
    }

    private ScoringDataDto buildScoringData(Statement stmt) {
        Client c = stmt.getClient();
        var offer = stmt.getAppliedOffer();
        var scoring=scoringMapper.toDealScoring(c);
        scoringMapper.updateScoring(offer,scoring);
        return scoring;
    }

    private Credit callCalculatorAndSaveCredit(ScoringDataDto scoringData) {
        var calcScoring = scoringMapper.toCalcScoring(scoringData);
        var calcCredit = calculatorApi.calculatorCalcPost(calcScoring).getBody();
        CreditDto creditDto = creditMapper.toDealCredit(calcCredit);

        Credit credit = creditMapper.toCredit(creditDto);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        creditRepository.save(credit);

        return credit;
    }

    private void finalizeStatementWithCredit(Statement stmt, Credit credit) {
        ApplicationStatus prev = stmt.getStatus();
        stmt.setCredit(credit);
        stmt.setStatus(ApplicationStatus.CC_APPROVED);
        stmt.getStatusHistory()
                .add(new StatementStatusHistoryDto(prev, LocalDateTime.now(), ChangeType.AUTOMATIC));
        statementRepository.save(stmt);
    }
}
