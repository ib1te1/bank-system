package ru.troshin.statement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.troshin.statement.dto.LoanOfferDto;
import ru.troshin.statement.dto.LoanStatementRequestDto;
import ru.troshin.statement.service.impl.StatementServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatementController.class)
class StatementControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private StatementServiceImpl statementService;

    @TestConfiguration
    static class MockServiceConfig {
        @Bean
        StatementServiceImpl statementService() {
            return Mockito.mock(StatementServiceImpl.class);
        }
    }

    @Test
    void statementOfferPost_ShouldReturnNoContent() throws Exception {
        LoanOfferDto dto = new LoanOfferDto();
        dto.setStatementId(UUID.randomUUID());
        dto.setRequestedAmount(new BigDecimal("25000"));
        dto.setTotalAmount(new BigDecimal("26500"));
        dto.setTerm(24);
        dto.setMonthlyPayment(new BigDecimal("1104.17"));
        dto.setRate(new BigDecimal("6.00"));
        dto.setIsInsuranceEnabled(true);
        dto.setIsSalaryClient(false);

        doNothing().when(statementService).selectOffer(any(LoanOfferDto.class));

        mvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void statementPost_ShouldReturnOffersList() throws Exception {
        LoanStatementRequestDto req = new LoanStatementRequestDto();
        req.setPassportSeries("1234");
        req.setPassportNumber("567890");
        req.setFirstName("Ivan");
        req.setLastName("Ivanov");
        req.setMiddleName("Petrovich");
        req.setBirthdate(LocalDate.now().minusYears(30));
        req.setEmail("ivan.ivanov@example.com");
        req.setAmount(new BigDecimal("20000"));
        req.setTerm(24);

        LoanOfferDto o1 = new LoanOfferDto();
        o1.setStatementId(UUID.randomUUID());
        o1.setRequestedAmount(req.getAmount());
        o1.setTotalAmount(new BigDecimal("21200"));
        o1.setTerm(req.getTerm());
        o1.setMonthlyPayment(new BigDecimal("883.33"));
        o1.setRate(new BigDecimal("5.50"));
        o1.setIsInsuranceEnabled(false);
        o1.setIsSalaryClient(true);

        LoanOfferDto o2 = new LoanOfferDto();
        o2.setStatementId(UUID.randomUUID());
        o2.setRequestedAmount(req.getAmount());
        o2.setTotalAmount(new BigDecimal("21000"));
        o2.setTerm(req.getTerm());
        o2.setMonthlyPayment(new BigDecimal("875.00"));
        o2.setRate(new BigDecimal("5.00"));
        o2.setIsInsuranceEnabled(true);
        o2.setIsSalaryClient(false);

        when(statementService.createStatement(any(LoanStatementRequestDto.class)))
                .thenReturn(List.of(o1, o2));

        mvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].rate").value(5.5))
                .andExpect(jsonPath("$[1].isInsuranceEnabled").value(true));
    }
}
