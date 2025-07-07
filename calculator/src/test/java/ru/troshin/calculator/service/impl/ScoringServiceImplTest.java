package ru.troshin.calculator.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.troshin.calculator.dto.*;
import ru.troshin.calculator.exception.ScoringRejectedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class ScoringServiceImplTest {

    private ScoringServiceImpl scoringService;
    private static final ZoneId ZONE = ZoneId.of("Europe/Saratov");

    @BeforeEach
    void setUp() {
        scoringService = new ScoringServiceImpl();
    }

    private ScoringDataDto baseDto() {
        ScoringDataDto dto = new ScoringDataDto();
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(30));
        dto.setAmount(BigDecimal.valueOf(1000));
        dto.setTerm(12);
        dto.setGender(Gender.MALE);
        dto.setPassportSeries("1234");
        dto.setPassportNumber("123456");
        dto.setPassportIssueDate(LocalDate.now(ZONE).minusYears(1));
        dto.setPassportIssueBranch("branch");
        dto.setMaritalStatus(MaritalStatus.SINGLE);
        dto.setDependentAmount(0);
        EmploymentDto emp = new EmploymentDto();
        emp.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        emp.setEmployerINN("1234567890");
        emp.setSalary(BigDecimal.valueOf(1000));
        emp.setEmploymentPosition(EmploymentPosition.WORKER);
        emp.setWorkExperienceTotal(24);
        emp.setWorkExperienceCurrent(12);
        dto.setEmployment(emp);
        dto.setAccountNumber("1234567890123456");
        dto.setIsInsuranceEnabled(false);
        dto.setIsSalaryClient(false);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setMiddleName(null);
        return dto;
    }

    @Test
    void calculateRate_unemployed_throws() {
        ScoringDataDto dto = baseDto();
        dto.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);
        ScoringRejectedException ex = assertThrows(ScoringRejectedException.class,
                () -> scoringService.calculateRate(dto, BigDecimal.valueOf(10)));
        assertTrue(ex.getMessage().contains("безработный"));
    }

    @Test
    void calculateRate_tooYoung_throws() {
        ScoringDataDto dto = baseDto();
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(19));
        ScoringRejectedException ex = assertThrows(ScoringRejectedException.class,
                () -> scoringService.calculateRate(dto, BigDecimal.valueOf(10)));
        assertTrue(ex.getMessage().contains("возраст"));
    }

    @Test
    void calculateRate_tooOld_throws() {
        ScoringDataDto dto = baseDto();
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(66));
        ScoringRejectedException ex = assertThrows(ScoringRejectedException.class,
                () -> scoringService.calculateRate(dto, BigDecimal.valueOf(10)));
        assertTrue(ex.getMessage().contains("возраст"));
    }

    @Test
    void calculateRate_insufficientTotalExp_throws() {
        ScoringDataDto dto = baseDto();
        dto.getEmployment().setWorkExperienceTotal(17);
        ScoringRejectedException ex = assertThrows(ScoringRejectedException.class,
                () -> scoringService.calculateRate(dto, BigDecimal.valueOf(10)));
        assertTrue(ex.getMessage().contains("общий стаж"));
    }

    @Test
    void calculateRate_insufficientCurrentExp_throws() {
        ScoringDataDto dto = baseDto();
        dto.getEmployment().setWorkExperienceCurrent(2);
        ScoringRejectedException ex = assertThrows(ScoringRejectedException.class,
                () -> scoringService.calculateRate(dto, BigDecimal.valueOf(10)));
        assertTrue(ex.getMessage().contains("текущий стаж"));
    }

    @Test
    void calculateRate_salaryTooLowForAmount_throws() {
        ScoringDataDto dto = baseDto();
        dto.getEmployment().setSalary(BigDecimal.valueOf(1000));
        dto.setAmount(BigDecimal.valueOf(1000 * 25L));
        ScoringRejectedException ex = assertThrows(ScoringRejectedException.class,
                () -> scoringService.calculateRate(dto, BigDecimal.valueOf(10)));
        assertTrue(ex.getMessage().contains("слишком велика"));
    }

    @Test
    void calculateRate_maritalStatus_adjustment() {
        ScoringDataDto dto = baseDto();
        BigDecimal baseRate = BigDecimal.valueOf(10);

        dto.setGender(Gender.MALE);
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(29));

        dto.setMaritalStatus(MaritalStatus.MARRIED);
        BigDecimal rate1 = scoringService.calculateRate(dto, baseRate);
        BigDecimal expected1 = baseRate.add(BigDecimal.valueOf(-3));
        assertEquals(0, rate1.compareTo(expected1));

        dto.setMaritalStatus(MaritalStatus.DIVORCED);
        BigDecimal rate2 = scoringService.calculateRate(dto, baseRate);
        BigDecimal expected2 = baseRate.add(BigDecimal.valueOf(1));
        assertEquals(0, rate2.compareTo(expected2));
    }


    @Test
    void calculateRate_genderAdjustment() {
        ScoringDataDto dto = baseDto();
        BigDecimal baseRate = BigDecimal.valueOf(10);
        dto.setGender(Gender.FEMALE);
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(35));
        BigDecimal rateF = scoringService.calculateRate(dto, baseRate);
        assertEquals(0, rateF.compareTo(baseRate.add(BigDecimal.valueOf(-3))));
        dto.setGender(Gender.MALE);
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(40));
        BigDecimal rateM = scoringService.calculateRate(dto, baseRate);
        assertEquals(0, rateM.compareTo(baseRate.add(BigDecimal.valueOf(-3))));
        dto.setGender(Gender.NON_BINARY);
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(30));
        BigDecimal rateNB = scoringService.calculateRate(dto, baseRate);
        assertEquals(0, rateNB.compareTo(baseRate.add(BigDecimal.valueOf(7))));
    }

    @Test
    void calculateRate_positionAdjustment() {
        ScoringDataDto dto = baseDto();
        BigDecimal baseRate = BigDecimal.valueOf(10);

        dto.setGender(Gender.MALE);
        dto.setBirthdate(LocalDate.now(ZONE).minusYears(29));
        dto.setMaritalStatus(MaritalStatus.SINGLE);

        dto.getEmployment().setEmploymentPosition(EmploymentPosition.MID_MANAGER);
        BigDecimal rate1 = scoringService.calculateRate(dto, baseRate);
        BigDecimal expected1 = baseRate.add(BigDecimal.valueOf(-2));
        assertEquals(0, rate1.compareTo(expected1));

        dto.getEmployment().setEmploymentPosition(EmploymentPosition.TOP_MANAGER);
        BigDecimal rate2 = scoringService.calculateRate(dto, baseRate);
        BigDecimal expected2 = baseRate.add(BigDecimal.valueOf(-3));
        assertEquals(0, rate2.compareTo(expected2));
    }

}
