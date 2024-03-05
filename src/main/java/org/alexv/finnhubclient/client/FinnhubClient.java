package org.alexv.finnhubclient.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alexv.finnhubclient.model.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
public class FinnhubClient {

    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private String token;
    private ObjectMapper objectMapper = new ObjectMapper();

    public FinnhubClient(String token) {
        this.token = token;
    }

    public FinnhubClient(CloseableHttpClient httpClient, String token, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.token = token;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<Quote> getQuote(String symbol) {
        HttpGet get = new HttpGet(Endpoint.QUOTE.url() + "?token=" + token + "&symbol=" + symbol);

        return CompletableFuture.supplyAsync(() -> {
            String result;
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }


            try {
                return objectMapper.readValue(result, Quote.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }


    public CompletableFuture<Candle> getCandle(String symbol, String resolution, long startEpoch, long endEpoch) {
        HttpGet get = new HttpGet(Endpoint.CANDLE.url() + "?token=" + token
                + "&symbol=" + symbol.toUpperCase() + "&resolution=" + resolution + "&from=" + startEpoch + "&to=" + endEpoch);

        return CompletableFuture.supplyAsync(() -> {
            String result;
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            try {
                return objectMapper.readValue(result, Candle.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<CompanyProfile> getCompanyProfile(String symbol) {
        HttpGet get = new HttpGet(Endpoint.COMPANY_PROFILE.url() + "?token=" + token + "&symbol=" + symbol);

        return CompletableFuture.supplyAsync(() -> {
            String result;
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            try {
                return objectMapper.readValue(result, CompanyProfile.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<List<EnrichedSymbol>> getSymbols(String exchange) {
        HttpGet get = new HttpGet(Endpoint.SYMBOL.url() + "?token=" + token + "&exchange=" + Exchange.valueOf(exchange).code());

        return CompletableFuture.supplyAsync(() -> {
            String result;
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            try {
                return objectMapper.readValue(result, new TypeReference<List<EnrichedSymbol>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public CompletableFuture<SymbolLookup> searchSymbol(String query) {
        HttpGet get = new HttpGet(Endpoint.SYMBOL_LOOKUP.url() + "?token=" + token + "&q=" + query);

        return CompletableFuture.supplyAsync(() -> {
            String result;
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                result = EntityUtils.toString(response.getEntity());
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            try {
                return objectMapper.readValue(result, SymbolLookup.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<List<EnrichedSymbol>> searchAllStock(String exchange, String symbol) {
        HttpGet get = new HttpGet(Endpoint.SYMBOL.url() + "?token=" + token + "&exchange=" + exchange);

        return CompletableFuture.supplyAsync(() -> {
            String result;
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                result = EntityUtils.toString(response.getEntity());
            } catch (ParseException | IOException e) {
                throw new RuntimeException(e);
            }

            List<EnrichedSymbol> stocks;
            try {
                stocks = objectMapper.readValue(result, new TypeReference<List<EnrichedSymbol>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            var stock = stocks.stream()
                    .filter(s -> s.getSymbol().compareTo(symbol) == 0)
                    .findFirst()
                    .orElse(EnrichedSymbol.builder().figi("").build());

            return stock.getFigi().isBlank()
                    ? Collections.emptyList()
                    : List.of(stock);
        });
    }

    public CompletableFuture<List<EnrichedSymbol>> searchAllStock(String exchange, List<String> mics, List<String> symbols) {
        HttpGet get = new HttpGet(Endpoint.SYMBOL.url() + "?token=" + token + "&exchange=" + exchange);

        return CompletableFuture.supplyAsync(() -> {
            String result;
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                result = EntityUtils.toString(response.getEntity());
            } catch (ParseException | IOException e) {
                throw new RuntimeException(e);
            }

            List<EnrichedSymbol> stocks;
            try {
                stocks = objectMapper.readValue(result, new TypeReference<List<EnrichedSymbol>>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            return stocks.stream()
                    .filter(stock -> mics.contains(stock.getMic()) &&  symbols.contains(stock.getSymbol()))
                    .toList();
        });
    }
}