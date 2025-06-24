package ru.troshin.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.troshin.calculator.dto.*;
import ru.troshin.calculator.service.CalculatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculatorService calculatorService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");

    @Test
    void offersEndpoint_validRequest_returnsOffers() throws Exception {
        LoanStatementRequestDto req = new LoanStatementRequestDto();
        req.setAmount(BigDecimal.valueOf(20000));
        req.setTerm(12);
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setBirthdate(LocalDate.now(ZONE).minusYears(30));
        req.setPassportSeries("1234");
        req.setPassportNumber("123456");
        req.setEmail("test@example.com");

        LoanOfferDto offer = new LoanOfferDto();
        offer.setStatementId(UUID.randomUUID());
        offer.setRequestedAmount(req.getAmount());
        offer.setTotalAmount(req.getAmount());
        offer.setTerm(req.getTerm());
        offer.setMonthlyPayment(BigDecimal.valueOf(1000));
        offer.setRate(BigDecimal.valueOf(12));
        offer.setIsInsuranceEnabled(false);
        offer.setIsSalaryClient(false);

        when(calculatorService.generateOffers(any(LoanStatementRequestDto.class)))
                .thenReturn(List.of(offer));

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].rate").value(12))
                .andExpect(jsonPath("$[0].monthlyPayment").value(1000));
    }

    @Test
    void calcEndpoint_validRequest_returnsCredit() throws Exception {
        ScoringDataDto req = new ScoringDataDto();
        req.setAmount(BigDecimal.valueOf(24000));
        req.setTerm(12);
        req.setBirthdate(LocalDate.now(ZONE).minusYears(30));
        req.setPassportSeries("1234");
        req.setPassportNumber("123456");
        req.setPassportIssueDate(LocalDate.now(ZONE).minusYears(1));
        req.setPassportIssueBranch("branch");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setMiddleName(null);
        req.setGender(Gender.MALE);
        req.setMaritalStatus(MaritalStatus.SINGLE);
        req.setDependentAmount(0);
        EmploymentDto emp = new EmploymentDto();
        emp.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        emp.setEmployerINN("1234567890");
        emp.setSalary(BigDecimal.valueOf(2000));
        emp.setPosition(Position.STAFF);
        emp.setWorkExperienceTotal(24);
        emp.setWorkExperienceCurrent(12);
        req.setEmployment(emp);
        req.setAccountNumber("1234567890123456");
        req.setIsInsuranceEnabled(false);
        req.setIsSalaryClient(false);

        CreditDto credit = new CreditDto();
        credit.setAmount(BigDecimal.valueOf(24000));
        credit.setTerm(12);
        credit.setMonthlyPayment(BigDecimal.valueOf(2100));
        credit.setRate(BigDecimal.valueOf(12));
        credit.setPsk(BigDecimal.valueOf(12.5));
        credit.setIsInsuranceEnabled(false);
        credit.setIsSalaryClient(false);
        credit.setPaymentSchedule(List.of());

        when(calculatorService.calculateCredit(any(ScoringDataDto.class))).thenReturn(credit);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rate").value(12))
                .andExpect(jsonPath("$.monthlyPayment").value(2100))
                .andExpect(jsonPath("$.psk").value(12.5));
    }

    @Test
    void offersEndpoint_invalidRequest_returnsBadRequest() throws Exception {
        LoanStatementRequestDto req = new LoanStatementRequestDto();
        req.setAmount(BigDecimal.valueOf(1000));
        req.setTerm(12);
        req.setFirstName("J");
        req.setLastName("D");
        req.setBirthdate(LocalDate.now(ZONE).minusYears(30));
        req.setPassportSeries("12");
        req.setPassportNumber("abc");
        req.setEmail("not-an-email");
        req.setMiddleName(null);

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
