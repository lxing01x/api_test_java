package com.automation.core;

import com.automation.config.EnvironmentConfig;
import lombok.extern.slf4j.Slf4j;
import spock.lang.Shared;
import spock.lang.Specification;

@Slf4j
public class BaseSpec extends Specification {

    @Shared
    protected static String testEnv;

    def setupSpec() {
        log.info("========== Starting Test Suite ==========");
        testEnv = EnvironmentConfig.getCurrentEnv();
        log.info("Running tests on environment: {}", testEnv);
        TestContext.clear();
    }

    def cleanupSpec() {
        log.info("========== Test Suite Completed ==========");
        TestContext.clear();
    }

    def setup() {
        log.info("---------- Starting Test: {} ----------", specificationContext.getCurrentIteration().getName());
    }

    def cleanup() {
        log.info("---------- Test Completed: {} ----------", specificationContext.getCurrentIteration().getName());
    }

    protected ResponseHandler get(String path) {
        return get(path, null);
    }

    protected ResponseHandler get(String path, Map<String, Object> queryParams) {
        var request = RestAssuredManager.given();
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        return new ResponseHandler(request.get(path));
    }

    protected ResponseHandler post(String path, Object body) {
        return post(path, body, null);
    }

    protected ResponseHandler post(String path, Object body, Map<String, Object> queryParams) {
        var request = RestAssuredManager.given()
                .body(body);
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        return new ResponseHandler(request.post(path));
    }

    protected ResponseHandler put(String path, Object body) {
        return put(path, body, null);
    }

    protected ResponseHandler put(String path, Object body, Map<String, Object> queryParams) {
        var request = RestAssuredManager.given()
                .body(body);
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        return new ResponseHandler(request.put(path));
    }

    protected ResponseHandler delete(String path) {
        return delete(path, null);
    }

    protected ResponseHandler delete(String path, Map<String, Object> queryParams) {
        var request = RestAssuredManager.given();
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        return new ResponseHandler(request.delete(path));
    }

    protected ResponseHandler patch(String path, Object body) {
        var request = RestAssuredManager.given()
                .body(body);
        return new ResponseHandler(request.patch(path));
    }
}
