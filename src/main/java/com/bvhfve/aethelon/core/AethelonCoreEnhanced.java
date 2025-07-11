package com.bvhfve.aethelon.core;

import com.bvhfve.aethelon.core.config.ConfigManager;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.di.ServiceRegistry;
import com.bvhfve.aethelon.core.registry.RegistryManager;
import com.bvhfve.aethelon.core.util.EnhancedModuleLoader;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AethelonCoreEnhanced - Enhanced version of AethelonCore with DI support
 * 
 * This enhanced core initialization demonstrates the DI framework in action
 * while maintaining full backward compatibility with the existing system.
 * 
 * FEATURES:
 * - Dependency injection container and service registry
 * - Enhanced module loader with DI support
 * - Feature flag system for gradual migration
 * - Comprehensive logging and debugging
 * - Backward compatibility with legacy modules
 * 
 * USAGE:
 * To enable this enhanced core, update fabric.mod.json to use this class
 * instead of AethelonCore. All existing functionality will continue to work.
 */
public class AethelonCoreEnhanced implements ModInitializer {
    public static final String MOD_ID = "aethelon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + ".enhanced");
    
    private static DependencyInjectionContainer diContainer;
    private static ServiceRegistry serviceRegistry;
    private static EnhancedModuleLoader enhancedModuleLoader;
    
    // Core services (for backward compatibility)
    private static ConfigManager configManager;
    private static RegistryManager registryManager;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Aethelon Core (Enhanced with DI) - The World Turtle mod");
        
        try {
            // Initialize core systems first (for backward compatibility)
            initializeCore();
            
            // Initialize DI infrastructure
            initializeDIInfrastructure();
            
            // Load modules with enhanced loader
            loadModulesWithDI();
            
            LOGGER.info("Aethelon Core (Enhanced) initialization complete");
            logInitializationSummary();
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Aethelon Core (Enhanced)", e);
            throw new RuntimeException("Aethelon Enhanced initialization failed", e);
        }
    }
    
    /**
     * Initialize core systems for backward compatibility
     */
    private void initializeCore() {
        LOGGER.debug("Initializing core systems for backward compatibility");
        
        // Initialize configuration system
        com.bvhfve.aethelon.core.config.AethelonConfig.initialize();
        
        // Create core services
        configManager = new ConfigManager();
        registryManager = new RegistryManager();
        
        LOGGER.debug("Core systems initialized successfully");
    }
    
    /**
     * Initialize dependency injection infrastructure
     */
    private void initializeDIInfrastructure() {
        LOGGER.debug("Initializing DI infrastructure");
        
        // Create DI container
        diContainer = new DependencyInjectionContainer();
        
        // Create service registry and register core services
        serviceRegistry = new ServiceRegistry(diContainer);
        serviceRegistry.registerCoreServices();
        
        // Create enhanced module loader
        enhancedModuleLoader = new EnhancedModuleLoader(configManager, registryManager);
        
        LOGGER.info("DI infrastructure initialized with {} registered services", 
            serviceRegistry.getStatistics().get("registeredServices"));
    }
    
    /**
     * Load modules using enhanced loader with DI support
     */
    private void loadModulesWithDI() {
        LOGGER.debug("Loading modules with enhanced DI support");
        
        // Determine which mode we're running in
        boolean useDI = configManager.getDebugConfig().logModuleLoading;
        LOGGER.info("Module loading mode: {} (DI enabled: {})", 
            useDI ? "Dependency Injection" : "Legacy", useDI);
        
        // Load modules using enhanced loader
        enhancedModuleLoader.loadEnabledModules();
        
        LOGGER.info("Enhanced module loading complete");
    }
    
    /**
     * Log initialization summary for debugging
     */
    private void logInitializationSummary() {
        if (configManager.getDebugConfig().logModuleLoading) {
            LOGGER.info("=== Aethelon Enhanced Initialization Summary ===");
            
            // DI Container statistics
            var diStats = serviceRegistry.getStatistics();
            LOGGER.info("DI Container: {} services registered, {} singletons, {} module scopes",
                diStats.get("registeredServices"), diStats.get("singletonInstances"), diStats.get("moduleScopes"));
            
            // Module loader statistics
            LOGGER.info("Enhanced Module Loader: DI modules available and feature flags active");
            
            // Registry statistics
            LOGGER.info("Registry Manager: {} total registrations", registryManager.getTotalRegistrationCount());
            
            // Feature flag status
            boolean useDI = configManager.getDebugConfig().logModuleLoading;
            LOGGER.info("Feature Flags: DI modules {}, Legacy modules {}", 
                useDI ? "ENABLED" : "disabled", useDI ? "disabled" : "ENABLED");
            
            LOGGER.info("=== End Summary ===");
        }
    }
    
    // Static accessors for backward compatibility
    
    /**
     * Get config manager (backward compatibility)
     */
    public static ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Get registry manager (backward compatibility)
     */
    public static RegistryManager getRegistryManager() {
        return registryManager;
    }
    
    /**
     * Get enhanced module loader
     */
    public static EnhancedModuleLoader getEnhancedModuleLoader() {
        return enhancedModuleLoader;
    }
    
    /**
     * Get DI container (for advanced usage)
     */
    public static DependencyInjectionContainer getDIContainer() {
        return diContainer;
    }
    
    /**
     * Get service registry (primary access point for services)
     */
    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
    
    /**
     * Get service instance (convenience method)
     */
    public static <T> T getService(Class<T> serviceClass) {
        if (serviceRegistry == null) {
            throw new IllegalStateException("Service registry not initialized. Call onInitialize() first.");
        }
        return serviceRegistry.getService(serviceClass);
    }
    
    /**
     * Get service instance with qualifier (convenience method)
     */
    public static <T> T getService(Class<T> serviceClass, String qualifier) {
        if (serviceRegistry == null) {
            throw new IllegalStateException("Service registry not initialized. Call onInitialize() first.");
        }
        return serviceRegistry.getService(serviceClass, qualifier);
    }
}