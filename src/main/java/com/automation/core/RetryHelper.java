package com.automation.core;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
public class RetryHelper {

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_INTERVAL_MS = 1000;

    private RetryHelper() {
    }

    public static <T> T retry(Callable<T> task) throws Exception {
        return retry(task, DEFAULT_MAX_RETRIES);
    }

    public static <T> T retry(Callable<T> task, int maxRetries) throws Exception {
        return retry(task, maxRetries, DEFAULT_RETRY_INTERVAL_MS);
    }

    public static <T> T retry(Callable<T> task, int maxRetries, long retryIntervalMs) throws Exception {
        Exception lastException = null;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                T result = task.call();
                if (attempt > 0) {
                    log.info("Retry attempt {} succeeded", attempt + 1);
                }
                return result;
            } catch (Exception e) {
                lastException = e;
                log.warn("Retry attempt {} failed: {}", attempt + 1, e.getMessage());
                
                if (attempt < maxRetries - 1) {
                    try {
                        Thread.sleep(retryIntervalMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw ie;
                    }
                }
            }
        }
        
        log.error("All {} retry attempts failed", maxRetries);
        throw lastException;
    }

    public static <T> T retryWithCondition(Callable<T> task, ResultValidator<T> validator) throws Exception {
        return retryWithCondition(task, validator, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_INTERVAL_MS);
    }

    public static <T> T retryWithCondition(Callable<T> task, ResultValidator<T> validator, 
                                            int maxRetries, long retryIntervalMs) throws Exception {
        T lastResult = null;
        
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                lastResult = task.call();
                if (validator.isValid(lastResult)) {
                    if (attempt > 0) {
                        log.info("Retry attempt {} succeeded", attempt + 1);
                    }
                    return lastResult;
                } else {
                    log.warn("Retry attempt {} result validation failed", attempt + 1);
                }
            } catch (Exception e) {
                log.warn("Retry attempt {} failed with exception: {}", attempt + 1, e.getMessage());
            }
            
            if (attempt < maxRetries - 1) {
                try {
                    Thread.sleep(retryIntervalMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ie;
                }
            }
        }
        
        log.error("All {} retry attempts failed validation", maxRetries);
        throw new AssertionError("All retry attempts failed validation");
    }

    @FunctionalInterface
    public interface ResultValidator<T> {
        boolean isValid(T result);
    }
}
