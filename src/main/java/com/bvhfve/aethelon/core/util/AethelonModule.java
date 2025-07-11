package com.bvhfve.aethelon.core.util;

import java.util.List;

/**
 * AethelonModule - Interface for all mod modules to implement
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None (pure interface)
 * - Hooks into: Module loading lifecycle
 * - Modifies: None (contract definition)
 * 
 * MODULE ROLE:
 * - Purpose: Define contract for all modules in the mod
 * - Dependencies: None (interface)
 * - Provides: Lifecycle methods, metadata access, dependency declaration
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no Minecraft dependencies)
 * - Fabric API: Any (no Fabric dependencies)
 * - Breaking changes: Interface changes require all modules to be updated
 */
public interface AethelonModule {
    
    /**
     * Get the unique name of this module
     * Used for identification, logging, and dependency resolution
     * 
     * @return Module name (e.g., "phase1.entity", "phase2.ai")
     */
    String getModuleName();
    
    /**
     * Get the phase this module belongs to
     * Used for phase-level enable/disable functionality
     * 
     * @return Phase name (e.g., "phase1", "phase2")
     */
    String getPhase();
    
    /**
     * Check if this module is currently enabled
     * Should check both phase and module-level configuration
     * 
     * @return true if module is enabled and should be active
     */
    boolean isEnabled();
    
    /**
     * Initialize the module
     * Called during mod loading after all dependencies are satisfied
     * Should perform all registration and setup tasks
     * 
     * @throws Exception if initialization fails
     * 
     * MINECRAFT CONTEXT:
     * - Called by: ModuleLoader during mod initialization
     * - Timing: After dependencies loaded, before world generation
     * - Thread safety: Single-threaded during mod loading
     */
    void initialize() throws Exception;
    
    /**
     * Shutdown the module
     * Called during mod shutdown or hot reload
     * Should clean up resources and unregister content if possible
     * 
     * @throws Exception if shutdown fails
     * 
     * MINECRAFT CONTEXT:
     * - Called by: ModuleLoader during shutdown or hot reload
     * - Timing: During mod shutdown or configuration changes
     * - Thread safety: Single-threaded during shutdown
     */
    void shutdown() throws Exception;
    
    /**
     * Get list of module names this module depends on
     * Used for dependency resolution during loading
     * 
     * @return List of module names (empty list if no dependencies)
     */
    List<String> getDependencies();
    
    /**
     * Check if this module is compatible with a specific Minecraft version
     * Used for version compatibility checking
     * 
     * @param minecraftVersion Minecraft version string (e.g., "1.21.4")
     * @return true if compatible with the specified version
     */
    boolean isCompatibleWith(String minecraftVersion);
    
    /**
     * Get the minimum Fabric API version required by this module
     * Used for dependency checking
     * 
     * @return Fabric API version string (e.g., "0.119.2")
     */
    default String getRequiredFabricApiVersion() {
        return "0.119.2";
    }
    
    /**
     * Get a description of what this module provides
     * Used for documentation and debugging
     * 
     * @return Human-readable description of module functionality
     */
    default String getDescription() {
        return "No description provided";
    }
    
    /**
     * Check if this module supports hot reloading
     * Hot reload allows enabling/disabling modules without restarting Minecraft
     * 
     * @return true if module supports hot reload
     */
    default boolean supportsHotReload() {
        return false;
    }
    
    /**
     * Get the priority of this module for loading order
     * Lower numbers load first, higher numbers load later
     * Used when dependency resolution alone isn't sufficient
     * 
     * @return Priority value (default: 100)
     */
    default int getLoadPriority() {
        return 100;
    }
}