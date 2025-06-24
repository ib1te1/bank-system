package ru.troshin.calculator.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import ru.troshin.calculator.config.CalculatorProperties;
import ru.troshin.calculator.dto.*;
import ru.troshin.calculator.service.PrescoringService;
import ru.troshin.calculator.service.ScoringService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculatorServiceImplTest {

    @Mock
    private PrescoringService prescoringService;

    @Mock
    private ScoringService scoringService;

    private CalculatorProperties properties;
    private CalculatorServiceImpl calculatorService;

    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        properties = new CalculatorProperties();
        properties.setBaseRate(12.0);
        properties.setInsuranceRateDelta(-3.0);
        properties.setSalaryClientDelta(-1.0);
        calculatorService = new CalculatorServiceImpl(prescoringService, scoringService, properties);
    }

    private LoanStatementRequestDto makeLoanStmtDto() {
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        dto.setAmount(BigDecimal.valueOf(20000));
        dto.setTerm(12);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(30));
        dto.setPassportSeries("1234");
        dto.setPassportNumber("123456");
        dto.setEmail("test@example.com");
        dto.setMiddleName(null);
        return dto;
    }

    @Test
    void generateOffers_returnsFourOffers_sortedByRateDesc() {
        LoanStatementRequestDto dto = makeLoanStmtDto();
        doNothing().when(prescoringService).validateBirthdate(dto.getBirthdate());

        List<LoanOfferDto> offers = calculatorService.generateOffers(dto);
        assertEquals(4, offers.size());

        BigDecimal prevRate = offers.get(0).getRate();
        for (LoanOfferDto offer : offers) {
            assertNotNull(offer.getStatementId());
            assertEquals(dto.getAmount(), offer.getRequestedAmount());
            assertEquals(dto.getTerm(), offer.getTerm());
            assertTrue(prevRate.compareTo(offer.getRate()) >= 0);
            prevRate = offer.getRate();
        }

        List<BigDecimal> rates = offers.stream().map(LoanOfferDto::getRate).toList();
        assertTrue(rates.contains(BigDecimal.valueOf(12.00).setScale(2)));
        assertTrue(rates.contains(BigDecimal.valueOf(11.00).setScale(2)));
        assertTrue(rates.contains(BigDecimal.valueOf(9.00).setScale(2)));
        assertTrue(rates.contains(BigDecimal.valueOf(8.00).setScale(2)));
    }

    @Test
    void generateOffers_prescoringThrows_propagates() {
        LoanStatementRequestDto dto = makeLoanStmtDto();
        doThrow(new IllegalArgumentException("invalid")).when(prescoringService).validateBirthdate(dto.getBirthdate());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculatorService.generateOffers(dto));
        assertEquals("invalid", ex.getMessage());
    }

    @Test
    void calculateCredit_successfulScenario() {
        ScoringDataDto dto = new ScoringDataDto();
        dto.setAmount(BigDecimal.valueOf(24000));
        dto.setTerm(12);
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(30));
        dto.setPassportIssueDate(LocalDate.now(ZONE).minusYears(1));
        dto.setPassportSeries("1234");
        dto.setPassportNumber("123456");
        dto.setPassportIssueBranch("branch");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setMiddleName(null);
        dto.setGender(Gender.MALE);
        dto.setMaritalStatus(MaritalStatus.SINGLE);
        dto.setDependentAmount(0);
        EmploymentDto emp = new EmploymentDto();
        emp.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        emp.setEmployerINN("1234567890");
        emp.setSalary(BigDecimal.valueOf(2000));
        emp.setPosition(Position.STAFF);
        emp.setWorkExperienceTotal(24);
        emp.setWorkExperienceCurrent(12);
        dto.setEmployment(emp);
        dto.setAccountNumber("1234567890123456");
        dto.setIsInsuranceEnabled(false);
        dto.setIsSalaryClient(false);

        doNothing().when(prescoringService).validateBirthdate(dto.getBirthdate());
        doNothing().when(prescoringService).validatePassportIssueDate(dto.getPassportIssueDate());
        when(scoringService.calculateRate(dto, BigDecimal.valueOf(properties.getBaseRate())))
                .thenReturn(BigDecimal.valueOf(properties.getBaseRate()));

        CreditDto credit = calculatorService.calculateCredit(dto);
        List<PaymentScheduleElementDto> schedule = credit.getPaymentSchedule();
        assertEquals(12, schedule.size());
        assertEquals(BigDecimal.ZERO, schedule.get(11).getRemainingDebt());
        assertTrue(credit.getMonthlyPayment().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(0, credit.getRate().compareTo(BigDecimal.valueOf(properties.getBaseRate()).setScale(2)));
        assertTrue(credit.getPsk().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculateCredit_prescoringThrows_propagates() {
        ScoringDataDto dto = new ScoringDataDto();
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(17));
        dto.setPassportIssueDate(LocalDate.now(ZONE).minusYears(1));
        dto.setAmount(BigDecimal.valueOf(20000));
        dto.setTerm(12);
        doThrow(new IllegalArgumentException("age invalid")).when(prescoringService).validateBirthdate(dto.getBirthdate());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculatorService.calculateCredit(dto));
        assertEquals("age invalid", ex.getMessage());
    }

    @Test
    void calculateCredit_scoringThrows_propagates() {
        ScoringDataDto dto = new ScoringDataDto();
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(30));
        dto.setPassportIssueDate(LocalDate.now(ZONE).minusYears(1));
        dto.setAmount(BigDecimal.valueOf(20000));
        dto.setTerm(12);
        EmploymentDto emp = new EmploymentDto();
        emp.setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        emp.setEmployerINN("1234567890");
        emp.setSalary(BigDecimal.valueOf(1000));
        emp.setPosition(Position.STAFF);
        emp.setWorkExperienceTotal(24);
        emp.setWorkExperienceCurrent(12);
        dto.setEmployment(emp);
        dto.setGender(Gender.MALE);
        dto.setMaritalStatus(MaritalStatus.SINGLE);
        dto.setDependentAmount(0);
        dto.setPassportSeries("1234");
        dto.setPassportNumber("123456");
        dto.setPassportIssueBranch("branch");
        dto.setAccountNumber("1234567890123456");
        dto.setIsInsuranceEnabled(false);
        dto.setIsSalaryClient(false);
        doNothing().when(prescoringService).validateBirthdate(dto.getBirthdate());
        doNothing().when(prescoringService).validatePassportIssueDate(dto.getPassportIssueDate());
        when(scoringService.calculateRate(dto, BigDecimal.valueOf(properties.getBaseRate())))
                .thenThrow(new IllegalArgumentException("scoring failed"));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculatorService.calculateCredit(dto));
        assertEquals("scoring failed", ex.getMessage());
    }
}
