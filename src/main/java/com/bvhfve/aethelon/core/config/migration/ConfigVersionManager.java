package com.bvhfve.aethelon.core.config.migration;

import com.bvhfve.aethelon.core.AethelonCore;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * ConfigVersionManager - Handles configuration version management and validation
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None (pure version management)
 * - Hooks into: Configuration system
 * - Modifies: Version metadata in configurations
 * 
 * MODULE ROLE:
 * - Purpose: Manage configuration version information and validation
 * - Dependencies: None (utility class)
 * - Provides: Version parsing, comparison, validation, metadata management
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no Minecraft dependencies)
 * - Fabric API: Any (no Fabric dependencies)
 * - Breaking changes: Version format may evolve
 * 
 * VERSION FORMAT:
 * Uses semantic versioning (MAJOR.MINOR.PATCH) with optional pre-release identifiers.
 * Examples: "1.0.0", "1.2.3-beta", "2.0.0-alpha.1"
 */
public class ConfigVersionManager {
    
    private static final Pattern VERSION_PATTERN = Pattern.compile(
        "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    );
    
    private static final String VERSION_FIELD = "configVersion";
    private static final String VERSION_METADATA_FIELD = "versionMetadata";
    
    /**
     * Parse a version string into components
     * 
     * @param version Version string to parse
     * @return Parsed version object
     * @throws IllegalArgumentException if version format is invalid
     */
    public static ConfigVersion parseVersion(String version) {
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("Version cannot be null or empty");
        }
        
        if (!VERSION_PATTERN.matcher(version).matches()) {
            throw new IllegalArgumentException("Invalid version format: " + version);
        }
        
        String[] parts = version.split("[-+]");
        String[] versionParts = parts[0].split("\\.");
        
        int major = Integer.parseInt(versionParts[0]);
        int minor = Integer.parseInt(versionParts[1]);
        int patch = Integer.parseInt(versionParts[2]);
        
        String preRelease = null;
        String buildMetadata = null;
        
        if (parts.length > 1) {
            if (version.contains("-") && !version.contains("+")) {
                preRelease = parts[1];
            } else if (version.contains("+") && !version.contains("-")) {
                buildMetadata = parts[1];
            } else if (version.contains("-") && version.contains("+")) {
                preRelease = parts[1];
                buildMetadata = parts[2];
            }
        }
        
        return new ConfigVersion(major, minor, patch, preRelease, buildMetadata);
    }
    
    /**
     * Compare two version strings
     * 
     * @param version1 First version
     * @param version2 Second version
     * @return -1 if version1 < version2, 0 if equal, 1 if version1 > version2
     */
    public static int compareVersions(String version1, String version2) {
        ConfigVersion v1 = parseVersion(version1);
        ConfigVersion v2 = parseVersion(version2);
        return v1.compareTo(v2);
    }
    
    /**
     * Check if a version is compatible with a target version
     * 
     * @param version Version to check
     * @param targetVersion Target version
     * @return true if versions are compatible
     */
    public static boolean isCompatible(String version, String targetVersion) {
        try {
            ConfigVersion v1 = parseVersion(version);
            ConfigVersion v2 = parseVersion(targetVersion);
            
            // Same major version is considered compatible
            return v1.getMajor() == v2.getMajor();
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to check version compatibility: {} vs {}", version, targetVersion, e);
            return false;
        }
    }
    
    /**
     * Set version information in configuration
     * 
     * @param config Configuration object
     * @param version Version string
     */
    public static void setVersion(JsonObject config, String version) {
        validateVersion(version);
        config.add(VERSION_FIELD, new JsonPrimitive(version));
        
        // Update metadata
        updateVersionMetadata(config, version);
    }
    
    /**
     * Get version from configuration
     * 
     * @param config Configuration object
     * @return Version string or null if not present
     */
    public static String getVersion(JsonObject config) {
        if (config.has(VERSION_FIELD)) {
            return config.get(VERSION_FIELD).getAsString();
        }
        return null;
    }
    
