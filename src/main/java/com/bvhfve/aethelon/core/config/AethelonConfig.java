package com.bvhfve.aethelon.core.config;

import com.bvhfve.aethelon.core.AethelonCore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * AethelonConfig - Main configuration class for the Aethelon mod
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: Fabric Loader config directory
 * - Hooks into: Mod initialization lifecycle
 * - Modifies: Configuration files in config directory
 * 
 * CONFIG ROLE:
 * - Purpose: Centralized configuration management with modular phase support
 * - Dependencies: None (core system)
 * - Provides: Phase toggles, module settings, debug options
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: Config format changes may require migration
 * 
 * DESIGN PRINCIPLES:
 * - Simple singleton pattern for easy access
 * - Gson-based JSON serialization with pretty printing
 * - Automatic default creation and validation
 * - Modular phase configuration support
 * - Graceful error handling with fallback to defaults
 */
public class AethelonConfig {
    
    // Singleton instance
    public static AethelonConfig INSTANCE;
    
    private static final String CONFIG_FILE_NAME = "aethelon.json";
    private static final Logger LOGGER = LogManager.getLogger("aethelon-config");
    
    // Core configuration fields
    public final String configVersion = "1.0.0";
    public final boolean enableDebugLogging = false;
    public final boolean enableModuleValidation = true;
    
    // Phase configurations
    public final PhaseConfigs phases = new PhaseConfigs();
    
    // Debug configuration
    public final DebugConfig debug = new DebugConfig();
    
    // Performance configuration
    public final PerformanceConfig performance = new PerformanceConfig();
    
    /**
     * Initialize the configuration system
     * Called during mod initialization
     */
    public static void initialize() {
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableInnerClassSerialization()
            .create();
            
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);
        AethelonConfig config = null;
        
        try {
            // Create config directory if it doesn't exist
            Files.createDirectories(configPath.getParent());
        } catch (IOException e) {
            LOGGER.warn("Failed to create config directory, using default config: {}", e.getMessage());
            config = new AethelonConfig();
        }
        
