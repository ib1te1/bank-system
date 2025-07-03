package ru.troshin.calculator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.troshin.calculator.dto.*;
import ru.troshin.calculator.exception.ScoringRejectedException;
import ru.troshin.calculator.service.ScoringService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoringServiceImpl implements ScoringService {
    private static final int REQUIRED_TOTAL_EXPERIENCE = 18;
    private static final int REQUIRED_CURRENT_EXPERIENCE = 3;
    private static final int MIN_AGE = 20;
    private static final int MAX_AGE = 65;
    private static final int MONTHS_IN_2_YEARS = 24;
    private static final int FEMALE_RISK_AGE_MIN = 31;
    private static final int FEMALE_RISK_AGE_MAX = 60;
    private static final int MALE_RISK_AGE_MIN = 29;
    private static final int MALE_RISK_AGE_MAX = 55;


    private static void isValidAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        int age = Period.between(birthDate, today).getYears();
        if (age < MIN_AGE || age > MAX_AGE) {
            log.warn("Scoring rejected: age {} out of bounds [20,65]", age);
            throw new ScoringRejectedException("Отказ: возраст не подходит");
        }
    }

    private static void isValidWorkingExp(EmploymentDto employmentDto) {
        if (employmentDto.getWorkExperienceTotal() < REQUIRED_TOTAL_EXPERIENCE) {
            log.warn("Scoring rejected: total experience {} < 18", employmentDto.getWorkExperienceTotal());
            throw new ScoringRejectedException("Отказ: общий стаж < 18 месяцев");
        }
        if (employmentDto.getWorkExperienceCurrent() < REQUIRED_CURRENT_EXPERIENCE) {
            log.warn("Scoring rejected: current experience {} < 3", employmentDto.getWorkExperienceCurrent());
            throw new ScoringRejectedException("Отказ: текущий стаж < 3 месяцев");
        }
    }

    private static int rateByEmpStatus(EmploymentStatus status) {
        return switch (status) {
            case UNEMPLOYED -> {
                log.warn("Scoring rejected: unemployed");
                throw new ScoringRejectedException("Отказ: безработный");
            }
            case SELF_EMPLOYED -> 2;
            case BUSINESS_OWNER -> 1;
            case EMPLOYED -> 0;
        };
    }

    private static int rateByPosition(Position position) {
        return switch (position) {
            case MIDDLE_MANAGER -> 2;
            case TOP_MANAGER -> 3;
            case STAFF -> 0;
        };
    }

    private static void isValidSalary(BigDecimal salary, BigDecimal amount) {
        if (amount.compareTo(salary.multiply(BigDecimal.valueOf(MONTHS_IN_2_YEARS))) > 0) {
            log.warn("Scoring rejected: amount {} > 24*salary {}", amount, salary);
            throw new ScoringRejectedException("Отказ: сумма кредита слишком велика относительно зарплаты");
        }
    }

    private static int rateByMartStatus(MaritalStatus status) {
        return switch (status) {
            case MARRIED -> -3;
            case DIVORCED -> 1;
            case WIDOWED -> -2;
            case SINGLE -> 0;
        };
    }

    private static int rateByGender(Gender gender, LocalDate birthdate) {
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        switch (gender) {
            case FEMALE:
                if (age > FEMALE_RISK_AGE_MIN && age < FEMALE_RISK_AGE_MAX) {
                    return -3;
                }
                break;
            case MALE:
                if (age > MALE_RISK_AGE_MIN && age < MALE_RISK_AGE_MAX) {
                    return -3;
                }
                break;
            case NON_BINARY:
                return 7;
        }
        return 0;
    }

    public BigDecimal calculateRate(ScoringDataDto scoringDto, BigDecimal baseRate) {
        isValidAge(scoringDto.getBirthdate());
        EmploymentDto employmentDto = scoringDto.getEmployment();
        isValidWorkingExp(employmentDto);
        baseRate = baseRate.add(BigDecimal.valueOf(rateByEmpStatus(employmentDto.getEmploymentStatus())));
        baseRate = baseRate.subtract(BigDecimal.valueOf(rateByPosition(employmentDto.getPosition())));
        isValidSalary(employmentDto.getSalary(), scoringDto.getAmount());
        baseRate = baseRate.add(BigDecimal.valueOf(rateByMartStatus(scoringDto.getMaritalStatus())));
        baseRate = baseRate.add(BigDecimal.valueOf(rateByGender(scoringDto.getGender(), scoringDto.getBirthdate())));
        log.debug("Scoring final rate: {}", baseRate);
        return baseRate;

    }

}
