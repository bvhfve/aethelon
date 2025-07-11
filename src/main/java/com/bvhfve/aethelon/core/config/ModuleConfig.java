package com.bvhfve.aethelon.core.config;

import java.util.HashMap;
import java.util.Map;

/**
 * ModuleConfig - Configuration data structure for phase and module toggles
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None (pure data structure)
 * - Hooks into: None (serialized to/from JSON)
 * - Modifies: None (configuration data only)
 * 
 * MODULE ROLE:
 * - Purpose: Define configuration structure for all phases and modules
 * - Dependencies: None (data structure)
 * - Provides: Type-safe configuration access, JSON serialization structure
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no Minecraft dependencies)
 * - Fabric API: Any (no Fabric dependencies)
 * - Breaking changes: Config structure may evolve, migration needed
 */
public class ModuleConfig {
    
    /**
     * Map of phase name to phase configuration
     * Key: phase name (e.g., "phase1", "phase2")
     * Value: PhaseConfig containing enabled status and module toggles
     */
    public Map<String, PhaseConfig> phases = new HashMap<>();
    
    /**
     * Debug and development configuration
     */
    public DebugConfig debug = new DebugConfig();
    
    /**
     * Configuration for a specific phase
     */
    public static class PhaseConfig {
        /**
         * Whether this entire phase is enabled
         * If false, all modules in this phase are disabled regardless of module settings
         */
        public boolean enabled;
        
        /**
         * Map of module name to enabled status within this phase
         * Key: module name (e.g., "entity", "client", "ai")
         * Value: whether this specific module is enabled
         */
        public Map<String, Boolean> modules = new HashMap<>();
        
        public PhaseConfig() {
            this.enabled = false;
        }
        
        public PhaseConfig(boolean enabled, Map<String, Boolean> modules) {
            this.enabled = enabled;
            this.modules = modules != null ? modules : new HashMap<>();
        }
    }
    
    /**
     * Debug and development configuration options
     */
    public static class DebugConfig {
        /**
         * Whether to log detailed module loading information
         * Useful for debugging module loading issues
         */
        public boolean logModuleLoading = false;
        
        /**
         * Whether to enable hot reloading of modules (experimental)
         * Allows enabling/disabling modules without restarting Minecraft
         */
        public boolean enableHotReload = false;
        
        /**
         * Whether to show debug particles for entity states
         * Useful for debugging AI and behavior systems
         */
        public boolean showStateParticles = false;
        
        /**
         * Whether to enable verbose logging for all modules
         * Can impact performance, use only for debugging
         */
        public boolean verboseLogging = false;
        
        /**
         * Whether to enable development commands
         * Provides in-game commands for testing and debugging
         */
        public boolean enableDevCommands = false;
    }
}