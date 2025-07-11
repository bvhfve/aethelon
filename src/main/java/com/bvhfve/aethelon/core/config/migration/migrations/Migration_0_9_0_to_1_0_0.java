package com.bvhfve.aethelon.core.config.migration.migrations;

import com.bvhfve.aethelon.core.config.migration.ConfigMigration;
import com.bvhfve.aethelon.core.config.migration.ConfigMigrationException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Migration_0_9_0_to_1_0_0 - Migrate from legacy config format to modular structure
 * 
 * This migration handles the transition from the old monolithic configuration
 * format to the new modular phase-based structure introduced in version 1.0.0.
 * 
 * CHANGES:
 * - Add configVersion field
 * - Restructure phase configurations
 * - Add new debug options
 * - Migrate legacy settings to new structure
 * - Add default values for new features
 */
public class Migration_0_9_0_to_1_0_0 implements ConfigMigration {
    
    @Override
    public String getFromVersion() {
        return "0.9.0";
    }
    
    @Override
    public String getToVersion() {
        return "1.0.0";
    }
    
    @Override
    public String getDescription() {
        return "Migrate from legacy config format to modular phase-based structure";
    }
    
    @Override
    public boolean canMigrate(JsonObject config) {
        // Check if this looks like a legacy configuration
        // Legacy configs don't have configVersion and may have old field names
        return !config.has("configVersion") || 
               config.has("legacyField") || 
               config.has("oldPhaseSettings");
    }
    
    @Override
    public void migrate(JsonObject config) throws ConfigMigrationException {
        try {
            // Add version field
            config.add("configVersion", new JsonPrimitive("1.0.0"));
            
            // Migrate legacy phase settings
            migrateLegacyPhaseSettings(config);
            
            // Add new debug configuration
            addDebugConfiguration(config);
            
            // Migrate phase-specific configurations
            migratePhaseConfigurations(config);
            
            // Remove deprecated fields
            removeDeprecatedFields(config);
            
        } catch (Exception e) {
            throw new ConfigMigrationException("Failed to migrate configuration from 0.9.0 to 1.0.0", e);
        }
    }
    
    @Override
    public boolean validateMigration(JsonObject config) {
        // Validate that migration was successful
        return config.has("configVersion") &&
               "1.0.0".equals(config.get("configVersion").getAsString()) &&
               config.has("phases") &&
               config.has("debug");
    }
    
    /**
     * Migrate legacy phase settings to new structure
     */
    private void migrateLegacyPhaseSettings(JsonObject config) {
        JsonObject phases = new JsonObject();
        
        // Phase 1: Basic Entity Foundation
        JsonObject phase1 = new JsonObject();
        phase1.add("enabled", new JsonPrimitive(true));
        
        JsonObject phase1Modules = new JsonObject();
        phase1Modules.add("entity", new JsonPrimitive(true));
        phase1Modules.add("client", new JsonPrimitive(true));
        phase1Modules.add("spawn", new JsonPrimitive(true));
        phase1.add("modules", phase1Modules);
        
        phases.add("phase1", phase1);
        
        // Add other phases as disabled by default
        for (int i = 2; i <= 10; i++) {
            JsonObject phase = new JsonObject();
            phase.add("enabled", new JsonPrimitive(false));
            phase.add("modules", new JsonObject());
            phases.add("phase" + i, phase);
        }
        
        config.add("phases", phases);
    }
    
    /**
     * Add new debug configuration
     */
    private void addDebugConfiguration(JsonObject config) {
        JsonObject debug = new JsonObject();
        debug.add("logModuleLoading", new JsonPrimitive(true));
        debug.add("enableHotReload", new JsonPrimitive(false));
        debug.add("showStateParticles", new JsonPrimitive(false));
        debug.add("verboseLogging", new JsonPrimitive(false));
        debug.add("enableDevCommands", new JsonPrimitive(false));
        
        config.add("debug", debug);
    }
    
