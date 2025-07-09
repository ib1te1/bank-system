package ru.troshin.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.troshin.deal.dto.*;
import ru.troshin.deal.service.impl.DealServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class DealControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DealServiceImpl dealServiceImpl;

    @InjectMocks
    private DealController dealController;

    private ObjectMapper objectMapper;
    private LoanStatementRequestDto stmtDto;
    private LoanOfferDto offerDto;
    private FinishRegistrationRequestDto finishDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(dealController).build();

        stmtDto = new LoanStatementRequestDto()
                .amount(BigDecimal.valueOf(5000))
                .term(6)
                .firstName("Test")
                .lastName("User")
                .email("test@user.com")
                .birthdate(LocalDate.of(1990,1,1))
                .passportSeries("AA")
                .passportNumber("111111");

        offerDto = new LoanOfferDto()
                .statementId(UUID.randomUUID())
                .requestedAmount(BigDecimal.valueOf(5000))
                .totalAmount(BigDecimal.valueOf(5500))
                .term(6)
                .monthlyPayment(BigDecimal.valueOf(950))
                .rate(BigDecimal.valueOf(5))
                .isInsuranceEnabled(false)
                .isSalaryClient(true);

        finishDto = new FinishRegistrationRequestDto()
                .gender(Gender.MALE)
                .maritalStatus(MaritalStatus.SINGLE)
                .dependentAmount(0)
                .passportIssueDate(LocalDate.of(2020,1,1))
                .passportIssueBranch("Branch")
                .employment(new EmploymentDto()
                        .employmentStatus(EmploymentStatus.EMPLOYED)
                        .employerINN("1234567890")
                        .salary(BigDecimal.valueOf(1000))
                        .employmentPosition(EmploymentPosition.WORKER)
                        .workExperienceTotal(12)
                        .workExperienceCurrent(6))
                .accountNumber("ACC123");
    }

    @Test
    void postStatement_validInput_returnsOffers() throws Exception {
        when(dealServiceImpl.createStatement(any()))
                .thenReturn(Collections.singletonList(offerDto));

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stmtDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statementId").value(offerDto.getStatementId().toString()));
    }

    @Test
    void postOfferSelect_validInput_returnsNoContent() throws Exception {
        doNothing().when(dealServiceImpl).selectOffer(any());

        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offerDto)))
                .andExpect(status().isNoContent());
    }

    @Test
    void postCalculate_validInput_returnsNoContent() throws Exception {
        doNothing().when(dealServiceImpl).calculateCredit(any(), anyString());

        mockMvc.perform(post("/deal/calculate/" + offerDto.getStatementId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finishDto)))
                .andExpect(status().isNoContent());
    }
}
