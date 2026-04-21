package com.automation.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TestContext {

    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(ConcurrentHashMap::new);

    private TestContext() {
    }

    public static void set(String key, Object value) {
        context.get().put(key, value);
        log.debug("Stored in context: {} = {}", key, value);
    }

    public static <T> T get(String key) {
        T value = (T) context.get().get(key);
        log.debug("Retrieved from context: {} = {}", key, value);
        return value;
    }

    public static <T> T getOrDefault(String key, T defaultValue) {
        T value = (T) context.get().getOrDefault(key, defaultValue);
        return value;
    }

    public static boolean containsKey(String key) {
        return context.get().containsKey(key);
    }

    public static void remove(String key) {
        context.get().remove(key);
        log.debug("Removed from context: {}", key);
    }

    public static void clear() {
        context.get().clear();
        log.debug("Cleared test context");
    }

    public static Map<String, Object> getAll() {
        return new ConcurrentHashMap<>(context.get());
    }
}
