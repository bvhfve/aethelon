package com.bvhfve.aethelon.core.config.migration;

import com.google.gson.JsonObject;

/**
 * ConfigMigration - Interface for configuration migration operations
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None (pure configuration management)
 * - Hooks into: Configuration loading system
 * - Modifies: Configuration data during migration
 * 
 * MODULE ROLE:
 * - Purpose: Define contract for configuration version migrations
 * - Dependencies: None (interface definition)
 * - Provides: Migration operation contract, version compatibility
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no Minecraft dependencies)
 * - Fabric API: Any (no Fabric dependencies)
 * - Breaking changes: Interface may evolve with migration needs
 * 
 * MIGRATION ARCHITECTURE:
 * Each migration handles upgrading configuration from one version to the next.
 * Migrations are applied sequentially to bring old configurations up to date.
 * All migrations should be idempotent and safe to run multiple times.
 */
public interface ConfigMigration {
    
    /**
     * Get the source version this migration upgrades from
     * 
     * @return Source configuration version (e.g., "1.0.0")
     */
    String getFromVersion();
    
    /**
     * Get the target version this migration upgrades to
     * 
     * @return Target configuration version (e.g., "1.1.0")
     */
    String getToVersion();
    
    /**
     * Get a description of what this migration does
     * 
     * @return Human-readable description of migration changes
     */
    String getDescription();
    
    /**
     * Check if this migration can be applied to the given configuration
     * 
     * @param config Configuration object to check
     * @return true if migration can be safely applied
     */
    boolean canMigrate(JsonObject config);
    
    /**
     * Apply the migration to the configuration
     * 
     * This method should:
     * - Modify the configuration in-place
     * - Update the version field
     * - Add any new required fields with defaults
     * - Remove or rename deprecated fields
     * - Validate the result
     * 
     * @param config Configuration object to migrate
     * @throws ConfigMigrationException if migration fails
     */
    void migrate(JsonObject config) throws ConfigMigrationException;
    
    /**
     * Validate that the migration was successful
     * 
     * @param config Migrated configuration object
     * @return true if migration result is valid
     */
    boolean validateMigration(JsonObject config);
    
    /**
     * Get the priority of this migration (lower numbers run first)
     * Used when multiple migrations target the same version
     * 
     * @return Migration priority (0-100, default 50)
     */
    default int getPriority() {
        return 50;
    }
    
    /**
     * Check if this migration is reversible
     * 
     * @return true if migration can be reversed
     */
    default boolean isReversible() {
        return false;
    }
    
    /**
     * Reverse the migration (if supported)
     * 
     * @param config Configuration object to reverse
     * @throws ConfigMigrationException if reversal fails or is not supported
     */
    default void reverse(JsonObject config) throws ConfigMigrationException {
        throw new ConfigMigrationException("Migration reversal not supported: " + getClass().getSimpleName());
    }
}