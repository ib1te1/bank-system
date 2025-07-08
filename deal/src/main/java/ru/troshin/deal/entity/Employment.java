package ru.troshin.deal.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
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

    @Min(0)
    private BigDecimal salary;

    private EmploymentPosition position;

    @JsonProperty("work_experience_total")
    @Min(0)
    private int workExperienceTotal;

    @JsonProperty("work_experience_current")
    @Min(0)
    private int workExperienceCurrent;
}
