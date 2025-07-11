package com.bvhfve.aethelon.phase1;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.util.AethelonModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase1Module - Main coordinator for Phase 1: Basic Entity Foundation
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None directly (coordination layer)
 * - Hooks into: Module loading system
 * - Modifies: None (coordinates sub-modules)
 * 
 * MODULE ROLE:
 * - Purpose: Coordinate Phase 1 sub-modules (entity, client, spawn)
 * - Dependencies: None (base phase)
 * - Provides: Phase 1 lifecycle management, sub-module coordination
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: Sub-module interface changes may require updates
 * 
 * PHASE 1 OVERVIEW:
 * This phase implements the basic foundation for the Aethelon world turtle:
 * - Entity registration and attributes (1000 HP, slow movement, large size)
 * - Client-side rendering (custom model, animations, textures)
 * - Spawn system (deep ocean biomes, spawn egg for testing)
 * 
 * All Phase 1 functionality can be disabled via configuration while maintaining
 * a stable core system for other phases to build upon.
 */
public class Phase1Module implements AethelonModule {
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase1";
    }
    
    @Override
    public String getPhase() {
        return "phase1";
    }
    
    @Override
    public boolean isEnabled() {
        return AethelonCore.getConfigManager().isPhaseEnabled("phase1");
    }
    
    @Override
    public void initialize() throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase 1 is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase 1: Basic Entity Foundation");
        
        try {
            // Register Phase 1 configuration
            registerPhaseConfiguration();
            
            // Phase 1 initialization is handled by sub-modules:
            // - phase1.entity: Entity registration and attributes
            // - phase1.client: Client-side rendering and models  
            // - phase1.spawn: Biome spawning and spawn eggs
            
            // This module serves as a coordinator and dependency anchor
            // Sub-modules depend on this module being loaded first
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase 1 initialization complete");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase 1", e);
            throw e;
        }
    }
    
    /**
     * Register Phase 1 configuration with the configuration system
     */
    private void registerPhaseConfiguration() {
        AethelonCore.getConfigManager().registerPhaseConfig("phase1", 
            new com.bvhfve.aethelon.core.config.PhaseConfigRegistry.PhaseConfigDefinition(
                Phase1Config.class,
                config -> ((Phase1Config) config).validate(),
                () -> new Phase1Config(),
                config -> ((Phase1Config) config).applyDefaults(),
                "Basic Entity Foundation - Core turtle entity with rendering and spawning"
            )
        );
        
        AethelonCore.LOGGER.debug("Registered Phase 1 configuration");
    }
    
    @Override
    public void shutdown() throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase 1");
        
        // Sub-modules handle their own shutdown
        // This module just tracks overall phase state
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase 1 shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // Phase 1 has no dependencies - it's the foundation
        return new ArrayList<>();
    }
    
    @Override
    public boolean isCompatibleWith(String minecraftVersion) {
        // Phase 1 is compatible with Minecraft 1.21.4+
        return minecraftVersion.startsWith("1.21.4") || 
               minecraftVersion.startsWith("1.21.5") ||
               minecraftVersion.startsWith("1.22");
    }
    
    @Override
    public String getRequiredFabricApiVersion() {
        return "0.119.2";
    }
    
    @Override
    public String getDescription() {
        return "Basic Entity Foundation - Implements core Aethelon turtle entity with rendering and spawning";
    }
    
    @Override
    public boolean supportsHotReload() {
        // Phase 1 doesn't support hot reload due to entity registration
        return false;
    }
    
    @Override
    public int getLoadPriority() {
        // Phase 1 loads first (lowest priority number)
        return 10;
    }
}