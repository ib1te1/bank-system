package ru.troshin.deal.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "calculator.api")
@Getter
@Setter
@RequiredArgsConstructor
public class CalculatorProperties {
    private String url;
}
