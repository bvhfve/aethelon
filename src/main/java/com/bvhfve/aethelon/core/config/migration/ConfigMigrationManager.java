package com.bvhfve.aethelon.core.config.migration;

import com.bvhfve.aethelon.core.AethelonCore;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ConfigMigrationManager - Manages configuration version migrations
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None (pure configuration management)
 * - Hooks into: Configuration loading system
 * - Modifies: Configuration data during loading
 * 
 * MODULE ROLE:
 * - Purpose: Coordinate configuration migrations across versions
 * - Dependencies: ConfigMigration implementations
 * - Provides: Migration orchestration, version tracking, backup management
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no Minecraft dependencies)
 * - Fabric API: Any (no Fabric dependencies)
 * - Breaking changes: Migration system may evolve with new requirements
 * 
 * MIGRATION PROCESS:
 * 1. Detect current configuration version
 * 2. Find migration path to target version
 * 3. Create backup of original configuration
 * 4. Apply migrations sequentially
 * 5. Validate final configuration
 * 6. Update version metadata
 */
public class ConfigMigrationManager {
    
    private static final String CURRENT_VERSION = "1.0.0";
    private static final String VERSION_FIELD = "configVersion";
    private static final String MIGRATION_HISTORY_FIELD = "migrationHistory";
    
    private final List<ConfigMigration> registeredMigrations = new ArrayList<>();
    private final Map<String, List<ConfigMigration>> migrationsByFromVersion = new HashMap<>();
    
    /**
     * Register a configuration migration
     * 
     * @param migration Migration to register
     */
    public void registerMigration(ConfigMigration migration) {
        registeredMigrations.add(migration);
        
        // Index by from version for quick lookup
        migrationsByFromVersion.computeIfAbsent(migration.getFromVersion(), k -> new ArrayList<>())
                               .add(migration);
        
        // Sort by priority
        migrationsByFromVersion.get(migration.getFromVersion())
                              .sort(Comparator.comparingInt(ConfigMigration::getPriority));
        
        AethelonCore.LOGGER.debug("Registered migration: {} -> {} ({})", 
            migration.getFromVersion(), migration.getToVersion(), migration.getDescription());
    }
    
    /**
     * Check if configuration needs migration
     * 
     * @param config Configuration to check
     * @return true if migration is needed
     */
    public boolean needsMigration(JsonObject config) {
        String currentVersion = getCurrentVersion(config);
        return !CURRENT_VERSION.equals(currentVersion);
    }
    
