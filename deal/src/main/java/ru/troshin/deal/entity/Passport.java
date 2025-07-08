package ru.troshin.deal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passport {

    @Pattern(regexp = "^[0-9]{4}$")
    private String series;

    @Pattern(regexp = "^[0-9]{6}$")
    private String number;

    private String issueBranch;

    private LocalDate issueDate;
}