        if (config == null) {
            if (Files.exists(configPath)) {
                // Load existing configuration
                try (var reader = Files.newBufferedReader(configPath)) {
                    config = gson.fromJson(reader, AethelonConfig.class);
                    LOGGER.info("Loaded Aethelon configuration from {}", configPath);
                } catch (IOException | JsonSyntaxException e) {
                    LOGGER.warn("Failed to read config file, using default config: {}", e.getMessage());
                    config = new AethelonConfig();
                }
            } else {
                // Create default configuration
                config = new AethelonConfig();
                try (var writer = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE_NEW)) {
                    gson.toJson(config, writer);
                    LOGGER.info("Created default Aethelon configuration at {}", configPath);
                } catch (IOException e) {
                    LOGGER.warn("Failed to save default config file: {}", e.getMessage());
                }
            }
        }
        
        INSTANCE = config;
        INSTANCE.onConfigLoaded();
    }
    
    /**
     * Called after configuration is loaded to perform validation and setup
     */
    private void onConfigLoaded() {
        LOGGER.info("Aethelon configuration loaded - Version: {}", configVersion);
        
        // Validate configuration
        if (!validateConfiguration()) {
            LOGGER.warn("Configuration validation failed, some features may not work correctly");
        }
        
        // Apply configuration settings
        applyConfiguration();
        
        // Log configuration summary
        if (debug.logConfigSummary) {
            LOGGER.info("Configuration Summary:\n{}", getConfigSummary());
        }
    }
    
    /**
     * Validate the loaded configuration
     * 
     * @return true if configuration is valid
     */
    private boolean validateConfiguration() {
        boolean valid = true;
        
        // Validate phase configurations
        if (phases == null) {
            LOGGER.error("Phase configuration is null");
            valid = false;
        } else {
            valid &= phases.validate();
        }
        
        // Validate debug configuration
        if (debug == null) {
            LOGGER.error("Debug configuration is null");
            valid = false;
        } else {
            valid &= debug.validate();
        }
        
        // Validate performance configuration
        if (performance == null) {
            LOGGER.error("Performance configuration is null");
            valid = false;
        } else {
            valid &= performance.validate();
        }
        
        return valid;
    }
    
    /**
     * Apply configuration settings to the mod
     */
    private void applyConfiguration() {
        // Apply debug settings
        if (enableDebugLogging) {
            LOGGER.info("Debug logging enabled");
        }
        
        // Apply performance settings
        performance.apply();
        
        // Apply phase-specific settings
        phases.apply();
    }
    
    /**
     * Get a summary of the current configuration
     * 
     * @return Configuration summary string
     */
    public String getConfigSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Aethelon Configuration Summary:\n");
        summary.append("  Version: ").append(configVersion).append("\n");
        summary.append("  Debug Logging: ").append(enableDebugLogging).append("\n");
        summary.append("  Module Validation: ").append(enableModuleValidation).append("\n");
        summary.append("\n");
        
        // Phase summary
        summary.append("Phases:\n");
        summary.append(phases.getSummary());
        summary.append("\n");
        
        // Debug summary
        summary.append("Debug Settings:\n");
        summary.append(debug.getSummary());
        summary.append("\n");
        
        // Performance summary
        summary.append("Performance Settings:\n");
        summary.append(performance.getSummary());
        
        return summary.toString();
    }
    
    /**
     * Phase configuration container
     */
    public static class PhaseConfigs {
        public final Phase1Config phase1 = new Phase1Config();
        public final Phase2Config phase2 = new Phase2Config();
        public final Phase3Config phase3 = new Phase3Config();
        public final Phase4Config phase4 = new Phase4Config();
        public final Phase5Config phase5 = new Phase5Config();
        public final Phase6Config phase6 = new Phase6Config();
        public final Phase7Config phase7 = new Phase7Config();
        public final Phase8Config phase8 = new Phase8Config();
        public final Phase9Config phase9 = new Phase9Config();
        public final Phase10Config phase10 = new Phase10Config();
        
        public boolean validate() {
            return phase1.validate() && 
                   phase2.validate() && 
                   phase3.validate() && 
                   phase4.validate() && 
                   phase5.validate() && 
                   phase6.validate() && 
                   phase7.validate() && 
                   phase8.validate() && 
                   phase9.validate() && 
                   phase10.validate();
        }
        
        public void apply() {
            // Apply phase-specific configurations
            if (phase1.enabled) phase1.apply();
            if (phase2.enabled) phase2.apply();
            if (phase3.enabled) phase3.apply();
            if (phase4.enabled) phase4.apply();
            if (phase5.enabled) phase5.apply();
            if (phase6.enabled) phase6.apply();
            if (phase7.enabled) phase7.apply();
            if (phase8.enabled) phase8.apply();
            if (phase9.enabled) phase9.apply();
            if (phase10.enabled) phase10.apply();
        }
        
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append("  Phase 1 (Basic Entity Foundation): ").append(phase1.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 2 (Entity Behavior & Movement): ").append(phase2.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 3 (Damage Response & Player Interaction): ").append(phase3.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 4 (Island Structure System): ").append(phase4.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 5 (Dynamic Island Movement): ").append(phase5.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 6 (Explosion & Destruction System): ").append(phase6.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 7 (Spawn Control & Population Management): ").append(phase7.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 8 (Configuration & Balancing): ").append(phase8.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 9 (Polish & Optimization): ").append(phase9.enabled ? "ENABLED" : "DISABLED").append("\n");
            summary.append("  Phase 10 (Advanced Features & Integration): ").append(phase10.enabled ? "ENABLED" : "DISABLED").append("\n");
            return summary.toString();
        }
    }
    
    /**
     * Debug configuration
     */
    public static class DebugConfig {
        public final boolean logModuleLoading = true;
        public final boolean logConfigSummary = true;
        public final boolean enableHotReload = false;
        public final boolean showStateParticles = false;
        public final boolean verboseLogging = false;
        public final boolean enableDevCommands = false;
        public final boolean logEntitySpawning = false;
        public final boolean logPhaseTransitions = false;
        
        public boolean validate() {
            // Debug config is always valid (all booleans)
            return true;
        }
        
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append("  Module Loading: ").append(logModuleLoading).append("\n");
            summary.append("  Config Summary: ").append(logConfigSummary).append("\n");
            summary.append("  Hot Reload: ").append(enableHotReload).append("\n");
            summary.append("  State Particles: ").append(showStateParticles).append("\n");
            summary.append("  Verbose Logging: ").append(verboseLogging).append("\n");
            summary.append("  Dev Commands: ").append(enableDevCommands).append("\n");
            summary.append("  Entity Spawning: ").append(logEntitySpawning).append("\n");
            summary.append("  Phase Transitions: ").append(logPhaseTransitions).append("\n");
            return summary.toString();
        }
    }
    
    /**
     * Performance configuration
     */
    public static class PerformanceConfig {
        public final int maxEntitiesPerChunk = 2;
        public final int maxTotalEntities = 50;
        public final int entityTickInterval = 1;
        public final boolean enableLOD = true;
        public final int maxRenderDistance = 256;
        public final boolean enableCulling = true;
        public final int pathfindingCacheSize = 1000;
        public final boolean enableAsyncProcessing = true;
        
        public boolean validate() {
            if (maxEntitiesPerChunk < 1 || maxEntitiesPerChunk > 10) {
                LOGGER.warn("Invalid maxEntitiesPerChunk: {}, should be 1-10", maxEntitiesPerChunk);
                return false;
            }
            if (maxTotalEntities < 1 || maxTotalEntities > 1000) {
                LOGGER.warn("Invalid maxTotalEntities: {}, should be 1-1000", maxTotalEntities);
                return false;
            }
            if (entityTickInterval < 1 || entityTickInterval > 20) {
                LOGGER.warn("Invalid entityTickInterval: {}, should be 1-20", entityTickInterval);
                return false;
            }
            if (maxRenderDistance < 32 || maxRenderDistance > 1024) {
                LOGGER.warn("Invalid maxRenderDistance: {}, should be 32-1024", maxRenderDistance);
                return false;
            }
            if (pathfindingCacheSize < 100 || pathfindingCacheSize > 10000) {
                LOGGER.warn("Invalid pathfindingCacheSize: {}, should be 100-10000", pathfindingCacheSize);
                return false;
            }
            return true;
        }
        
        public void apply() {
            LOGGER.debug("Applied performance configuration: maxEntities={}, renderDistance={}", 
                maxTotalEntities, maxRenderDistance);
        }
        
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            summary.append("  Max Entities Per Chunk: ").append(maxEntitiesPerChunk).append("\n");
            summary.append("  Max Total Entities: ").append(maxTotalEntities).append("\n");
            summary.append("  Entity Tick Interval: ").append(entityTickInterval).append("\n");
            summary.append("  Level of Detail: ").append(enableLOD).append("\n");
            summary.append("  Max Render Distance: ").append(maxRenderDistance).append("\n");
            summary.append("  Enable Culling: ").append(enableCulling).append("\n");
            summary.append("  Pathfinding Cache Size: ").append(pathfindingCacheSize).append("\n");
            summary.append("  Async Processing: ").append(enableAsyncProcessing).append("\n");
            return summary.toString();
        }
    }
    
    /**
     * Base class for phase configurations
     */
    public abstract static class BasePhaseConfig {
        public final boolean enabled;
        public final Map<String, Boolean> modules;
        
        protected BasePhaseConfig(boolean enabled, String... moduleNames) {
            this.enabled = enabled;
            this.modules = new HashMap<>();
            for (String moduleName : moduleNames) {
                this.modules.put(moduleName, enabled); // Default to phase enabled state
            }
        }
        
        public boolean isModuleEnabled(String moduleName) {
            return enabled && modules.getOrDefault(moduleName, false);
        }
        
        public boolean validate() {
            return modules != null;
        }
        
        public void apply() {
            // Override in subclasses for phase-specific application
        }
    }
    
    /**
     * Phase 1: Basic Entity Foundation
     */
    public static class Phase1Config extends BasePhaseConfig {
        // Entity configuration
        public final double entityMaxHealth = 1000.0;
        public final double entityMovementSpeed = 0.1;
        public final double entityFollowRange = 64.0;
        public final float entityWidth = 8.0f;
        public final float entityHeight = 4.0f;
        
        // Spawn configuration
        public final int spawnWeight = 5;
        public final int minGroupSize = 1;
        public final int maxGroupSize = 1;
        public final boolean enableNaturalSpawning = true;
        public final boolean enableSpawnEgg = true;
        
        // Client configuration
        public final boolean enableCustomModel = true;
        public final boolean enableAnimations = true;
        public final boolean enableParticles = true;
        public final float shadowRadius = 4.0f;
        
        public Phase1Config() {
            super(true, "entity", "client", "spawn");
        }
        
        @Override
        public boolean validate() {
            if (!super.validate()) return false;
            if (entityMaxHealth <= 0 || entityMaxHealth > 10000) return false;
            if (entityMovementSpeed < 0 || entityMovementSpeed > 2.0) return false;
            if (entityFollowRange < 0 || entityFollowRange > 256) return false;
            if (entityWidth <= 0 || entityWidth > 32) return false;
            if (entityHeight <= 0 || entityHeight > 32) return false;
            if (spawnWeight < 0 || spawnWeight > 100) return false;
            if (minGroupSize < 1 || minGroupSize > maxGroupSize) return false;
            if (maxGroupSize < 1 || maxGroupSize > 10) return false;
            if (shadowRadius < 0 || shadowRadius > 10) return false;
            return true;
        }
    }
    
    // Placeholder phase configs (to be implemented in future phases)
    public static class Phase2Config extends BasePhaseConfig {
        public Phase2Config() { super(false, "ai", "state", "pathfinding"); }
    }
    
    public static class Phase3Config extends BasePhaseConfig {
        public Phase3Config() { super(false, "damage", "interaction", "triggers"); }
    }
    
    public static class Phase4Config extends BasePhaseConfig {
        public Phase4Config() { super(false, "structure", "attachment", "management"); }
    }
    
    public static class Phase5Config extends BasePhaseConfig {
        public Phase5Config() { super(false, "capture", "placement", "synchronization"); }
    }
    
    public static class Phase6Config extends BasePhaseConfig {
        public Phase6Config() { super(false, "explosion", "destruction", "effects"); }
    }
    
    public static class Phase7Config extends BasePhaseConfig {
        public Phase7Config() { super(false, "spawning", "population", "migration"); }
    }
    
    public static class Phase8Config extends BasePhaseConfig {
        public Phase8Config() { super(false, "config", "balancing", "presets"); }
    }
    
    public static class Phase9Config extends BasePhaseConfig {
        public Phase9Config() { super(false, "performance", "polish", "debugging"); }
    }
    
    public static class Phase10Config extends BasePhaseConfig {
        public Phase10Config() { super(false, "advanced", "integration", "ecosystem", "api"); }
    }
}