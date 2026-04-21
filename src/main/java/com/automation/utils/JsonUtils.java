package com.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;

@Slf4j
public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Failed to convert object to JSON", e);
            return "";
        }
    }

    public static String toPrettyJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            log.error("Failed to convert object to pretty JSON", e);
            return "";
        }
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("Failed to parse JSON to object: {}", clazz.getName(), e);
            return null;
        }
    }

    public static Map<String, Object> toMap(String jsonString) {
        try {
            return mapper.readValue(jsonString, Map.class);
        } catch (Exception e) {
            log.error("Failed to convert JSON to Map", e);
            return null;
        }
    }

    public static String fromMap(Map<String, Object> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Failed to convert Map to JSON", e);
            return "";
        }
    }

    public static String getValueByPath(String jsonString, String path) {
        try {
            JsonNode root = mapper.readTree(jsonString);
            String[] paths = path.split("\\.");
            JsonNode current = root;
            
            for (String p : paths) {
                if (p.matches("\\d+")) {
                    current = current.get(Integer.parseInt(p));
                } else {
                    current = current.get(p);
                }
                if (current == null) {
                    return null;
                }
            }
            
            return current.asText();
        } catch (Exception e) {
            log.error("Failed to get value by path: {}", path, e);
            return null;
        }
    }

    public static String merge(String json1, String json2) {
        try {
            JsonNode node1 = mapper.readTree(json1);
            JsonNode node2 = mapper.readTree(json2);
            JsonNode merged = mergeJsonNodes(node1, node2);
            return mapper.writeValueAsString(merged);
        } catch (Exception e) {
            log.error("Failed to merge JSON", e);
            return json1;
        }
    }

    private static JsonNode mergeJsonNodes(JsonNode mainNode, JsonNode updateNode) {
        if (mainNode == null) {
            return updateNode;
        }
        if (updateNode == null) {
            return mainNode;
        }
        
        if (mainNode.isObject() && updateNode.isObject()) {
            ObjectNode result = mainNode.deepCopy();
            Iterator<Map.Entry<String, JsonNode>> fields = updateNode.fields();
            
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();
                
                if (result.has(fieldName)) {
                    result.set(fieldName, mergeJsonNodes(result.get(fieldName), fieldValue));
                } else {
                    result.set(fieldName, fieldValue);
                }
            }
            
            return result;
        } else {
            return updateNode;
        }
    }

    public static boolean isValidJson(String jsonString) {
        try {
            mapper.readTree(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
