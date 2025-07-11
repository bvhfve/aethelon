package com.bvhfve.aethelon.core.config.migration.migrations;

import com.bvhfve.aethelon.core.config.migration.ConfigMigration;
import com.bvhfve.aethelon.core.config.migration.ConfigMigrationException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Migration_1_0_0_to_1_1_0 - Add Phase 2 configuration and enhanced features
 * 
 * This migration adds support for Phase 2 (Entity Behavior & Movement) and
 * introduces new configuration options for enhanced functionality.
 * 
 * CHANGES:
 * - Enable Phase 2 configuration structure
 * - Add AI behavior settings
 * - Add performance optimization options
 * - Add new debug features
 * - Update allowed biomes list
 * - Add migration history tracking
 */
public class Migration_1_0_0_to_1_1_0 implements ConfigMigration {
    
    @Override
    public String getFromVersion() {
        return "1.0.0";
    }
    
    @Override
    public String getToVersion() {
        return "1.1.0";
    }
    
    @Override
    public String getDescription() {
        return "Add Phase 2 configuration and enhanced features";
    }
    
    @Override
    public boolean canMigrate(JsonObject config) {
        // Check if this is a valid 1.0.0 configuration
        return config.has("configVersion") &&
               "1.0.0".equals(config.get("configVersion").getAsString()) &&
               config.has("phases");
    }
    
    @Override
    public void migrate(JsonObject config) throws ConfigMigrationException {
        try {
            // Update version
            config.add("configVersion", new JsonPrimitive("1.1.0"));
            
            // Enable Phase 2
            enablePhase2(config);
            
            // Add Phase 2 configuration
            addPhase2Configuration(config);
            
            // Enhance existing configurations
            enhancePhase1Configuration(config);
            
            // Add new debug options
            enhanceDebugConfiguration(config);
            
            // Add migration history
            addMigrationHistory(config);
            
        } catch (Exception e) {
            throw new ConfigMigrationException("Failed to migrate configuration from 1.0.0 to 1.1.0", e);
        }
    }
    
    @Override
    public boolean validateMigration(JsonObject config) {
        return config.has("configVersion") &&
               "1.1.0".equals(config.get("configVersion").getAsString()) &&
               config.has("phase2Config") &&
               config.getAsJsonObject("phases").getAsJsonObject("phase2").get("enabled").getAsBoolean();
    }
    
    /**
     * Enable Phase 2 in the phases configuration
     */
    private void enablePhase2(JsonObject config) {
        JsonObject phases = config.getAsJsonObject("phases");
        JsonObject phase2 = phases.getAsJsonObject("phase2");
        
        if (phase2 == null) {
            phase2 = new JsonObject();
            phases.add("phase2", phase2);
        }
        
        phase2.add("enabled", new JsonPrimitive(true));
        
        // Add Phase 2 modules
        JsonObject modules = new JsonObject();
        modules.add("ai", new JsonPrimitive(true));
        modules.add("state", new JsonPrimitive(true));
        modules.add("pathfinding", new JsonPrimitive(true));
        phase2.add("modules", modules);
    }
    
    /**
     * Add Phase 2 configuration
     */
    private void addPhase2Configuration(JsonObject config) {
        JsonObject phase2Config = new JsonObject();
        
        // Phase-level settings
        phase2Config.add("enabled", new JsonPrimitive(true));
        phase2Config.add("debugMode", new JsonPrimitive(false));
        
        // AI module configuration
        JsonObject aiConfig = new JsonObject();
        aiConfig.add("enabled", new JsonPrimitive(true));
        aiConfig.add("idleTimeMin", new JsonPrimitive(300)); // 15 seconds
        aiConfig.add("idleTimeMax", new JsonPrimitive(1200)); // 60 seconds
        aiConfig.add("wanderSpeed", new JsonPrimitive(0.1));
        aiConfig.add("enableBehaviorTrees", new JsonPrimitive(true));
        aiConfig.add("aggressionLevel", new JsonPrimitive(0.0)); // Passive
        aiConfig.add("curiosityLevel", new JsonPrimitive(0.3));
        aiConfig.add("memoryDuration", new JsonPrimitive(6000)); // 5 minutes
        phase2Config.add("ai", aiConfig);
        
        // State module configuration
        JsonObject stateConfig = new JsonObject();
        stateConfig.add("enabled", new JsonPrimitive(true));
        stateConfig.add("enableStateTransitions", new JsonPrimitive(true));
        stateConfig.add("stateChangeDelay", new JsonPrimitive(20)); // 1 second
        stateConfig.add("enableStateParticles", new JsonPrimitive(false));
        stateConfig.add("logStateChanges", new JsonPrimitive(false));
        
        // State-specific settings
        JsonObject stateSettings = new JsonObject();
        stateSettings.add("idleDuration", new JsonPrimitive(600)); // 30 seconds
        stateSettings.add("movingDuration", new JsonPrimitive(2400)); // 2 minutes
        stateSettings.add("transitionDuration", new JsonPrimitive(100)); // 5 seconds
        stateSettings.add("damagedDuration", new JsonPrimitive(200)); // 10 seconds
        stateConfig.add("stateSettings", stateSettings);
        
        phase2Config.add("state", stateConfig);
        
        // Pathfinding module configuration
        JsonObject pathfindingConfig = new JsonObject();
        pathfindingConfig.add("enabled", new JsonPrimitive(true));
        pathfindingConfig.add("pathfindingRange", new JsonPrimitive(128.0));
        pathfindingConfig.add("avoidLand", new JsonPrimitive(true));
        pathfindingConfig.add("preferDeepWater", new JsonPrimitive(true));
        pathfindingConfig.add("minWaterDepth", new JsonPrimitive(10));
        pathfindingConfig.add("maxPathLength", new JsonPrimitive(1000));
        pathfindingConfig.add("pathRecalculationInterval", new JsonPrimitive(100)); // 5 seconds
        pathfindingConfig.add("enableSmartPathing", new JsonPrimitive(true));
        pathfindingConfig.add("avoidObstacles", new JsonPrimitive(true));
        phase2Config.add("pathfinding", pathfindingConfig);
        
        config.add("phase2Config", phase2Config);
    }
    
