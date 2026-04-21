package com.automation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EnvironmentConfig {

    private static final Map<String, Object> CONFIG_CACHE = new ConcurrentHashMap<>();
    private static final String DEFAULT_ENV = "dev";
    private static String currentEnv;

    static {
        loadEnvironment();
    }

    private EnvironmentConfig() {
    }

    private static void loadEnvironment() {
        currentEnv = System.getProperty("env", DEFAULT_ENV);
        log.info("Loading configuration for environment: {}", currentEnv);
        
        String configFile = String.format("config/%s.yaml", currentEnv);
        loadConfigFromFile(configFile);
        
        String commonConfig = "config/common.yaml";
        loadCommonConfig(commonConfig);
    }

    @SuppressWarnings("unchecked")
    private static void loadConfigFromFile(String configFile) {
        try (InputStream inputStream = EnvironmentConfig.class.getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                log.warn("Configuration file not found: {}", configFile);
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> config = mapper.readValue(inputStream, Map.class);
            
            if (config != null) {
                flattenConfig("", config);
                log.info("Successfully loaded configuration from: {}", configFile);
            }
        } catch (Exception e) {
            log.error("Failed to load configuration from: {}", configFile, e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void loadCommonConfig(String configFile) {
        try (InputStream inputStream = EnvironmentConfig.class.getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                log.warn("Common configuration file not found: {}", configFile);
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> config = mapper.readValue(inputStream, Map.class);
            
            if (config != null) {
                config.forEach((key, value) -> {
                    String fullKey = "common." + key;
                    if (!CONFIG_CACHE.containsKey(fullKey)) {
                        if (value instanceof Map) {
                            flattenConfig("common.", Map.of(key, value));
                        } else {
                            CONFIG_CACHE.put(fullKey, value);
                        }
                    }
                });
                log.info("Successfully loaded common configuration");
            }
        } catch (Exception e) {
            log.error("Failed to load common configuration", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void flattenConfig(String prefix, Map<String, Object> config) {
        config.forEach((key, value) -> {
            String fullKey = prefix.isEmpty() ? key : prefix + key;
            if (value instanceof Map) {
                flattenConfig(fullKey + ".", (Map<String, Object>) value);
            } else {
                CONFIG_CACHE.put(fullKey, value);
            }
        });
    }

    public static String getCurrentEnv() {
        return currentEnv;
    }

    public static String getString(String key) {
        Object value = CONFIG_CACHE.get(key);
        return value != null ? value.toString() : null;
    }

    public static String getString(String key, String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }

    public static Integer getInt(String key) {
        String value = getString(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    public static Integer getInt(String key, Integer defaultValue) {
        Integer value = getInt(key);
        return value != null ? value : defaultValue;
    }

    public static Boolean getBoolean(String key) {
        String value = getString(key);
        return value != null ? Boolean.parseBoolean(value) : null;
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value != null ? value : defaultValue;
    }

    public static String getBaseUrl() {
        return getString("api.baseUrl");
    }

    public static Integer getTimeout() {
        return getInt("api.timeout", 30000);
    }

    public static String getUsername() {
        return getString("auth.username");
    }

    public static String getPassword() {
        return getString("auth.password");
    }

    public static void reload() {
        CONFIG_CACHE.clear();
        loadEnvironment();
    }
}
