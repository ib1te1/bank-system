package ru.troshin.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.troshin.gateway.statement.controller.StatementApi;

@Configuration
@RequiredArgsConstructor
public class StatementClientConfig {

    private final StatementProperties statementProperties;

    @Bean
    public StatementApi statementApi() {
        RestClient restClient = RestClient.builder()
                .baseUrl(statementProperties.getUrl())
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(StatementApi.class);
    }
}
