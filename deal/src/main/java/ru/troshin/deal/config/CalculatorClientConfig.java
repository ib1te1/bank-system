package ru.troshin.deal.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.troshin.deal.calculator.controller.CalculatorApi;

@Configuration
@RequiredArgsConstructor
public class CalculatorClientConfig {

    private final CalculatorProperties calculatorProperties;

    @Bean
    public CalculatorApi calculatorApi() {
        RestClient restClient = RestClient.builder()
                .baseUrl(calculatorProperties.getUrl())
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(CalculatorApi.class);
    }
}
