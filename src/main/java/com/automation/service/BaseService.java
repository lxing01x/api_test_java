package com.automation.service;

import com.automation.core.ResponseHandler;
import com.automation.core.RestAssuredManager;
import com.automation.core.TestContext;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseService {

    protected final String basePath;
    protected String authToken;

    protected BaseService(String basePath) {
        this.basePath = basePath;
    }

    protected RequestSpecification given() {
        RequestSpecification spec = RestAssuredManager.given();
        if (authToken != null && !authToken.isEmpty()) {
            spec.header("Authorization", "Bearer " + authToken);
        }
        return spec;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
        log.debug("Auth token updated");
    }

    public void clearAuthToken() {
        this.authToken = null;
        log.debug("Auth token cleared");
    }

    protected ResponseHandler get(String path) {
        return new ResponseHandler(given().get(basePath + path));
    }

    protected ResponseHandler get(String path, Object... pathParams) {
        return new ResponseHandler(given().get(basePath + path, pathParams));
    }

    protected ResponseHandler post(String path, Object body) {
        return new ResponseHandler(given().body(body).post(basePath + path));
    }

    protected ResponseHandler put(String path, Object body) {
        return new ResponseHandler(given().body(body).put(basePath + path));
    }

    protected ResponseHandler put(String path, Object body, Object... pathParams) {
        return new ResponseHandler(given().body(body).put(basePath + path, pathParams));
    }

    protected ResponseHandler delete(String path) {
        return new ResponseHandler(given().delete(basePath + path));
    }

    protected ResponseHandler delete(String path, Object... pathParams) {
        return new ResponseHandler(given().delete(basePath + path, pathParams));
    }

    protected ResponseHandler patch(String path, Object body) {
        return new ResponseHandler(given().body(body).patch(basePath + path));
    }

    protected void storeToContext(String key, Object value) {
        TestContext.set(key, value);
    }

    protected <T> T getFromContext(String key) {
        return TestContext.get(key);
    }
}
