package ru.troshin.calculator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.troshin.calculator.config.CalculatorProperties;
import ru.troshin.calculator.dto.*;
import ru.troshin.calculator.service.CalculatorService;
import ru.troshin.calculator.service.PrescoringService;
import ru.troshin.calculator.service.ScoringService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {

    private final PrescoringService prescoringService;
    private final ScoringService scoringService;
    private final CalculatorProperties calculatorProperties;

    private BigDecimal calculateMonthlyPayment(BigDecimal totalAmount,BigDecimal rate,Integer term){
        double r=toMonthlyRate(rate);
        double numerator=Math.pow(1+r,term)*r;
        double denominator=Math.pow(1+r,term)-1;
        return totalAmount.multiply(BigDecimal.valueOf(numerator/denominator)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateInsCost(BigDecimal requestedAmount,BigDecimal rate,Integer term){
        double r=toMonthlyRate(rate);
        return requestedAmount.multiply(BigDecimal.valueOf(r*24.0/term*0.02)).setScale(2, RoundingMode.HALF_UP);
    }

    private double toMonthlyRate(BigDecimal rate){
        return (rate.doubleValue()/12)/100;
    }

    @Override
    public List<LoanOfferDto> generateOffers(LoanStatementRequestDto loanStatementRequestDto) {
        prescoringService.validateBirthdate(loanStatementRequestDto.getBirthdate());
        BigDecimal requestedAmount = loanStatementRequestDto.getAmount();
        Integer term=loanStatementRequestDto.getTerm();
        double baseRate=calculatorProperties.getBaseRate();
        double insuranceReduction =calculatorProperties.getInsuranceRateDelta();
        double salaryReduction =calculatorProperties.getSalaryClientDelta();
        List<LoanOfferDto> offers=new ArrayList<>();
        boolean[] bools={false,true};
        for(boolean insurance:bools){
            for (boolean salary:bools){
                BigDecimal rate=BigDecimal.valueOf(baseRate+(insurance?insuranceReduction:0)+(salary?salaryReduction:0))
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal insCost=BigDecimal.ZERO;
                if(insurance){
                    insCost=calculateInsCost(requestedAmount,rate,term);
                }
                BigDecimal totalAmount=requestedAmount.add(insCost);
                BigDecimal monthlyPayment=calculateMonthlyPayment(totalAmount,rate,term);
                offers.add(new LoanOfferDto(
                        UUID.randomUUID(),
                        requestedAmount,
                        totalAmount,
                        term,
                        monthlyPayment,
                        rate,
                        insurance,
                        salary
                ));
            }
        }
        offers.sort(Comparator.comparing(LoanOfferDto::getRate).reversed());
        return offers;
    }

    private BigDecimal isSalaryAndInsuranceClient(Boolean isInsurance,Boolean isSalary,BigDecimal rate){
        if (Boolean.TRUE.equals(isInsurance)) {
            rate = rate.add(BigDecimal.valueOf(calculatorProperties.getInsuranceRateDelta()));
        }
        if (Boolean.TRUE.equals(isSalary)) {
            rate = rate.add(BigDecimal.valueOf(calculatorProperties.getSalaryClientDelta()));
        }
        return rate;
    }

    private List<PaymentScheduleElementDto> createPaymentSchedule(BigDecimal totalAmount,BigDecimal rate,Integer term,BigDecimal monthlyPayment) {
        List<PaymentScheduleElementDto> schedule = new ArrayList<>();
        BigDecimal remainingDebt = totalAmount;
        double monthlyRate = toMonthlyRate(rate);
        LocalDate firstDate = LocalDate.now().plusMonths(1);
        for (int i = 1; i <= term; i++) {
            BigDecimal interestPayment = remainingDebt.multiply(BigDecimal.valueOf(monthlyRate))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(2, RoundingMode.HALF_UP);
            BigDecimal newRemaining = remainingDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);
            if (i == term) {
                debtPayment = debtPayment.add(newRemaining);
                monthlyPayment = interestPayment.add(debtPayment).setScale(2, RoundingMode.HALF_UP);
                newRemaining = BigDecimal.ZERO;
            }
            LocalDate payDate = firstDate.plusMonths(i - 1);
            PaymentScheduleElementDto elem =new PaymentScheduleElementDto(i,payDate,monthlyPayment,
                    interestPayment,debtPayment,newRemaining);
            schedule.add(elem);
            remainingDebt = newRemaining;
        }
        return schedule;
    }

    private BigDecimal calculatePSK(List<PaymentScheduleElementDto> schedule,Integer term,BigDecimal amount){
        BigDecimal sumPayments = schedule.stream()
                .map(PaymentScheduleElementDto::getTotalPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal years = BigDecimal.valueOf(term)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal ratio = sumPayments.divide(amount, 10, RoundingMode.HALF_UP);
        BigDecimal psk = ratio.subtract(BigDecimal.ONE)
                .divide(years, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        log.debug("sumPayments={}, principal={}, years={}, PСК={}", sumPayments, amount, years, psk);
        return psk;
    }

    @Override
    public CreditDto calculateCredit(ScoringDataDto scoringDataDto) {
        log.info("calculateCredit for {}", scoringDataDto);
        prescoringService.validatePassportIssueDate(scoringDataDto.getPassportIssueDate());
        prescoringService.validateBirthdate(scoringDataDto.getBirthdate());
        BigDecimal baseRate=BigDecimal.valueOf(calculatorProperties.getBaseRate());
        BigDecimal rate = scoringService.calculateRate(scoringDataDto,baseRate);
        rate=isSalaryAndInsuranceClient(scoringDataDto.getIsInsuranceEnabled(),scoringDataDto.getIsSalaryClient(),rate).setScale(2, RoundingMode.HALF_UP);
        log.debug("Rate after adjustments: {}", rate);
        BigDecimal amount = scoringDataDto.getAmount();
        Integer term = scoringDataDto.getTerm();
        BigDecimal insCost = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(scoringDataDto.getIsInsuranceEnabled())) {
            insCost = calculateInsCost(amount, rate, term);
        }
        BigDecimal totalAmount = amount.add(insCost);
        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, rate, term);
        log.debug("totalAmount={}, monthlyPayment={}", totalAmount, monthlyPayment);

        List<PaymentScheduleElementDto> schedule = createPaymentSchedule(totalAmount,rate,term,monthlyPayment);
        BigDecimal psk=calculatePSK(schedule,term,amount);

        return new CreditDto(amount,term,monthlyPayment,rate,psk,
                scoringDataDto.getIsInsuranceEnabled(),scoringDataDto.getIsSalaryClient(),schedule);
    }
}
