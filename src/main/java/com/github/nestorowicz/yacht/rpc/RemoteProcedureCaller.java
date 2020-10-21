package com.github.nestorowicz.yacht.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RemoteProcedureCaller {

    private final Logger logger = LoggerFactory.getLogger(RemoteProcedureCaller.class);

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RemoteProcedureCaller(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public <T> T call(String address, Object requestBody, Class<T> responseClass) throws RPCException {
        logger.debug("Sending http request to: {}", address);
        try {
            HttpRequest request = createRequest(address, requestBody);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.debug("Got http response from {}: {}", address, response.body());
            return processResponse(response, responseClass);
        } catch (InterruptedException | IOException e) {
            throw new RPCException("Failed to connect to: " + address, e);
        }
    }

    private HttpRequest createRequest(String address, Object requestBody) {
        String bodyString;
        try {
            bodyString = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unexpected error when creating http request. ", e);
        }
        return HttpRequest.newBuilder(URI.create(address))
                .POST(HttpRequest.BodyPublishers.ofString(bodyString))
                .build();
    }

    private <T> T processResponse(HttpResponse<String> response, Class<T> responseClass) {
        // TODO error validation
        try {
            return objectMapper.readValue(response.body(), responseClass);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unexpected error when parsing http response: ", e);
        }
    }
}
