package com.automation.core;

import com.automation.config.EnvironmentConfig;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RestAssuredManager {

    private static RequestSpecification baseRequestSpec;
    private static final Map<String, String> DEFAULT_HEADERS = new HashMap<>();

    static {
        initialize();
    }

    private RestAssuredManager() {
    }

    private static void initialize() {
        log.info("Initializing RestAssured configuration...");
        
        DEFAULT_HEADERS.put("Content-Type", ContentType.JSON.toString());
        DEFAULT_HEADERS.put("Accept", ContentType.JSON.toString());
        DEFAULT_HEADERS.put("User-Agent", "Automation-Framework/1.0");
        
        RestAssuredConfig config = RestAssuredConfig.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation())
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
        
        RestAssured.config = config;
        RestAssured.useRelaxedHTTPSValidation();
        
        String baseUrl = EnvironmentConfig.getBaseUrl();
        if (baseUrl != null) {
            RestAssured.baseURI = baseUrl;
        }
        
        int timeout = EnvironmentConfig.getTimeout();
        RestAssured.urlEncodingEnabled = false;
        
        log.info("RestAssured initialized with baseUrl: {}", baseUrl);
    }

    public static RequestSpecification given() {
        RequestSpecification spec = RestAssured.given();
        
        DEFAULT_HEADERS.forEach(spec::header);
        
        spec.contentType(ContentType.JSON)
            .accept(ContentType.JSON);
        
        return spec;
    }

    public static RequestSpecification givenWithAuth(String token) {
        return given()
                .header("Authorization", "Bearer " + token);
    }

    public static RequestSpecification givenWithBasicAuth(String username, String password) {
        return given()
                .auth().preemptive().basic(username, password);
    }

    public static void setBaseUri(String baseUri) {
        RestAssured.baseURI = baseUri;
        log.info("Base URI updated to: {}", baseUri);
    }

    public static void resetBaseUri() {
        String baseUrl = EnvironmentConfig.getBaseUrl();
        if (baseUrl != null) {
            RestAssured.baseURI = baseUrl;
            log.info("Base URI reset to: {}", baseUrl);
        }
    }

    public static void addDefaultHeader(String key, String value) {
        DEFAULT_HEADERS.put(key, value);
        log.info("Added default header: {}={}", key, value);
    }

    public static void removeDefaultHeader(String key) {
        DEFAULT_HEADERS.remove(key);
        log.info("Removed default header: {}", key);
    }

    public static void clearDefaultHeaders() {
        DEFAULT_HEADERS.clear();
        log.info("Cleared all default headers");
    }

    public static void setProxy(String host, int port) {
        RestAssured.proxy(host, port);
        log.info("Proxy set to: {}:{}", host, port);
    }

    public static void resetProxy() {
        RestAssured.reset();
        initialize();
        log.info("Proxy reset and RestAssured reinitialized");
    }
}
