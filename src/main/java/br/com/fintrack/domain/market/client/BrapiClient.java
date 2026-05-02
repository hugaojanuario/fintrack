package br.com.fintrack.domain.market.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BrapiClient {

    private final RestClient restClient;

    @Value("${brapi.token:}")
    private String token;

    public BrapiClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://brapi.dev").build();
    }

    public BrapiResponseDTO fetchQuotes(String tickers) {
        return restClient.get()
                .uri(uri -> uri
                        .path("/api/quote/{tickers}")
                        .queryParam("token", token)
                        .build(tickers))
                .retrieve()
                .body(BrapiResponseDTO.class);
    }
}
