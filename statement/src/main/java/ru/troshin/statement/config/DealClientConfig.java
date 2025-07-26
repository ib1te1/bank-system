package ru.troshin.statement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.troshin.statement.deal.controller.DealApi;

@Configuration
@RequiredArgsConstructor
public class DealClientConfig {

    private final DealProperties dealProperties;

    @Bean
    public DealApi dealClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(dealProperties.getUrl())
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(DealApi.class);

    }
}
