package ru.troshin.calculator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("calculator")
public class CalculatorProperties {
    private double baseRate;
    private double insuranceRateDelta;
    private double salaryClientDelta;
}
