package org.alexv.finnhubclient.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alexv.finnhubclient.model.*;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
public class FinnhubClient {

    private CloseableHttpAsyncClient httpClient;
    private String token;
    private ObjectMapper objectMapper = new ObjectMapper();

    public FinnhubClient(String token) {
        this.token = token;

        configClient();
        startClient();
    }

    public FinnhubClient(CloseableHttpAsyncClient httpClient, String token, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.token = token;
        this.objectMapper = objectMapper;

        startClient();
    }

    private void startClient() {
        this.httpClient.start();
    }

    private void configClient() {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(5))
                .build();

        this.httpClient = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig)
                .build();
    }


    public CompletableFuture<Quote> getQuote(String symbol) {

        CompletableFuture<Quote> futureQuote = new CompletableFuture<>();
        URI uri = URI.create(Endpoint.QUOTE.url() + "?token=" + token + "&symbol=" + symbol);

        SimpleHttpRequest request = SimpleHttpRequest.create(Method.GET, uri);

        httpClient.execute(
                request,
                new FutureCallback<>() {
                    @Override
                    public void completed(SimpleHttpResponse response) {
                        try {
                            Quote quote = objectMapper.readValue(response.getBodyText(), Quote.class);
                            futureQuote.complete(quote);
                        } catch (JsonProcessingException exception) {
                            futureQuote.completeExceptionally(exception);
                        }
                    }

                    @Override
                    public void failed(Exception e) {
                        futureQuote.completeExceptionally(e);
                    }

                    @Override
                    public void cancelled() {
                        futureQuote.cancel(true);
                    }

                }
        );

        return futureQuote;
    }


    public CompletableFuture<Candle> getCandle(String symbol, String resolution, long startEpoch, long endEpoch) {

        CompletableFuture<Candle> futureCandle = new CompletableFuture<>();

        URI uri = URI.create(Endpoint.CANDLE.url() + "?token=" + token
                + "&symbol=" + symbol.toUpperCase() + "&resolution=" + resolution + "&from=" + startEpoch + "&to=" + endEpoch);

        SimpleHttpRequest request = SimpleHttpRequest.create(Method.GET, uri);

        httpClient.execute(
                request,
                new FutureCallback<SimpleHttpResponse>() {
                    @Override
                    public void completed(SimpleHttpResponse response) {
                        try {
                            Candle candle = objectMapper.readValue(response.getBodyText(), Candle.class);
                            futureCandle.complete(candle);
                        } catch (JsonProcessingException e) {
                            futureCandle.completeExceptionally(e);
                        }
                    }

                    @Override
                    public void failed(Exception e) {
                        futureCandle.completeExceptionally(e);
                    }

                    @Override
                    public void cancelled() {
                        futureCandle.cancel(true);
                    }
                }
        );

       return futureCandle;
    }

    public CompletableFuture<CompanyProfile> getCompanyProfile(String symbol) {

        CompletableFuture<CompanyProfile> futureCompanyProfile = new CompletableFuture<>();

        URI uri = URI.create(Endpoint.COMPANY_PROFILE.url() + "?token=" + token + "&symbol=" + symbol);

        SimpleHttpRequest request = SimpleHttpRequest.create(Method.GET, uri);

        httpClient.execute(
                request,
                new FutureCallback<SimpleHttpResponse>() {
                    @Override
                    public void completed(SimpleHttpResponse response) {
                        try {
                            CompanyProfile companyProfile = objectMapper.readValue(response.getBodyText(), CompanyProfile.class);
                            futureCompanyProfile.complete(companyProfile);
                        } catch (JsonProcessingException e) {
                            futureCompanyProfile.completeExceptionally(e);
                        }
                    }

                    @Override
                    public void failed(Exception e) {
                        futureCompanyProfile.completeExceptionally(e);
                    }

                    @Override
                    public void cancelled() {
                        futureCompanyProfile.cancel(true);
                    }
                }
        );

        return futureCompanyProfile;
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
                    .filter(stock -> mics.contains(stock.getMic()) && symbols.contains(stock.getSymbol()))
                    .toList();
        });
    }
}