    /**
     * Validate version format
     * 
     * @param version Version string to validate
     * @throws IllegalArgumentException if version is invalid
     */
    public static void validateVersion(String version) {
        parseVersion(version); // Will throw if invalid
    }
    
    /**
     * Check if configuration has version information
     * 
     * @param config Configuration object
     * @return true if version is present and valid
     */
    public static boolean hasValidVersion(JsonObject config) {
        try {
            String version = getVersion(config);
            if (version == null) {
                return false;
            }
            validateVersion(version);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Update version metadata
     * 
     * @param config Configuration object
     * @param version New version
     */
    private static void updateVersionMetadata(JsonObject config, String version) {
        JsonObject metadata = new JsonObject();
        metadata.add("version", new JsonPrimitive(version));
        metadata.add("updatedAt", new JsonPrimitive(Instant.now().toString()));
        metadata.add("updatedBy", new JsonPrimitive("ConfigVersionManager"));
        
        config.add(VERSION_METADATA_FIELD, metadata);
    }
    
    /**
     * Get version metadata
     * 
     * @param config Configuration object
     * @return Version metadata or null if not present
     */
    public static JsonObject getVersionMetadata(JsonObject config) {
        if (config.has(VERSION_METADATA_FIELD)) {
            return config.getAsJsonObject(VERSION_METADATA_FIELD);
        }
        return null;
    }
    
    /**
     * Create a new version by incrementing components
     * 
     * @param currentVersion Current version string
     * @param component Component to increment (major, minor, patch)
     * @return New version string
     */
    public static String incrementVersion(String currentVersion, VersionComponent component) {
        ConfigVersion version = parseVersion(currentVersion);
        
        switch (component) {
            case MAJOR:
                return new ConfigVersion(version.getMajor() + 1, 0, 0, null, null).toString();
            case MINOR:
                return new ConfigVersion(version.getMajor(), version.getMinor() + 1, 0, null, null).toString();
            case PATCH:
                return new ConfigVersion(version.getMajor(), version.getMinor(), version.getPatch() + 1, null, null).toString();
            default:
                throw new IllegalArgumentException("Unknown version component: " + component);
        }
    }
    
    /**
     * Version component enumeration
     */
    public enum VersionComponent {
        MAJOR, MINOR, PATCH
    }
    
    /**
     * Configuration version representation
     */
    public static class ConfigVersion implements Comparable<ConfigVersion> {
        private final int major;
        private final int minor;
        private final int patch;
        private final String preRelease;
        private final String buildMetadata;
        
        public ConfigVersion(int major, int minor, int patch, String preRelease, String buildMetadata) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.preRelease = preRelease;
            this.buildMetadata = buildMetadata;
        }
        
        public int getMajor() { return major; }
        public int getMinor() { return minor; }
        public int getPatch() { return patch; }
        public String getPreRelease() { return preRelease; }
        public String getBuildMetadata() { return buildMetadata; }
        
        @Override
        public int compareTo(ConfigVersion other) {
            // Compare major.minor.patch
            int result = Integer.compare(this.major, other.major);
            if (result != 0) return result;
            
            result = Integer.compare(this.minor, other.minor);
            if (result != 0) return result;
            
            result = Integer.compare(this.patch, other.patch);
            if (result != 0) return result;
            
            // Handle pre-release versions
            if (this.preRelease == null && other.preRelease == null) return 0;
            if (this.preRelease == null) return 1; // Release > pre-release
            if (other.preRelease == null) return -1; // Pre-release < release
            
            return this.preRelease.compareTo(other.preRelease);
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(major).append('.').append(minor).append('.').append(patch);
            
            if (preRelease != null) {
                sb.append('-').append(preRelease);
            }
            
            if (buildMetadata != null) {
                sb.append('+').append(buildMetadata);
            }
            
            return sb.toString();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            
            ConfigVersion that = (ConfigVersion) obj;
            return major == that.major &&
                   minor == that.minor &&
                   patch == that.patch &&
                   java.util.Objects.equals(preRelease, that.preRelease) &&
                   java.util.Objects.equals(buildMetadata, that.buildMetadata);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(major, minor, patch, preRelease, buildMetadata);
        }
    }
}