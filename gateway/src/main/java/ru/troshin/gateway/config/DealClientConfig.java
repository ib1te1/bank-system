package ru.troshin.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.troshin.gateway.deal.controller.AdminApi;
import ru.troshin.gateway.deal.controller.DealApi;

@Configuration
@RequiredArgsConstructor
public class DealClientConfig {

    private final DealProperties dealProperties;

    @Bean
    public DealApi dealApi(){
        RestClient restClient=RestClient
                .builder()
                .baseUrl(dealProperties.getUrl())
                .build();
        HttpServiceProxyFactory factory= HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(DealApi.class);
    }

    @Bean
    public AdminApi adminApi(){
        RestClient restClient=RestClient
                .builder()
                .baseUrl(dealProperties.getUrl())
                .build();
        HttpServiceProxyFactory factory= HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(AdminApi.class);
    }
}
