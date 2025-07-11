package com.bvhfve.aethelon.core.config;

import com.bvhfve.aethelon.core.AethelonCore;

/**
 * ConfigManager - Backward-compatible ConfigManager implementation
 * 
 * This class maintains the existing ConfigManager interface while delegating
 * to the new AethelonConfig system. This allows existing modules to work
 * without modification during the config system upgrade.
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None directly (compatibility layer)
 * - Hooks into: Existing module system
 * - Modifies: None (delegates to new config system)
 * 
 * COMPATIBILITY ROLE:
 * - Purpose: Maintain existing ConfigManager interface
 * - Dependencies: AethelonConfig, ConfigAdapter
 * - Provides: Legacy ConfigManager methods
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: None (maintains compatibility)
 */
public class ConfigManager {
    
    private final ConfigAdapter adapter;
    
    public ConfigManager() {
        this.adapter = new ConfigAdapter();
    }
    
    /**
     * Load configuration (no-op in new system, handled by AethelonConfig.initialize())
     */
    public void loadConfiguration() {
        // Configuration is loaded by AethelonConfig.initialize()
        // This method is kept for compatibility
        AethelonCore.LOGGER.debug("ConfigManager.loadConfiguration() called - delegating to AethelonConfig");
    }
    
    /**
     * Check if a specific phase is enabled
     * 
     * @param phaseName Name of the phase (e.g., "phase1", "phase2")
     * @return true if phase is enabled, false otherwise
     */
    public boolean isPhaseEnabled(String phaseName) {
        return adapter.isPhaseEnabled(phaseName);
    }
    
    /**
     * Check if a specific module within a phase is enabled
     * 
     * @param phaseName Name of the phase
     * @param moduleName Name of the module within the phase
     * @return true if both phase and module are enabled, false otherwise
     */
    public boolean isModuleEnabled(String phaseName, String moduleName) {
        return adapter.isModuleEnabled(phaseName, moduleName);
    }
    
    /**
     * Get debug configuration
     * 
     * @return Debug configuration object
     */
    public ConfigAdapter.DebugConfigAdapter getDebugConfig() {
        return adapter.getDebugConfig();
    }
    
    /**
     * Get phase-specific configuration
     * 
     * @param phaseName Name of the phase
     * @param configClass Class of the configuration
     * @return Phase configuration object or null if not found
     */
    public <T> T getPhaseConfig(String phaseName, Class<T> configClass) {
        return adapter.getPhaseConfig(phaseName, configClass);
    }
    
    /**
     * Register a phase configuration definition (no-op in new system)
     * 
     * @param phaseName Name of the phase
     * @param definition Configuration definition
     */
    public void registerPhaseConfig(String phaseName, Object definition) {
        // Phase configs are now defined statically in AethelonConfig
        // This method is kept for compatibility but does nothing
        AethelonCore.LOGGER.debug("ConfigManager.registerPhaseConfig() called for {} - no-op in new system", phaseName);
    }
    
    /**
     * Enable or disable a specific phase (not supported in new system)
     * 
     * @param phaseName Name of the phase
     * @param enabled Whether the phase should be enabled
     */
    public void setPhaseEnabled(String phaseName, boolean enabled) {
        AethelonCore.LOGGER.warn("ConfigManager.setPhaseEnabled() called - runtime phase toggling not supported in new config system");
        AethelonCore.LOGGER.warn("Please modify the configuration file and restart to change phase settings");
    }
    
    /**
     * Enable or disable a specific module within a phase (not supported in new system)
     * 
     * @param phaseName Name of the phase
     * @param moduleName Name of the module
     * @param enabled Whether the module should be enabled
     */
    public void setModuleEnabled(String phaseName, String moduleName, boolean enabled) {
        AethelonCore.LOGGER.warn("ConfigManager.setModuleEnabled() called - runtime module toggling not supported in new config system");
        AethelonCore.LOGGER.warn("Please modify the configuration file and restart to change module settings");
    }
    
    /**
     * Get configuration summary
     * 
     * @return Configuration summary string
     */
    public String getConfigSummary() {
        if (AethelonConfig.INSTANCE != null) {
            return AethelonConfig.INSTANCE.getConfigSummary();
        } else {
            return "AethelonConfig not initialized";
        }
    }
    
    /**
     * Get all available phase names
     * 
     * @return Set of phase names
     */
    public java.util.Set<String> getAvailablePhases() {
        return java.util.Set.of("phase1", "phase2", "phase3", "phase4", "phase5", 
                               "phase6", "phase7", "phase8", "phase9", "phase10");
    }
}