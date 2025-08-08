package ru.troshin.dossier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EmailMessage {

    @Email
    @NotNull
    @JsonProperty("address")
    private String address;

    @JsonProperty("theme")
    @NotNull
    private Theme theme;

    @JsonProperty("statementId")
    @NotNull
    private UUID statementId;

    @JsonProperty("text")
    @NotNull
    private String text;
}
