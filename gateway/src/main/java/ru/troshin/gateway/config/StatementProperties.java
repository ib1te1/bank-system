package ru.troshin.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "statement.api")
@Getter
@Setter
public class StatementProperties {
    private String url;
}