    /**
     * Migrate configuration to current version
     * 
     * @param config Configuration to migrate
     * @return Migration result with details
     * @throws ConfigMigrationException if migration fails
     */
    public MigrationResult migrateConfiguration(JsonObject config) throws ConfigMigrationException {
        String startVersion = getCurrentVersion(config);
        
        if (CURRENT_VERSION.equals(startVersion)) {
            return new MigrationResult(startVersion, CURRENT_VERSION, Collections.emptyList(), false);
        }
        
        AethelonCore.LOGGER.info("Starting configuration migration from {} to {}", startVersion, CURRENT_VERSION);
        
        // Create backup
        JsonObject backup = config.deepCopy();
        
        try {
            // Find migration path
            List<ConfigMigration> migrationPath = findMigrationPath(startVersion, CURRENT_VERSION);
            
            if (migrationPath.isEmpty()) {
                throw new ConfigMigrationException(
                    "No migration path found from " + startVersion + " to " + CURRENT_VERSION);
            }
            
            // Apply migrations
            List<String> appliedMigrations = new ArrayList<>();
            String currentVersion = startVersion;
            
            for (ConfigMigration migration : migrationPath) {
                AethelonCore.LOGGER.info("Applying migration: {} -> {} ({})", 
                    migration.getFromVersion(), migration.getToVersion(), migration.getDescription());
                
                if (!migration.canMigrate(config)) {
                    throw new ConfigMigrationException(
                        "Migration cannot be applied: " + migration.getClass().getSimpleName(),
                        migration.getClass().getSimpleName(),
                        migration.getFromVersion(),
                        migration.getToVersion()
                    );
                }
                
                migration.migrate(config);
                
                if (!migration.validateMigration(config)) {
                    throw new ConfigMigrationException(
                        "Migration validation failed: " + migration.getClass().getSimpleName(),
                        migration.getClass().getSimpleName(),
                        migration.getFromVersion(),
                        migration.getToVersion()
                    );
                }
                
                currentVersion = migration.getToVersion();
                appliedMigrations.add(migration.getClass().getSimpleName());
                
                // Update migration history
                addToMigrationHistory(config, migration);
            }
            
            // Update version
            config.add(VERSION_FIELD, new JsonPrimitive(CURRENT_VERSION));
            
            AethelonCore.LOGGER.info("Configuration migration completed successfully: {} -> {}", 
                startVersion, CURRENT_VERSION);
            
            return new MigrationResult(startVersion, CURRENT_VERSION, appliedMigrations, true);
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Configuration migration failed, restoring backup", e);
            
            // Restore from backup
            config.entrySet().clear();
            backup.entrySet().forEach(entry -> config.add(entry.getKey(), entry.getValue()));
            
            if (e instanceof ConfigMigrationException) {
                throw e;
            } else {
                throw new ConfigMigrationException("Migration failed: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Get current configuration version
     * 
     * @param config Configuration object
     * @return Current version string
     */
    public String getCurrentVersion(JsonObject config) {
        if (config.has(VERSION_FIELD)) {
            return config.get(VERSION_FIELD).getAsString();
        }
        
        // Legacy configurations without version field are considered version 0.9.0
        return "0.9.0";
    }
    
    /**
     * Find migration path from source to target version
     * 
     * @param fromVersion Source version
     * @param toVersion Target version
     * @return List of migrations to apply in order
     * @throws ConfigMigrationException if no path exists
     */
    private List<ConfigMigration> findMigrationPath(String fromVersion, String toVersion) 
            throws ConfigMigrationException {
        
        List<ConfigMigration> path = new ArrayList<>();
        String currentVersion = fromVersion;
        Set<String> visited = new HashSet<>();
        
        while (!currentVersion.equals(toVersion)) {
            if (visited.contains(currentVersion)) {
                throw new ConfigMigrationException("Circular migration dependency detected at version: " + currentVersion);
            }
            visited.add(currentVersion);
            
            List<ConfigMigration> availableMigrations = migrationsByFromVersion.get(currentVersion);
            if (availableMigrations == null || availableMigrations.isEmpty()) {
                throw new ConfigMigrationException("No migration available from version: " + currentVersion);
            }
            
            // Find the best migration (highest priority, closest to target)
            ConfigMigration bestMigration = findBestMigration(availableMigrations, toVersion);
            if (bestMigration == null) {
                throw new ConfigMigrationException("No suitable migration found from version: " + currentVersion);
            }
            
            path.add(bestMigration);
            currentVersion = bestMigration.getToVersion();
        }
        
        return path;
    }
    
    /**
     * Find the best migration from available options
     * 
     * @param migrations Available migrations
     * @param targetVersion Target version
     * @return Best migration or null if none suitable
     */
    private ConfigMigration findBestMigration(List<ConfigMigration> migrations, String targetVersion) {
        // For now, just return the first migration (they're sorted by priority)
        // In the future, this could implement more sophisticated selection logic
        return migrations.isEmpty() ? null : migrations.get(0);
    }
    
    /**
     * Add migration to history
     * 
     * @param config Configuration object
     * @param migration Applied migration
     */
    private void addToMigrationHistory(JsonObject config, ConfigMigration migration) {
        if (!config.has(MIGRATION_HISTORY_FIELD)) {
            config.add(MIGRATION_HISTORY_FIELD, new JsonObject());
        }
        
        JsonObject history = config.getAsJsonObject(MIGRATION_HISTORY_FIELD);
        JsonObject migrationRecord = new JsonObject();
        migrationRecord.add("from", new JsonPrimitive(migration.getFromVersion()));
        migrationRecord.add("to", new JsonPrimitive(migration.getToVersion()));
        migrationRecord.add("description", new JsonPrimitive(migration.getDescription()));
        migrationRecord.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));
        
        history.add(migration.getClass().getSimpleName(), migrationRecord);
    }
    
    /**
     * Get all registered migrations
     * 
     * @return List of registered migrations
     */
    public List<ConfigMigration> getRegisteredMigrations() {
        return new ArrayList<>(registeredMigrations);
    }
    
    /**
     * Get migrations for a specific version
     * 
     * @param fromVersion Source version
     * @return List of migrations from that version
     */
    public List<ConfigMigration> getMigrationsFromVersion(String fromVersion) {
        return migrationsByFromVersion.getOrDefault(fromVersion, Collections.emptyList());
    }
    
    /**
     * Get current target version
     * 
     * @return Current version string
     */
    public String getCurrentTargetVersion() {
        return CURRENT_VERSION;
    }
    
    /**
     * Validate configuration version
     * 
     * @param config Configuration to validate
     * @return true if version is valid
     */
    public boolean isValidVersion(JsonObject config) {
        try {
            String version = getCurrentVersion(config);
            return version != null && !version.trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Result of a migration operation
     */
    public static class MigrationResult {
        private final String fromVersion;
        private final String toVersion;
        private final List<String> appliedMigrations;
        private final boolean migrationPerformed;
        
        public MigrationResult(String fromVersion, String toVersion, 
                             List<String> appliedMigrations, boolean migrationPerformed) {
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
            this.appliedMigrations = new ArrayList<>(appliedMigrations);
            this.migrationPerformed = migrationPerformed;
        }
        
        public String getFromVersion() { return fromVersion; }
        public String getToVersion() { return toVersion; }
        public List<String> getAppliedMigrations() { return new ArrayList<>(appliedMigrations); }
        public boolean wasMigrationPerformed() { return migrationPerformed; }
        
        @Override
        public String toString() {
            return String.format("MigrationResult{%s -> %s, migrations: %s, performed: %s}", 
                fromVersion, toVersion, appliedMigrations, migrationPerformed);
        }
    }
}