    /**
     * Enhance existing Phase 1 configuration
     */
    private void enhancePhase1Configuration(JsonObject config) {
        if (config.has("phase1Config")) {
            JsonObject phase1Config = config.getAsJsonObject("phase1Config");
            
            // Enhance spawn configuration with new biomes
            if (phase1Config.has("spawn")) {
                JsonObject spawnConfig = phase1Config.getAsJsonObject("spawn");
                
                // Add expanded biome list
                JsonArray allowedBiomes = new JsonArray();
                allowedBiomes.add("minecraft:ocean");
                allowedBiomes.add("minecraft:deep_ocean");
                allowedBiomes.add("minecraft:cold_ocean");
                allowedBiomes.add("minecraft:deep_cold_ocean");
                allowedBiomes.add("minecraft:lukewarm_ocean");
                allowedBiomes.add("minecraft:deep_lukewarm_ocean");
                allowedBiomes.add("minecraft:warm_ocean");
                allowedBiomes.add("minecraft:deep_warm_ocean");
                allowedBiomes.add("minecraft:frozen_ocean");
                allowedBiomes.add("minecraft:deep_frozen_ocean");
                
                spawnConfig.add("allowedBiomes", allowedBiomes);
                
                // Add new spawn options
                spawnConfig.add("respectMobCap", new JsonPrimitive(true));
                spawnConfig.add("spawnOnlyInLoadedChunks", new JsonPrimitive(true));
                spawnConfig.add("minimumDistanceFromPlayer", new JsonPrimitive(64));
                spawnConfig.add("maximumDistanceFromPlayer", new JsonPrimitive(256));
            }
            
            // Enhance client configuration
            if (phase1Config.has("client")) {
                JsonObject clientConfig = phase1Config.getAsJsonObject("client");
                
                // Add performance options
                clientConfig.add("enableDistanceCulling", new JsonPrimitive(true));
                clientConfig.add("enableFrustumCulling", new JsonPrimitive(true));
                clientConfig.add("lodDistance1", new JsonPrimitive(64.0));
                clientConfig.add("lodDistance2", new JsonPrimitive(128.0));
                clientConfig.add("lodDistance3", new JsonPrimitive(256.0));
                
                // Add visual enhancements
                clientConfig.add("enableWaterRipples", new JsonPrimitive(true));
                clientConfig.add("enableBubbleTrail", new JsonPrimitive(true));
                clientConfig.add("enableShellGlow", new JsonPrimitive(false));
            }
        }
    }
    
    /**
     * Enhance debug configuration
     */
    private void enhanceDebugConfiguration(JsonObject config) {
        if (config.has("debug")) {
            JsonObject debug = config.getAsJsonObject("debug");
            
            // Add new debug options
            debug.add("enableAIDebug", new JsonPrimitive(false));
            debug.add("enablePathfindingDebug", new JsonPrimitive(false));
            debug.add("enableStateDebug", new JsonPrimitive(false));
            debug.add("debugRenderDistance", new JsonPrimitive(32.0));
            debug.add("enablePerformanceMetrics", new JsonPrimitive(false));
            debug.add("logPerformanceWarnings", new JsonPrimitive(true));
        }
    }
    
    /**
     * Add migration history tracking
     */
    private void addMigrationHistory(JsonObject config) {
        JsonObject migrationHistory = new JsonObject();
        
        JsonObject migration = new JsonObject();
        migration.add("from", new JsonPrimitive("1.0.0"));
        migration.add("to", new JsonPrimitive("1.1.0"));
        migration.add("description", new JsonPrimitive("Add Phase 2 configuration and enhanced features"));
        migration.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));
        
        migrationHistory.add("Migration_1_0_0_to_1_1_0", migration);
        config.add("migrationHistory", migrationHistory);
    }
    
    @Override
    public int getPriority() {
        return 20; // Normal priority for minor version migration
    }
    
    @Override
    public boolean isReversible() {
        return true; // This migration can be reversed
    }
    
    @Override
    public void reverse(JsonObject config) throws ConfigMigrationException {
        try {
            // Remove Phase 2 configuration
            config.remove("phase2Config");
            
            // Disable Phase 2
            JsonObject phases = config.getAsJsonObject("phases");
            if (phases.has("phase2")) {
                JsonObject phase2 = phases.getAsJsonObject("phase2");
                phase2.add("enabled", new JsonPrimitive(false));
            }
            
            // Remove enhanced debug options
            if (config.has("debug")) {
                JsonObject debug = config.getAsJsonObject("debug");
                debug.remove("enableAIDebug");
                debug.remove("enablePathfindingDebug");
                debug.remove("enableStateDebug");
                debug.remove("debugRenderDistance");
                debug.remove("enablePerformanceMetrics");
                debug.remove("logPerformanceWarnings");
            }
            
            // Remove migration history
            config.remove("migrationHistory");
            
            // Revert version
            config.add("configVersion", new JsonPrimitive("1.0.0"));
            
        } catch (Exception e) {
            throw new ConfigMigrationException("Failed to reverse migration from 1.1.0 to 1.0.0", e);
        }
    }
}