    /**
     * Migrate phase-specific configurations
     */
    private void migratePhaseConfigurations(JsonObject config) {
        // Migrate Phase 1 configuration if it exists
        if (config.has("entitySettings") || config.has("spawnSettings")) {
            JsonObject phase1Config = new JsonObject();
            
            // Migrate entity settings
            JsonObject entityConfig = new JsonObject();
            entityConfig.add("enabled", new JsonPrimitive(true));
            entityConfig.add("maxHealth", new JsonPrimitive(1000.0));
            entityConfig.add("movementSpeed", new JsonPrimitive(0.1));
            entityConfig.add("followRange", new JsonPrimitive(64.0));
            entityConfig.add("knockbackResistance", new JsonPrimitive(1.0));
            entityConfig.add("canBePushed", new JsonPrimitive(false));
            entityConfig.add("canBreatheUnderwater", new JsonPrimitive(true));
            entityConfig.add("immuneToFire", new JsonPrimitive(false));
            entityConfig.add("immuneToExplosions", new JsonPrimitive(false));
            entityConfig.add("entityWidth", new JsonPrimitive(4.0));
            entityConfig.add("entityHeight", new JsonPrimitive(2.0));
            entityConfig.add("eyeHeight", new JsonPrimitive(1.8));
            
            // Migrate legacy entity settings if they exist
            if (config.has("entitySettings")) {
                JsonObject legacyEntity = config.getAsJsonObject("entitySettings");
                if (legacyEntity.has("health")) {
                    entityConfig.add("maxHealth", legacyEntity.get("health"));
                }
                if (legacyEntity.has("speed")) {
                    entityConfig.add("movementSpeed", legacyEntity.get("speed"));
                }
            }
            
            phase1Config.add("entity", entityConfig);
            
            // Migrate client settings
            JsonObject clientConfig = new JsonObject();
            clientConfig.add("enabled", new JsonPrimitive(true));
            clientConfig.add("enableCustomModel", new JsonPrimitive(true));
            clientConfig.add("enableAnimations", new JsonPrimitive(true));
            clientConfig.add("enableParticles", new JsonPrimitive(true));
            clientConfig.add("renderDistance", new JsonPrimitive(128.0));
            clientConfig.add("showHealthBar", new JsonPrimitive(true));
            clientConfig.add("showNameTag", new JsonPrimitive(true));
            clientConfig.add("nameTagDistance", new JsonPrimitive(32.0));
            clientConfig.add("enableLOD", new JsonPrimitive(true));
            clientConfig.add("maxRenderDistance", new JsonPrimitive(256));
            clientConfig.add("enableCulling", new JsonPrimitive(true));
            
            phase1Config.add("client", clientConfig);
            
            // Migrate spawn settings
            JsonObject spawnConfig = new JsonObject();
            spawnConfig.add("enabled", new JsonPrimitive(true));
            spawnConfig.add("spawnWeight", new JsonPrimitive(5));
            spawnConfig.add("minGroupSize", new JsonPrimitive(1));
            spawnConfig.add("maxGroupSize", new JsonPrimitive(1));
            spawnConfig.add("maxWorldPopulation", new JsonPrimitive(10));
            spawnConfig.add("maxChunkPopulation", new JsonPrimitive(1));
            spawnConfig.add("minWaterDepth", new JsonPrimitive(10));
            spawnConfig.add("maxWaterDepth", new JsonPrimitive(256));
            spawnConfig.add("requiresDeepOcean", new JsonPrimitive(true));
            spawnConfig.add("avoidShallowWater", new JsonPrimitive(true));
            spawnConfig.add("enableSpawnEgg", new JsonPrimitive(true));
            spawnConfig.add("spawnEggName", new JsonPrimitive("Aethelon Spawn Egg"));
            
            // Migrate legacy spawn settings if they exist
            if (config.has("spawnSettings")) {
                JsonObject legacySpawn = config.getAsJsonObject("spawnSettings");
                if (legacySpawn.has("weight")) {
                    spawnConfig.add("spawnWeight", legacySpawn.get("weight"));
                }
                if (legacySpawn.has("maxPopulation")) {
                    spawnConfig.add("maxWorldPopulation", legacySpawn.get("maxPopulation"));
                }
            }
            
            phase1Config.add("spawn", spawnConfig);
            
            // Add phase-level settings
            phase1Config.add("enabled", new JsonPrimitive(true));
            phase1Config.add("debugMode", new JsonPrimitive(false));
            
            config.add("phase1Config", phase1Config);
        }
    }
    
    /**
     * Remove deprecated fields from configuration
     */
    private void removeDeprecatedFields(JsonObject config) {
        // Remove legacy fields that are no longer used
        config.remove("entitySettings");
        config.remove("spawnSettings");
        config.remove("legacyField");
        config.remove("oldPhaseSettings");
        config.remove("deprecatedOption");
    }
    
    @Override
    public int getPriority() {
        return 10; // High priority for major version migration
    }
    
    @Override
    public boolean isReversible() {
        return false; // This migration is not reversible due to structural changes
    }
}