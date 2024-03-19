package org.alexv.finnhubclient.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alexv.finnhubclient.model.*;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;

import java.net.URI;
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

        CompletableFuture<List<EnrichedSymbol>> futureEnrichedSymbolList = new CompletableFuture<>();

        URI uri = URI.create(Endpoint.SYMBOL.url() + "?token=" + token + "&exchange=" + Exchange.valueOf(exchange).code());

        SimpleHttpRequest request = SimpleHttpRequest.create(Method.GET, uri);

        httpClient.execute(
                request,
                new FutureCallback<SimpleHttpResponse>() {
                    @Override
                    public void completed(SimpleHttpResponse response) {
                        try {
                            List<EnrichedSymbol> enrichedSymbolList = objectMapper.readValue(response.getBodyText(), new TypeReference<List<EnrichedSymbol>>() {
                            });
                            futureEnrichedSymbolList.complete(enrichedSymbolList);
                        } catch (JsonProcessingException e) {
                            futureEnrichedSymbolList.completeExceptionally(e);
                        }
                    }

                    @Override
                    public void failed(Exception e) {
                        futureEnrichedSymbolList.completeExceptionally(e);
                    }

                    @Override
                    public void cancelled() {
                        futureEnrichedSymbolList.cancel(true);
                    }
                }
        );

        return futureEnrichedSymbolList;
    }

    public CompletableFuture<SymbolLookup> searchSymbol(String query) {

        CompletableFuture<SymbolLookup> futureSymbols = new CompletableFuture<>();

        URI uri = URI.create(Endpoint.SYMBOL_LOOKUP.url() + "?token=" + token + "&q=" + query);

        SimpleHttpRequest request = SimpleHttpRequest.create(Method.GET, uri);

        httpClient.execute(
                request,
                new FutureCallback<SimpleHttpResponse>() {
                    @Override
                    public void completed(SimpleHttpResponse response) {
                        try {
                            SymbolLookup symbols = objectMapper.readValue(response.getBodyText(), SymbolLookup.class);
                            futureSymbols.complete(symbols);
                        } catch (JsonProcessingException e) {
                            futureSymbols.completeExceptionally(e);
                        }
                    }

                    @Override
                    public void failed(Exception e) {
                        futureSymbols.completeExceptionally(e);
                    }

                    @Override
                    public void cancelled() {
                        futureSymbols.cancel(true);
                    }
                }
        );

        return futureSymbols;
    }

    public CompletableFuture<List<EnrichedSymbol>> searchAllStock(String exchange, String symbol) {

        CompletableFuture<List<EnrichedSymbol>> futureEnrichedSymbolList = new CompletableFuture<>();

        URI uri = URI.create(Endpoint.SYMBOL.url() + "?token=" + token + "&exchange=" + exchange);

        SimpleHttpRequest request = SimpleHttpRequest.create(Method.GET, uri);

        httpClient.execute(
                request,
                new FutureCallback<SimpleHttpResponse>() {
                    @Override
                    public void completed(SimpleHttpResponse response) {
                        try {
                            List<EnrichedSymbol> enrichedSymbolList = objectMapper.readValue(response.getBodyText(), new TypeReference<List<EnrichedSymbol>>() {
                            });

                            for (EnrichedSymbol enrichedSymbol: enrichedSymbolList) {
                                if (enrichedSymbol.getSymbol().equals(symbol)) {
                                    futureEnrichedSymbolList.complete(List.of(enrichedSymbol));
                                    return;
                                }
                            }
                        } catch (JsonProcessingException e) {
                            futureEnrichedSymbolList.completeExceptionally(e);
                        }
                    }

                    @Override
                    public void failed(Exception e) {
                        futureEnrichedSymbolList.completeExceptionally(e);
                    }

                    @Override
                    public void cancelled() {
                        futureEnrichedSymbolList.cancel(true);
                    }
                }
        );

        return futureEnrichedSymbolList;
    }

    public CompletableFuture<List<EnrichedSymbol>> searchStock(String exchange, String mic) {

        CompletableFuture<List<EnrichedSymbol>> futureEnrichedSymbolList = new CompletableFuture<>();

        URI uri = URI.create(Endpoint.SYMBOL.url() + "?token=" + token + "&exchange=" + exchange + "&mic=" + mic);

        SimpleHttpRequest request = SimpleHttpRequest.create(Method.GET, uri);

        httpClient.execute(
                request,
                new FutureCallback<SimpleHttpResponse>() {
                    @Override
                    public void completed(SimpleHttpResponse response) {
                        try {
                            List<EnrichedSymbol> enrichedSymbolList = objectMapper.readValue(response.getBodyText(), new TypeReference<List<EnrichedSymbol>>() {});
                            futureEnrichedSymbolList.complete(enrichedSymbolList);
                        } catch (JsonProcessingException e) {
                            futureEnrichedSymbolList.completeExceptionally(e);
                        }
                    }

                    @Override
                    public void failed(Exception e) {
                        futureEnrichedSymbolList.completeExceptionally(e);
                    }

                    @Override
                    public void cancelled() {
                        futureEnrichedSymbolList.cancel(true);
                    }
                }
        );

        return futureEnrichedSymbolList;
    }
}