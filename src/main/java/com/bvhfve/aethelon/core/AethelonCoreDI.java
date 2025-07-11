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
 * AethelonCoreDI - Dependency injection version of AethelonCore
 * 
 * This is a demonstration of how the core initialization would look with
 * full dependency injection support. It shows the improved architecture
 * and patterns for future development.
 * 
 * IMPROVEMENTS OVER ORIGINAL:
 * - Service-oriented architecture with DI container
 * - Enhanced module loader with DI support
 * - Proper service lifecycle management
 * - Better error handling and validation
 * - Comprehensive debugging and monitoring
 * 
 * MIGRATION NOTES:
 * - This runs alongside the original AethelonCore for comparison
 * - In Phase 2, this would replace the original implementation
 * - Shows the full potential of the DI architecture
 * - Maintains compatibility with existing systems
 */
public class AethelonCoreDI implements ModInitializer {
    public static final String MOD_ID = "aethelon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID + ".di");
    
    private static DependencyInjectionContainer diContainer;
    private static ServiceRegistry serviceRegistry;
    private static EnhancedModuleLoader moduleLoader;
    
    // Core services (injected)
    private static ConfigManager configManager;
    private static RegistryManager registryManager;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Aethelon Core (DI version) - The World Turtle mod");
        
        try {
            // Initialize DI infrastructure
            initializeDIInfrastructure();
            
            // Initialize core systems with DI
            initializeCoreWithDI();
            
            // Load and initialize enabled modules with DI
            loadModulesWithDI();
            
            LOGGER.info("Aethelon Core (DI version) initialization complete");
            logInitializationStatistics();
            
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Aethelon Core (DI version)", e);
            throw new RuntimeException("Aethelon DI initialization failed", e);
        }
    }
    
    /**
     * Initialize dependency injection infrastructure
     */
    private void initializeDIInfrastructure() {
        LOGGER.debug("Initializing DI infrastructure");
        
        // Create DI container
        diContainer = new DependencyInjectionContainer();
        
        // Create service registry
        serviceRegistry = new ServiceRegistry(diContainer);
        
        LOGGER.debug("DI infrastructure initialized successfully");
    }
    
    /**
     * Initialize core systems with dependency injection
     */
    private void initializeCoreWithDI() {
        LOGGER.debug("Initializing core systems with DI");
        
        // Initialize configuration system
        com.bvhfve.aethelon.core.config.AethelonConfig.initialize();
        
        // Create core services
        configManager = new ConfigManager();
        registryManager = new RegistryManager();
        
        // Register core services with DI container
        serviceRegistry.registerCoreServices();
        
        // Create enhanced module loader
        moduleLoader = new EnhancedModuleLoader(configManager, registryManager);
        
        LOGGER.debug("Core systems with DI initialized successfully");
    }
    
    /**
     * Load and initialize all enabled modules with DI support
     */
    private void loadModulesWithDI() {
        LOGGER.debug("Loading enabled modules with DI support");
        
        // Load modules using enhanced loader
        moduleLoader.loadEnabledModules();
        
        LOGGER.info("Loaded modules successfully with DI support");
    }
    
    /**
     * Log initialization statistics for debugging
     */
    private void logInitializationStatistics() {
        if (configManager.getDebugConfig().logModuleLoading) {
            LOGGER.info("=== Aethelon DI Initialization Statistics ===");
            
            // DI Container statistics
            var diStats = serviceRegistry.getStatistics();
            LOGGER.info("DI Container: {} services registered, {} singletons, {} module scopes",
                diStats.get("registeredServices"), diStats.get("singletonInstances"), diStats.get("moduleScopes"));
            
            // Module loader statistics
            LOGGER.info("Enhanced Module Loader: Available for future statistics");
            
            // Registry statistics
            LOGGER.info("Registry Manager: {} total registrations", registryManager.getTotalRegistrationCount());
            
            LOGGER.info("=== End Statistics ===");
        }
    }
    
    // Static accessors for backward compatibility and external access
    
    /**
     * Get the DI container (for advanced usage)
     */
    public static DependencyInjectionContainer getDIContainer() {
        return diContainer;
    }
    
    /**
     * Get the service registry (primary access point for services)
     */
    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
    
    /**
     * Get enhanced module loader
     */
    public static EnhancedModuleLoader getEnhancedModuleLoader() {
        return moduleLoader;
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
    
    /**
     * Check if service is available
     */
    public static boolean isServiceAvailable(Class<?> serviceClass) {
        return serviceRegistry != null && serviceRegistry.isServiceRegistered(serviceClass);
    }
    
    // Backward compatibility accessors (delegate to DI services)
    
    /**
     * Get config manager (backward compatibility)
     */
    public static ConfigManager getConfigManager() {
        if (configManager != null) {
            return configManager;
        }
        
        // Try to get from DI container if available
        if (serviceRegistry != null && serviceRegistry.isServiceRegistered(ConfigManager.class)) {
            return serviceRegistry.getService(ConfigManager.class);
        }
        
        throw new IllegalStateException("ConfigManager not available. Initialize core systems first.");
    }
    
    /**
     * Get registry manager (backward compatibility)
     */
    public static RegistryManager getRegistryManager() {
        if (registryManager != null) {
            return registryManager;
        }
        
        // Try to get from DI container if available
        if (serviceRegistry != null && serviceRegistry.isServiceRegistered(RegistryManager.class)) {
            return serviceRegistry.getService(RegistryManager.class);
        }
        
        throw new IllegalStateException("RegistryManager not available. Initialize core systems first.");
    }
    
    /**
     * Shutdown hook for cleanup
     */
    public static void shutdown() {
        LOGGER.info("Shutting down Aethelon Core (DI version)");
        
        try {
            // Shutdown modules
            if (moduleLoader != null) {
                moduleLoader.shutdownAllModules();
            }
            
            // Shutdown DI infrastructure
            if (serviceRegistry != null) {
                serviceRegistry.shutdown();
            }
            
            // Clear static references
            diContainer = null;
            serviceRegistry = null;
            moduleLoader = null;
            configManager = null;
            registryManager = null;
            
            LOGGER.info("Aethelon Core (DI version) shutdown complete");
            
        } catch (Exception e) {
            LOGGER.error("Error during Aethelon Core (DI version) shutdown", e);
        }
    }
}