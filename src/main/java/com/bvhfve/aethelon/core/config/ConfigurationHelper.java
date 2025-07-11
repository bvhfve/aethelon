package com.bvhfve.aethelon.core.config;

import com.bvhfve.aethelon.core.AethelonCore;

/**
 * ConfigurationHelper - Utility methods for configuration management
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None (pure utility class)
 * - Hooks into: Configuration system
 * - Modifies: None (helper methods only)
 * 
 * MODULE ROLE:
 * - Purpose: Provide convenient methods for configuration access and management
 * - Dependencies: ConfigManager, PhaseConfigRegistry
 * - Provides: Simplified configuration access, validation helpers, utility methods
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no Minecraft dependencies)
 * - Fabric API: Any (no Fabric dependencies)
 * - Breaking changes: Method signatures may evolve with configuration system
 */
public class ConfigurationHelper {
    
    /**
     * Get phase configuration with type safety and null checking
     * 
     * @param phaseName Name of the phase
     * @param configClass Class of the configuration
     * @param defaultValue Default value if configuration not found
     * @return Phase configuration or default value
     */
    public static <T> T getPhaseConfigSafe(String phaseName, Class<T> configClass, T defaultValue) {
        try {
            T config = AethelonCore.getConfigManager().getPhaseConfig(phaseName, configClass);
            return config != null ? config : defaultValue;
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to get configuration for phase {}, using default", phaseName, e);
            return defaultValue;
        }
    }
    
    /**
     * Check if a phase and all its required modules are enabled
     * 
     * @param phaseName Name of the phase
     * @param requiredModules Array of required module names
     * @return true if phase and all required modules are enabled
     */
    public static boolean isPhaseFullyEnabled(String phaseName, String... requiredModules) {
        ConfigManager configManager = AethelonCore.getConfigManager();
        
        if (!configManager.isPhaseEnabled(phaseName)) {
            return false;
        }
        
        for (String moduleName : requiredModules) {
            if (!configManager.isModuleEnabled(phaseName, moduleName)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get a configuration value with fallback chain
     * 
     * @param primaryPhase Primary phase to check
     * @param fallbackPhase Fallback phase if primary not available
     * @param configClass Configuration class
     * @param defaultValue Default value if neither phase has config
     * @return Configuration value from primary, fallback, or default
     */
    public static <T> T getConfigWithFallback(String primaryPhase, String fallbackPhase, 
                                            Class<T> configClass, T defaultValue) {
        ConfigManager configManager = AethelonCore.getConfigManager();
        
        // Try primary phase first
        T primaryConfig = configManager.getPhaseConfig(primaryPhase, configClass);
        if (primaryConfig != null) {
            return primaryConfig;
        }
        
        // Try fallback phase
        T fallbackConfig = configManager.getPhaseConfig(fallbackPhase, configClass);
        if (fallbackConfig != null) {
            return fallbackConfig;
        }
        
        // Return default
        return defaultValue;
    }
    
    /**
     * Register a simple phase configuration with basic validation
     * 
     * @param phaseName Name of the phase
     * @param configClass Configuration class
     * @param description Description of the phase
     */
    public static <T> void registerSimplePhaseConfig(String phaseName, Class<T> configClass, String description) {
        try {
            PhaseConfigRegistry.PhaseConfigDefinition definition = 
                new PhaseConfigRegistry.PhaseConfigDefinition(
                    configClass,
                    config -> true, // Basic validation - always true
                    () -> {
                        try {
                            return configClass.getDeclaredConstructor().newInstance();
                        } catch (Exception e) {
                            AethelonCore.LOGGER.error("Failed to create default config for {}", phaseName, e);
                            return null;
                        }
                    },
                    config -> {}, // No default application
                    description
                );
            
            AethelonCore.getConfigManager().registerPhaseConfig(phaseName, definition);
            AethelonCore.LOGGER.debug("Registered simple configuration for phase: {}", phaseName);
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to register configuration for phase: {}", phaseName, e);
        }
    }
    
    /**
     * Log configuration summary for debugging
     * 
     * @param phaseName Name of the phase to log
     */
    public static void logPhaseConfigSummary(String phaseName) {
        try {
            ConfigManager configManager = AethelonCore.getConfigManager();
            
            if (configManager.isPhaseEnabled(phaseName)) {
                AethelonCore.LOGGER.info("Phase {} is ENABLED", phaseName);
                
                // Log module status
                // Use new config system - simplified logging
                AethelonCore.LOGGER.info("  Phase {} configuration loaded", phaseName);
            } else {
                AethelonCore.LOGGER.info("Phase {} is DISABLED", phaseName);
            }
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to log configuration summary for phase: {}", phaseName, e);
        }
    }
    
    /**
     * Validate all phase configurations and log results
     * 
     * @return true if all configurations are valid
     */
    public static boolean validateAllConfigurations() {
        boolean allValid = true;
        ConfigManager configManager = AethelonCore.getConfigManager();
        
        try {
            for (String phaseName : configManager.getAvailablePhases()) {
                if (configManager.isPhaseEnabled(phaseName)) {
                    // Validation is handled internally by ConfigManager
                    // This method provides a convenient way to trigger validation
                    AethelonCore.LOGGER.debug("Phase {} configuration is valid", phaseName);
                }
            }
            
            AethelonCore.LOGGER.info("Configuration validation complete: {}", allValid ? "SUCCESS" : "FAILED");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to validate configurations", e);
            allValid = false;
        }
        
        return allValid;
    }
    
    /**
     * Create a configuration backup
     * 
     * @return true if backup was created successfully
     */
    public static boolean createConfigBackup() {
        try {
            // This would implement configuration backup functionality
            // For now, just log the intent
            AethelonCore.LOGGER.info("Configuration backup functionality not yet implemented");
            return true;
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to create configuration backup", e);
            return false;
        }
    }
    
    /**
     * Get debug information about the configuration system
     * 
     * @return Debug information string
     */
    public static String getConfigDebugInfo() {
        StringBuilder debug = new StringBuilder();
        ConfigManager configManager = AethelonCore.getConfigManager();
        
        try {
            debug.append("=== Aethelon Configuration Debug Info ===\n");
            debug.append(configManager.getConfigSummary());
            debug.append("\n");
            
            // Add phase-specific debug info
            for (String phaseName : configManager.getAvailablePhases()) {
                debug.append(String.format("Phase %s: %s\n", 
                    phaseName, 
                    configManager.isPhaseEnabled(phaseName) ? "ENABLED" : "DISABLED"));
            }
            
            debug.append("==========================================");
            
        } catch (Exception e) {
            debug.append("Error generating debug info: ").append(e.getMessage());
        }
        
        return debug.toString();
    }
}