package com.automation.core;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ResponseHandler {

    private final Response response;
    private final Map<String, Object> context = new ConcurrentHashMap<>();

    public ResponseHandler(Response response) {
        this.response = response;
        log.debug("Response status code: {}", response.getStatusCode());
    }

    public Response getResponse() {
        return response;
    }

    public int getStatusCode() {
        return response.getStatusCode();
    }

    public String getStatusLine() {
        return response.getStatusLine();
    }

    public String getBody() {
        return response.getBody().asString();
    }

    public <T> T getBodyAs(Class<T> clazz) {
        return response.getBody().as(clazz);
    }

    public String getHeader(String name) {
        return response.getHeader(name);
    }

    public Map<String, String> getHeaders() {
        return response.getHeaders().asList().stream()
                .collect(java.util.stream.Collectors.toMap(
                        io.restassured.http.Header::getName,
                        io.restassured.http.Header::getValue,
                        (existing, replacement) -> existing
                ));
    }

    public String getCookie(String name) {
        return response.getCookie(name);
    }

    public Map<String, String> getCookies() {
        return response.getCookies();
    }

    public long getResponseTime() {
        return response.getTime();
    }

    public long getResponseTimeIn(TimeUnit unit) {
        return response.getTimeIn(unit);
    }

    public ResponseHandler validateStatusCode(int expectedStatusCode) {
        int actual = response.getStatusCode();
        if (actual != expectedStatusCode) {
            throw new AssertionError(
                    String.format("Expected status code: %d, Actual: %d", expectedStatusCode, actual)
            );
        }
        log.debug("Status code validation passed: {}", expectedStatusCode);
        return this;
    }

    public ResponseHandler validateStatusCodeIsSuccess() {
        int statusCode = response.getStatusCode();
        if (statusCode < 200 || statusCode >= 300) {
            throw new AssertionError(
                    String.format("Expected successful status code (2xx), Actual: %d", statusCode)
            );
        }
        log.debug("Success status code validation passed: {}", statusCode);
        return this;
    }

    public ResponseHandler validateJsonPath(String path, Object expectedValue) {
        Object actualValue = response.jsonPath().get(path);
        if (!java.util.Objects.equals(expectedValue, actualValue)) {
            throw new AssertionError(
                    String.format("JSON path [%s] validation failed. Expected: %s, Actual: %s",
                            path, expectedValue, actualValue)
            );
        }
        log.debug("JSON path [{}] validation passed: {}", path, expectedValue);
        return this;
    }

    public ResponseHandler validateJsonPathContains(String path, Object expectedValue) {
        Object actualValue = response.jsonPath().get(path);
        if (actualValue == null || !actualValue.toString().contains(expectedValue.toString())) {
            throw new AssertionError(
                    String.format("JSON path [%s] does not contain expected value. Expected to contain: %s, Actual: %s",
                            path, expectedValue, actualValue)
            );
        }
        log.debug("JSON path [{}] contains validation passed", path);
        return this;
    }

    public ResponseHandler validateHeader(String name, String expectedValue) {
        String actualValue = response.getHeader(name);
        if (!java.util.Objects.equals(expectedValue, actualValue)) {
            throw new AssertionError(
                    String.format("Header [%s] validation failed. Expected: %s, Actual: %s",
                            name, expectedValue, actualValue)
            );
        }
        log.debug("Header [{}] validation passed: {}", name, expectedValue);
        return this;
    }

    public ResponseHandler validateResponseTime(long maxMillis) {
        long actualTime = response.getTime();
        if (actualTime > maxMillis) {
            throw new AssertionError(
                    String.format("Response time validation failed. Max allowed: %dms, Actual: %dms",
                            maxMillis, actualTime)
            );
        }
        log.debug("Response time validation passed: {}ms", actualTime);
        return this;
    }

    public ResponseHandler prettyPrint() {
        response.prettyPrint();
        return this;
    }

    public ResponseHandler storeToContext(String key) {
        context.put(key, response);
        log.debug("Stored response to context with key: {}", key);
        return this;
    }

    public Object getFromContext(String key) {
        return context.get(key);
    }
}
