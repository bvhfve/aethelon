package com.bvhfve.aethelon.core.config.migration;

import com.bvhfve.aethelon.core.AethelonCore;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

/**
 * MigrationUtils - Utility methods for configuration migrations
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None (pure utility class)
 * - Hooks into: Migration system
 * - Modifies: Configuration data during migrations
 * 
 * MODULE ROLE:
 * - Purpose: Provide common migration operations and helpers
 * - Dependencies: None (utility methods)
 * - Provides: JSON manipulation, validation, transformation helpers
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no Minecraft dependencies)
 * - Fabric API: Any (no Fabric dependencies)
 * - Breaking changes: Utility methods may evolve with migration needs
 * 
 * UTILITY CATEGORIES:
 * - JSON manipulation (add, remove, rename fields)
 * - Data transformation (type conversion, structure changes)
 * - Validation helpers (field existence, type checking)
 * - Migration logging and debugging
 */
public class MigrationUtils {
    
    /**
     * Safely get a string value from JSON object
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default value if field doesn't exist
     * @return String value or default
     */
    public static String getString(JsonObject object, String key, String defaultValue) {
        if (object.has(key) && !object.get(key).isJsonNull()) {
            return object.get(key).getAsString();
        }
        return defaultValue;
    }
    
    /**
     * Safely get a boolean value from JSON object
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default value if field doesn't exist
     * @return Boolean value or default
     */
    public static boolean getBoolean(JsonObject object, String key, boolean defaultValue) {
        if (object.has(key) && !object.get(key).isJsonNull()) {
            return object.get(key).getAsBoolean();
        }
        return defaultValue;
    }
    
    /**
     * Safely get an integer value from JSON object
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default value if field doesn't exist
     * @return Integer value or default
     */
    public static int getInt(JsonObject object, String key, int defaultValue) {
        if (object.has(key) && !object.get(key).isJsonNull()) {
            return object.get(key).getAsInt();
        }
        return defaultValue;
    }
    
    /**
     * Safely get a double value from JSON object
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default value if field doesn't exist
     * @return Double value or default
     */
    public static double getDouble(JsonObject object, String key, double defaultValue) {
        if (object.has(key) && !object.get(key).isJsonNull()) {
            return object.get(key).getAsDouble();
        }
        return defaultValue;
    }
    
    /**
     * Safely get a JSON object from another JSON object
     * 
     * @param object Parent JSON object
     * @param key Field key
     * @return JSON object or new empty object if field doesn't exist
     */
    public static JsonObject getObject(JsonObject object, String key) {
        if (object.has(key) && object.get(key).isJsonObject()) {
            return object.getAsJsonObject(key);
        }
        return new JsonObject();
    }
    
    /**
     * Safely get a JSON array from JSON object
     * 
     * @param object JSON object
     * @param key Field key
     * @return JSON array or new empty array if field doesn't exist
     */
    public static JsonArray getArray(JsonObject object, String key) {
        if (object.has(key) && object.get(key).isJsonArray()) {
            return object.getAsJsonArray(key);
        }
        return new JsonArray();
    }
    
    /**
     * Rename a field in JSON object
     * 
     * @param object JSON object
     * @param oldKey Old field name
     * @param newKey New field name
     * @return true if field was renamed, false if old field didn't exist
     */
    public static boolean renameField(JsonObject object, String oldKey, String newKey) {
        if (object.has(oldKey)) {
            JsonElement value = object.remove(oldKey);
            object.add(newKey, value);
            return true;
        }
        return false;
    }
    
    /**
     * Move a field from one object to another
     * 
     * @param source Source JSON object
     * @param target Target JSON object
     * @param key Field key to move
     * @return true if field was moved, false if field didn't exist in source
     */
    public static boolean moveField(JsonObject source, JsonObject target, String key) {
        if (source.has(key)) {
            JsonElement value = source.remove(key);
            target.add(key, value);
            return true;
        }
        return false;
    }
    
    /**
     * Copy a field from one object to another
     * 
     * @param source Source JSON object
     * @param target Target JSON object
     * @param sourceKey Source field key
     * @param targetKey Target field key
     * @return true if field was copied, false if source field didn't exist
     */
    public static boolean copyField(JsonObject source, JsonObject target, String sourceKey, String targetKey) {
        if (source.has(sourceKey)) {
            JsonElement value = source.get(sourceKey);
            target.add(targetKey, value);
            return true;
        }
        return false;
    }
    
    /**
     * Ensure a field exists with a default value
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default value to set if field doesn't exist
     */
    public static void ensureField(JsonObject object, String key, JsonElement defaultValue) {
        if (!object.has(key)) {
            object.add(key, defaultValue);
        }
    }
    
    /**
     * Ensure a string field exists with a default value
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default string value
     */
    public static void ensureStringField(JsonObject object, String key, String defaultValue) {
        ensureField(object, key, new JsonPrimitive(defaultValue));
    }
    
    /**
     * Ensure a boolean field exists with a default value
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default boolean value
     */
    public static void ensureBooleanField(JsonObject object, String key, boolean defaultValue) {
        ensureField(object, key, new JsonPrimitive(defaultValue));
    }
    
    /**
     * Ensure an integer field exists with a default value
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default integer value
     */
    public static void ensureIntField(JsonObject object, String key, int defaultValue) {
        ensureField(object, key, new JsonPrimitive(defaultValue));
    }
    
