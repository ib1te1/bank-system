package ru.troshin.deal.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.troshin.deal.dto.EmploymentPosition;
import ru.troshin.deal.dto.EmploymentStatus;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employment {


    private EmploymentStatus status;

    @JsonProperty("employer_inn")
    private String employerINN;

    private BigDecimal salary;

    private EmploymentPosition position;

    @JsonProperty("work_experience_total")
    private int workExperienceTotal;

    @JsonProperty("work_experience_current")
    private int workExperienceCurrent;
}
