package com.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class YamlUtils {

    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    private YamlUtils() {
    }

    public static Map<String, Object> loadYamlFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return yamlMapper.readValue(content, Map.class);
        } catch (Exception e) {
            log.error("Failed to load YAML file: {}", filePath, e);
            return Collections.emptyMap();
        }
    }

    public static Map<String, Object> loadYamlFromClasspath(String resourcePath) {
        try (InputStream inputStream = YamlUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                log.warn("YAML resource not found: {}", resourcePath);
                return Collections.emptyMap();
            }
            return yamlMapper.readValue(inputStream, Map.class);
        } catch (Exception e) {
            log.error("Failed to load YAML from classpath: {}", resourcePath, e);
            return Collections.emptyMap();
        }
    }

    public static <T> T loadYamlFile(String filePath, Class<T> clazz) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return yamlMapper.readValue(content, clazz);
        } catch (Exception e) {
            log.error("Failed to load YAML file to object: {}", filePath, e);
            return null;
        }
    }

    public static String toYaml(Object object) {
        try {
            return yamlMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Failed to convert object to YAML", e);
            return "";
        }
    }

    public static String yamlToJson(String yamlString) {
        try {
            JsonNode jsonNode = yamlMapper.readTree(yamlString);
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (Exception e) {
            log.error("Failed to convert YAML to JSON", e);
            return "";
        }
    }

    public static String jsonToYaml(String jsonString) {
        try {
            JsonNode jsonNode = jsonMapper.readTree(jsonString);
            return yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (Exception e) {
            log.error("Failed to convert JSON to YAML", e);
            return "";
        }
    }
}
