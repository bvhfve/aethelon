package com.bvhfve.aethelon.core.config;

import com.bvhfve.aethelon.core.AethelonCore;

/**
 * ConfigAdapter - Adapter to bridge new AethelonConfig with existing ConfigManager interface
 * 
 * This class provides backward compatibility while we transition to the new config system.
 * It implements the same interface as the old ConfigManager but delegates to AethelonConfig.
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None directly (adapter pattern)
 * - Hooks into: Existing module system
 * - Modifies: None (compatibility layer)
 * 
 * ADAPTER ROLE:
 * - Purpose: Maintain compatibility during config system upgrade
 * - Dependencies: AethelonConfig
 * - Provides: ConfigManager-compatible interface
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: None (maintains existing interface)
 */
public class ConfigAdapter {
    
    /**
     * Check if a specific phase is enabled
     * 
     * @param phaseName Name of the phase (e.g., "phase1", "phase2")
     * @return true if phase is enabled, false otherwise
     */
    public boolean isPhaseEnabled(String phaseName) {
        if (AethelonConfig.INSTANCE == null) {
            AethelonCore.LOGGER.warn("AethelonConfig not initialized, defaulting to false for phase: {}", phaseName);
            return false;
        }
        
        switch (phaseName) {
            case "phase1": return AethelonConfig.INSTANCE.phases.phase1.enabled;
            case "phase2": return AethelonConfig.INSTANCE.phases.phase2.enabled;
            case "phase3": return AethelonConfig.INSTANCE.phases.phase3.enabled;
            case "phase4": return AethelonConfig.INSTANCE.phases.phase4.enabled;
            case "phase5": return AethelonConfig.INSTANCE.phases.phase5.enabled;
            case "phase6": return AethelonConfig.INSTANCE.phases.phase6.enabled;
            case "phase7": return AethelonConfig.INSTANCE.phases.phase7.enabled;
            case "phase8": return AethelonConfig.INSTANCE.phases.phase8.enabled;
            case "phase9": return AethelonConfig.INSTANCE.phases.phase9.enabled;
            case "phase10": return AethelonConfig.INSTANCE.phases.phase10.enabled;
            default:
                AethelonCore.LOGGER.warn("Unknown phase: {}", phaseName);
                return false;
        }
    }
    
    /**
     * Check if a specific module within a phase is enabled
     * 
     * @param phaseName Name of the phase
     * @param moduleName Name of the module within the phase
     * @return true if both phase and module are enabled, false otherwise
     */
    public boolean isModuleEnabled(String phaseName, String moduleName) {
        if (AethelonConfig.INSTANCE == null) {
            AethelonCore.LOGGER.warn("AethelonConfig not initialized, defaulting to false for module: {}/{}", phaseName, moduleName);
            return false;
        }
        
        AethelonConfig.BasePhaseConfig phaseConfig = getPhaseConfig(phaseName);
        if (phaseConfig == null) {
            return false;
        }
        
        return phaseConfig.isModuleEnabled(moduleName);
    }
    
    /**
     * Get debug configuration
     * 
     * @return Debug configuration object
     */
    public DebugConfigAdapter getDebugConfig() {
        return new DebugConfigAdapter();
    }
    
    /**
     * Get phase-specific configuration
     * 
     * @param phaseName Name of the phase
     * @param configClass Class of the configuration
     * @return Phase configuration object or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getPhaseConfig(String phaseName, Class<T> configClass) {
        if (AethelonConfig.INSTANCE == null) {
            return null;
        }
        
        AethelonConfig.BasePhaseConfig phaseConfig = getPhaseConfig(phaseName);
        if (phaseConfig != null && configClass.isInstance(phaseConfig)) {
            return (T) phaseConfig;
        }
        
        // Special handling for Phase1Config
        if ("phase1".equals(phaseName) && configClass.getName().contains("Phase1Config")) {
            return (T) AethelonConfig.INSTANCE.phases.phase1;
        }
        
        return null;
    }
    
    /**
     * Get phase configuration by name
     * 
     * @param phaseName Name of the phase
     * @return Phase configuration or null if not found
     */
    private AethelonConfig.BasePhaseConfig getPhaseConfig(String phaseName) {
        if (AethelonConfig.INSTANCE == null) {
            return null;
        }
        
        switch (phaseName) {
            case "phase1": return AethelonConfig.INSTANCE.phases.phase1;
            case "phase2": return AethelonConfig.INSTANCE.phases.phase2;
            case "phase3": return AethelonConfig.INSTANCE.phases.phase3;
            case "phase4": return AethelonConfig.INSTANCE.phases.phase4;
            case "phase5": return AethelonConfig.INSTANCE.phases.phase5;
            case "phase6": return AethelonConfig.INSTANCE.phases.phase6;
            case "phase7": return AethelonConfig.INSTANCE.phases.phase7;
            case "phase8": return AethelonConfig.INSTANCE.phases.phase8;
            case "phase9": return AethelonConfig.INSTANCE.phases.phase9;
            case "phase10": return AethelonConfig.INSTANCE.phases.phase10;
            default: return null;
        }
    }
    
    /**
     * Debug configuration adapter
     */
    public static class DebugConfigAdapter {
        public boolean logModuleLoading() {
            return AethelonConfig.INSTANCE != null && AethelonConfig.INSTANCE.debug.logModuleLoading;
        }
        
        public boolean enableHotReload() {
            return AethelonConfig.INSTANCE != null && AethelonConfig.INSTANCE.debug.enableHotReload;
        }
        
        public boolean showStateParticles() {
            return AethelonConfig.INSTANCE != null && AethelonConfig.INSTANCE.debug.showStateParticles;
        }
        
        public boolean verboseLogging() {
            return AethelonConfig.INSTANCE != null && AethelonConfig.INSTANCE.debug.verboseLogging;
        }
        
        public boolean enableDevCommands() {
            return AethelonConfig.INSTANCE != null && AethelonConfig.INSTANCE.debug.enableDevCommands;
        }
        
        public boolean logEntitySpawning() {
            return AethelonConfig.INSTANCE != null && AethelonConfig.INSTANCE.debug.logEntitySpawning;
        }
        
        public boolean logPhaseTransitions() {
            return AethelonConfig.INSTANCE != null && AethelonConfig.INSTANCE.debug.logPhaseTransitions;
        }
        
        // Legacy property access for backward compatibility
        public final boolean logModuleLoading = logModuleLoading();
        public final boolean enableHotReload = enableHotReload();
        public final boolean showStateParticles = showStateParticles();
        public final boolean verboseLogging = verboseLogging();
        public final boolean enableDevCommands = enableDevCommands();
    }
}