    /**
     * Ensure a double field exists with a default value
     * 
     * @param object JSON object
     * @param key Field key
     * @param defaultValue Default double value
     */
    public static void ensureDoubleField(JsonObject object, String key, double defaultValue) {
        ensureField(object, key, new JsonPrimitive(defaultValue));
    }
    
    /**
     * Ensure an object field exists
     * 
     * @param object JSON object
     * @param key Field key
     * @return The existing or newly created JSON object
     */
    public static JsonObject ensureObjectField(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonObject()) {
            JsonObject newObject = new JsonObject();
            object.add(key, newObject);
            return newObject;
        }
        return object.getAsJsonObject(key);
    }
    
    /**
     * Ensure an array field exists
     * 
     * @param object JSON object
     * @param key Field key
     * @return The existing or newly created JSON array
     */
    public static JsonArray ensureArrayField(JsonObject object, String key) {
        if (!object.has(key) || !object.get(key).isJsonArray()) {
            JsonArray newArray = new JsonArray();
            object.add(key, newArray);
            return newArray;
        }
        return object.getAsJsonArray(key);
    }
    
    /**
     * Remove multiple fields from JSON object
     * 
     * @param object JSON object
     * @param keys Field keys to remove
     * @return Number of fields actually removed
     */
    public static int removeFields(JsonObject object, String... keys) {
        int removed = 0;
        for (String key : keys) {
            if (object.remove(key) != null) {
                removed++;
            }
        }
        return removed;
    }
    
    /**
     * Convert string array to JSON array
     * 
     * @param strings String array
     * @return JSON array
     */
    public static JsonArray stringArrayToJsonArray(String[] strings) {
        JsonArray array = new JsonArray();
        for (String string : strings) {
            array.add(string);
        }
        return array;
    }
    
    /**
     * Convert JSON array to string list
     * 
     * @param array JSON array
     * @return List of strings
     */
    public static List<String> jsonArrayToStringList(JsonArray array) {
        List<String> list = new ArrayList<>();
        for (JsonElement element : array) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                list.add(element.getAsString());
            }
        }
        return list;
    }
    
    /**
     * Validate that required fields exist
     * 
     * @param object JSON object to validate
     * @param requiredFields Required field names
     * @return true if all required fields exist
     */
    public static boolean validateRequiredFields(JsonObject object, String... requiredFields) {
        for (String field : requiredFields) {
            if (!object.has(field)) {
                AethelonCore.LOGGER.warn("Missing required field: {}", field);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Log migration step for debugging
     * 
     * @param step Step description
     * @param details Additional details
     */
    public static void logMigrationStep(String step, String details) {
        AethelonCore.LOGGER.debug("Migration step: {} - {}", step, details);
    }
    
    /**
     * Log migration warning
     * 
     * @param message Warning message
     * @param args Message arguments
     */
    public static void logMigrationWarning(String message, Object... args) {
        AethelonCore.LOGGER.warn("Migration warning: " + message, args);
    }
    
    /**
     * Create a migration timestamp
     * 
     * @return Current timestamp as long
     */
    public static long createTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * Merge two JSON objects (target gets values from source)
     * 
     * @param target Target object to merge into
     * @param source Source object to merge from
     * @param overwrite Whether to overwrite existing fields
     */
    public static void mergeObjects(JsonObject target, JsonObject source, boolean overwrite) {
        for (String key : source.keySet()) {
            if (!target.has(key) || overwrite) {
                target.add(key, source.get(key));
            }
        }
    }
    
    /**
     * Deep copy a JSON element
     * 
     * @param element Element to copy
     * @return Deep copy of the element
     */
    public static JsonElement deepCopy(JsonElement element) {
        return com.google.gson.JsonParser.parseString(element.toString());
    }
    
    /**
     * Check if a field has a specific type
     * 
     * @param object JSON object
     * @param key Field key
     * @param expectedType Expected JSON element type
     * @return true if field exists and has expected type
     */
    public static boolean hasFieldOfType(JsonObject object, String key, Class<? extends JsonElement> expectedType) {
        if (!object.has(key)) {
            return false;
        }
        
        JsonElement element = object.get(key);
        return expectedType.isInstance(element);
    }
    
    /**
     * Convert legacy boolean string to actual boolean
     * 
     * @param object JSON object
     * @param key Field key
     */
    public static void convertStringBooleanToBoolean(JsonObject object, String key) {
        if (object.has(key)) {
            JsonElement element = object.get(key);
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                String value = element.getAsString().toLowerCase();
                boolean boolValue = "true".equals(value) || "yes".equals(value) || "1".equals(value);
                object.add(key, new JsonPrimitive(boolValue));
            }
        }
    }
    
    /**
     * Convert legacy number string to actual number
     * 
     * @param object JSON object
     * @param key Field key
     */
    public static void convertStringNumberToNumber(JsonObject object, String key) {
        if (object.has(key)) {
            JsonElement element = object.get(key);
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                try {
                    String value = element.getAsString();
                    if (value.contains(".")) {
                        double doubleValue = Double.parseDouble(value);
                        object.add(key, new JsonPrimitive(doubleValue));
                    } else {
                        int intValue = Integer.parseInt(value);
                        object.add(key, new JsonPrimitive(intValue));
                    }
                } catch (NumberFormatException e) {
                    logMigrationWarning("Failed to convert string to number for field: {}", key);
                }
            }
        }
    }
}