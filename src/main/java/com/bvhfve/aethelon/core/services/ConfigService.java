package com.bvhfve.aethelon.core.services;

import java.util.Set;

/**
 * ConfigService - Service interface for configuration management
 * 
 * This interface replaces static access to ConfigManager and AethelonConfig.INSTANCE
 * with a proper service that can be injected and tested.
 * 
 * REPLACES:
 * - AethelonCore.getConfigManager()
 * - AethelonConfig.INSTANCE direct access
 * - Static configuration access patterns
 * 
 * BENEFITS:
 * - Dependency injection support
 * - Easy mocking for testing
 * - Clear service contract
 * - Configuration change notifications (future)
 */
public interface ConfigService {
    
    /**
     * Check if a specific phase is enabled
     * 
     * @param phaseName Name of the phase (e.g., "phase1", "phase2")
     * @return true if phase is enabled, false otherwise
     */
    boolean isPhaseEnabled(String phaseName);
    
    /**
     * Check if a specific module within a phase is enabled
     * 
     * @param phaseName Name of the phase
     * @param moduleName Name of the module within the phase
     * @return true if both phase and module are enabled, false otherwise
     */
    boolean isModuleEnabled(String phaseName, String moduleName);
    
    /**
     * Get phase-specific configuration
     * 
     * @param phaseName Name of the phase
     * @param configClass Class of the configuration
     * @return Phase configuration object or null if not found
     */
    <T> T getPhaseConfig(String phaseName, Class<T> configClass);
    
    /**
     * Get debug configuration
     * 
     * @return Debug configuration object
     */
    DebugConfig getDebugConfig();
    
    /**
     * Get configuration summary for logging
     * 
     * @return Configuration summary string
     */
    String getConfigSummary();
    
    /**
     * Get all available phase names
     * 
     * @return Set of phase names
     */
    Set<String> getAvailablePhases();
    
    /**
     * Check if configuration is loaded and valid
     * 
     * @return true if configuration is ready for use
     */
    boolean isConfigurationReady();
    
    /**
     * Get configuration value with fallback
     * 
     * @param path Configuration path (e.g., "phases.phase1.enableNaturalSpawning")
     * @param defaultValue Default value if path not found
     * @param valueClass Class of the expected value
     * @return Configuration value or default
     */
    <T> T getConfigValue(String path, T defaultValue, Class<T> valueClass);
    
    /**
     * Debug configuration interface
     */
    interface DebugConfig {
        boolean isModuleLoadingEnabled();
        boolean isConfigSummaryEnabled();
        boolean isHotReloadEnabled();
        boolean isStateParticlesEnabled();
        boolean isVerboseLoggingEnabled();
        boolean isDevCommandsEnabled();
        boolean isEntitySpawningEnabled();
        boolean isPhaseTransitionsEnabled();
    }
}