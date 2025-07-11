package com.bvhfve.aethelon.phase1;

/**
 * Phase1Config - Configuration data for Phase 1: Basic Entity Foundation
 * 
 * This class defines all configurable parameters for Phase 1 modules.
 * All fields should have sensible defaults and be documented with their purpose
 * and valid value ranges.
 * 
 * VERSION COMPATIBILITY:
 * - Backward compatible with previous config versions
 * - New fields should have sensible defaults
 * - Deprecated fields should be marked for removal
 */
public class Phase1Config {
    
    // Phase-level configuration
    public boolean enabled = true;
    public boolean debugMode = false;
    
    // Sub-module configurations
    public EntityConfig entity = new EntityConfig();
    public ClientConfig client = new ClientConfig();
    public SpawnConfig spawn = new SpawnConfig();
    
    /**
     * Configuration for entity module
     */
    public static class EntityConfig {
        public boolean enabled = true;
        
        // Entity attributes
        public double maxHealth = 1000.0;
        public double movementSpeed = 0.1;
        public double followRange = 64.0;
        public double knockbackResistance = 1.0;
        
        // Entity behavior
        public boolean canBePushed = false;
        public boolean canBreatheUnderwater = true;
        public boolean immuneToFire = false;
        public boolean immuneToExplosions = false;
        
        // Entity size
        public float entityWidth = 4.0f;
        public float entityHeight = 2.0f;
        public float eyeHeight = 1.8f;
    }
    
    /**
     * Configuration for client module
     */
    public static class ClientConfig {
        public boolean enabled = true;
        
        // Rendering options
        public boolean enableCustomModel = true;
        public boolean enableAnimations = true;
        public boolean enableParticles = true;
        public float renderDistance = 128.0f;
        
        // Visual effects
        public boolean showHealthBar = true;
        public boolean showNameTag = true;
        public float nameTagDistance = 32.0f;
        
        // Performance options
        public boolean enableLOD = true; // Level of Detail
        public int maxRenderDistance = 256;
        public boolean enableCulling = true;
    }
    
    /**
     * Configuration for spawn module
     */
    public static class SpawnConfig {
        public boolean enabled = true;
        
        // Spawn rates and limits
        public int spawnWeight = 5;
        public int minGroupSize = 1;
        public int maxGroupSize = 1;
        public int maxWorldPopulation = 10;
        public int maxChunkPopulation = 1;
        
        // Spawn conditions
        public String[] allowedBiomes = {
            "minecraft:ocean",
            "minecraft:deep_ocean",
            "minecraft:cold_ocean",
            "minecraft:deep_cold_ocean",
            "minecraft:lukewarm_ocean",
            "minecraft:deep_lukewarm_ocean",
            "minecraft:warm_ocean",
            "minecraft:deep_warm_ocean"
        };
        
        public int minWaterDepth = 10;
        public int maxWaterDepth = 256;
        public boolean requiresDeepOcean = true;
        public boolean avoidShallowWater = true;
        
        // Spawn egg
        public boolean enableSpawnEgg = true;
        public String spawnEggName = "Aethelon Spawn Egg";
    }
    
    /**
     * Validate all configuration values
     * 
     * @return true if configuration is valid, false otherwise
     */
    public boolean validate() {
        try {
            // Validate phase-level config
            if (!validatePhaseConfig()) {
                return false;
            }
            
            // Validate sub-module configs
            if (entity != null && !validateEntityConfig()) {
                return false;
            }
            
            if (client != null && !validateClientConfig()) {
                return false;
            }
            
            if (spawn != null && !validateSpawnConfig()) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate phase-level configuration
     */
    private boolean validatePhaseConfig() {
        // Phase-level validation is minimal
        return true;
    }
    
    /**
     * Validate entity module configuration
     */
    private boolean validateEntityConfig() {
        if (entity.maxHealth <= 0 || entity.maxHealth > 10000) return false;
        if (entity.movementSpeed < 0 || entity.movementSpeed > 2.0) return false;
        if (entity.followRange < 0 || entity.followRange > 256) return false;
        if (entity.knockbackResistance < 0 || entity.knockbackResistance > 1.0) return false;
        if (entity.entityWidth <= 0 || entity.entityWidth > 32) return false;
        if (entity.entityHeight <= 0 || entity.entityHeight > 32) return false;
        if (entity.eyeHeight < 0 || entity.eyeHeight > entity.entityHeight) return false;
        
        return true;
    }
    
    /**
     * Validate client module configuration
     */
    private boolean validateClientConfig() {
        if (client.renderDistance < 0 || client.renderDistance > 512) return false;
        if (client.nameTagDistance < 0 || client.nameTagDistance > 128) return false;
        if (client.maxRenderDistance < 0 || client.maxRenderDistance > 1024) return false;
        
        return true;
    }
    
    /**
     * Validate spawn module configuration
     */
    private boolean validateSpawnConfig() {
        if (spawn.spawnWeight < 0 || spawn.spawnWeight > 100) return false;
        if (spawn.minGroupSize < 1 || spawn.minGroupSize > spawn.maxGroupSize) return false;
        if (spawn.maxGroupSize < 1 || spawn.maxGroupSize > 10) return false;
        if (spawn.maxWorldPopulation < 0 || spawn.maxWorldPopulation > 1000) return false;
        if (spawn.maxChunkPopulation < 0 || spawn.maxChunkPopulation > 10) return false;
        if (spawn.minWaterDepth < 0 || spawn.minWaterDepth > spawn.maxWaterDepth) return false;
        if (spawn.maxWaterDepth < 1 || spawn.maxWaterDepth > 320) return false;
        if (spawn.allowedBiomes == null || spawn.allowedBiomes.length == 0) return false;
        
        return true;
    }
    
    /**
     * Apply default values for any missing configuration
     */
    public void applyDefaults() {
        // Apply sub-module defaults
        if (entity == null) {
            entity = new EntityConfig();
        }
        
        if (client == null) {
            client = new ClientConfig();
        }
        
        if (spawn == null) {
            spawn = new SpawnConfig();
        }
        
        // Apply specific defaults
        if (spawn.allowedBiomes == null || spawn.allowedBiomes.length == 0) {
            spawn.allowedBiomes = new String[]{
                "minecraft:ocean",
                "minecraft:deep_ocean"
            };
        }
    }
    
    /**
     * Get a summary of current configuration for logging
     */
    public String getConfigSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Phase 1 Config: ");
        summary.append("enabled=").append(enabled);
        summary.append(", debugMode=").append(debugMode);
        summary.append(", entity.enabled=").append(entity.enabled);
        summary.append(", client.enabled=").append(client.enabled);
        summary.append(", spawn.enabled=").append(spawn.enabled);
        
        if (entity.enabled) {
            summary.append(", health=").append(entity.maxHealth);
            summary.append(", speed=").append(entity.movementSpeed);
        }
        
        if (spawn.enabled) {
            summary.append(", spawnWeight=").append(spawn.spawnWeight);
            summary.append(", maxPop=").append(spawn.maxWorldPopulation);
        }
        
        return summary.toString();
    }
}