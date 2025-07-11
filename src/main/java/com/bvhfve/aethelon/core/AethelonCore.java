package com.bvhfve.aethelon.core;

import com.bvhfve.aethelon.core.config.ConfigManager;
import com.bvhfve.aethelon.core.registry.RegistryManager;
import com.bvhfve.aethelon.core.util.ModuleLoader;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AethelonCore - Main mod initialization and module coordination
 * 
 * MINECRAFT INTEGRATION:
 * - Implements: ModInitializer (Fabric API entry point)
 * - Hooks into: Fabric mod loading lifecycle
 * - Modifies: None (pure coordination layer)
 * 
 * MODULE ROLE:
 * - Purpose: Coordinates loading and initialization of all mod phases
 * - Dependencies: None (core module)
 * - Provides: Module loading infrastructure, configuration management
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: None expected (uses stable Fabric APIs)
 */
public class AethelonCore implements ModInitializer {
    public static final String MOD_ID = "aethelon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static ConfigManager configManager;
    private static RegistryManager registryManager;
    private static ModuleLoader moduleLoader;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Aethelon Core - The World Turtle mod");
        
        try {
            // Initialize core systems
            initializeCore();
            
            // Load and initialize enabled modules
            loadModules();
            
            LOGGER.info("Aethelon Core initialization complete");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Aethelon Core", e);
            throw new RuntimeException("Aethelon initialization failed", e);
        }
    }
    
    /**
     * Initialize core systems required by all modules
     * 
     * MINECRAFT CONTEXT:
     * - Called by: Fabric mod loader during initialization
     * - Timing: Early in mod loading process, before world generation
     * - Thread safety: Single-threaded during mod loading
     */
    private void initializeCore() {
        LOGGER.debug("Initializing core systems");
        
        // Initialize new configuration system
        com.bvhfve.aethelon.core.config.AethelonConfig.initialize();
        
        // Initialize backward-compatible configuration manager
        configManager = new ConfigManager();
        
        // Initialize registry coordination
        registryManager = new RegistryManager();
        
        // Initialize module loading system
        moduleLoader = new ModuleLoader(configManager, registryManager);
        
        LOGGER.debug("Core systems initialized successfully");
    }
    
    /**
     * Load and initialize all enabled modules based on configuration
     * 
     * MINECRAFT CONTEXT:
     * - Called by: Core initialization after core systems ready
     * - Timing: During mod initialization, before game content registration
     * - Thread safety: Single-threaded during mod loading
     */
    private void loadModules() {
        LOGGER.debug("Loading enabled modules");
        
        // Load modules in dependency order
        moduleLoader.loadEnabledModules();
        
        LOGGER.info("Loaded {} modules successfully", moduleLoader.getLoadedModuleCount());
    }
    
    // Static accessors for other modules
    public static ConfigManager getConfigManager() {
        return configManager;
    }
    
    public static RegistryManager getRegistryManager() {
        return registryManager;
    }
    
    public static ModuleLoader getModuleLoader() {
        return moduleLoader;
    }
}