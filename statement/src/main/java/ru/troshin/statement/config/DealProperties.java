package ru.troshin.statement.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "deal.api")
public class DealProperties {
    private String url;
}
