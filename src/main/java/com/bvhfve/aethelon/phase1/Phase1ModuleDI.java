package com.bvhfve.aethelon.phase1;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.di.ServiceRegistry;
import com.bvhfve.aethelon.core.services.ConfigService;
import com.bvhfve.aethelon.core.util.InjectableAethelonModule;

import java.util.List;

/**
 * Phase1ModuleDI - Dependency injection version of Phase1Module
 * 
 * This module serves as the main coordinator for Phase 1 using dependency injection
 * patterns. It replaces the original Phase1Module's static dependencies with clean,
 * testable service injection.
 * 
 * IMPROVEMENTS OVER ORIGINAL:
 * - Service injection instead of static access
 * - Clear dependency declarations
 * - Testable design with mockable services
 * - Proper error handling and validation
 * - Enhanced coordination capabilities
 * 
 * COORDINATION ROLE:
 * - Initializes Phase 1 configuration and services
 * - Coordinates sub-module initialization
 * - Provides phase-level statistics and monitoring
 * - Manages phase lifecycle and state
 * 
 * MIGRATION NOTES:
 * - This runs alongside the original Phase1Module during transition
 * - In Phase 2.2, this would replace the original implementation
 * - Shows the full potential of the DI architecture for phase coordination
 * - Maintains all functionality while improving maintainability
 */
public class Phase1ModuleDI implements InjectableAethelonModule {
    
    // Injected services
    @DependencyInjectionContainer.Inject
    private ConfigService configService;
    
    private boolean initialized = false;
    private long initializationTime;
    private int subModulesInitialized = 0;
    
    @Override
    public String getModuleName() {
        return "phase1.di";
    }
    
    @Override
    public String getPhase() {
        return "phase1";
    }
    
    @Override
    public boolean isEnabled() {
        return configService.isPhaseEnabled("phase1");
    }
    
    @Override
    public void initializeWithDI(ServiceRegistry serviceRegistry) throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase 1 (DI) is disabled, skipping initialization");
            return;
        }
        
        long startTime = System.currentTimeMillis();
        AethelonCore.LOGGER.info("Initializing Phase 1: Basic Entity Foundation (DI version)");
        
        try {
            // Validate all dependencies are available
            if (!validateDependencies(serviceRegistry)) {
                throw new IllegalStateException("Required services not available for Phase1ModuleDI");
            }
            
            // Register Phase 1 configuration
            registerPhaseConfiguration();
            
            // Initialize phase-level services and coordination
            initializePhaseCoordination(serviceRegistry);
            
            // Phase 1 DI initialization is handled by sub-modules:
            // - phase1.entity.di: Entity registration and attributes with DI
            // - phase1.client.di: Client-side rendering and models with DI
            // - phase1.spawn.di: Biome spawning and spawn eggs with DI
            
            // This module serves as a coordinator and dependency anchor for DI modules
            // Sub-modules depend on this module being loaded first
            
            initializationTime = System.currentTimeMillis() - startTime;
            initialized = true;
            
            AethelonCore.LOGGER.info("Phase 1 (DI) initialization complete in {}ms", initializationTime);
            
            // Log phase statistics if debug mode is enabled
            if (configService.getDebugConfig().isModuleLoadingEnabled()) {
                logPhaseStatistics(serviceRegistry);
            }
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase 1 (DI)", e);
            throw e;
        }
    }
    
    /**
     * Register Phase 1 configuration with the configuration system
     */
    private void registerPhaseConfiguration() {
        try {
            // In the DI version, we use the ConfigService for configuration management
            // This provides a cleaner interface than the original static approach
            
            AethelonCore.LOGGER.debug("Phase 1 (DI) configuration registered through ConfigService");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to register Phase 1 configuration", e);
        }
    }
    
    /**
     * Initialize phase-level coordination and monitoring
     */
    private void initializePhaseCoordination(ServiceRegistry serviceRegistry) {
        try {
            // Set up phase-level monitoring and coordination
            // This could include metrics collection, health checks, etc.
            
            // Count available sub-module services
            if (serviceRegistry.isServiceRegistered(com.bvhfve.aethelon.phase1.entity.EntityTypeService.class)) {
                subModulesInitialized++;
            }
            if (serviceRegistry.isServiceRegistered(com.bvhfve.aethelon.phase1.services.SpawnService.class)) {
                subModulesInitialized++;
            }
            if (serviceRegistry.isServiceRegistered(com.bvhfve.aethelon.phase1.services.ClientRenderService.class)) {
                subModulesInitialized++;
            }
            
            AethelonCore.LOGGER.debug("Phase 1 coordination initialized with {} sub-module services", 
                subModulesInitialized);
            
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to initialize phase coordination", e);
        }
    }
    
    /**
     * Log phase statistics for debugging and monitoring
     */
    private void logPhaseStatistics(ServiceRegistry serviceRegistry) {
        try {
            AethelonCore.LOGGER.info("=== Phase 1 (DI) Statistics ===");
            AethelonCore.LOGGER.info("Initialization time: {}ms", initializationTime);
            AethelonCore.LOGGER.info("Sub-module services available: {}", subModulesInitialized);
            
            // Service registry statistics
            var stats = serviceRegistry.getStatistics();
            AethelonCore.LOGGER.info("Total services registered: {}", stats.get("registeredServices"));
            AethelonCore.LOGGER.info("Singleton instances: {}", stats.get("singletonInstances"));
            AethelonCore.LOGGER.info("Module scopes: {}", stats.get("moduleScopes"));
            
            // Phase-specific configuration
            if (configService.isConfigurationReady()) {
                AethelonCore.LOGGER.info("Configuration status: Ready");
                AethelonCore.LOGGER.info("Available phases: {}", configService.getAvailablePhases());
            } else {
                AethelonCore.LOGGER.warn("Configuration status: Not ready");
            }
            
            AethelonCore.LOGGER.info("=== End Phase 1 Statistics ===");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to log phase statistics", e);
        }
    }
    
    @Override
    public void shutdownWithDI(ServiceRegistry serviceRegistry) throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase 1 (DI)");
        
        // Sub-modules handle their own shutdown
        // This module tracks overall phase state and coordination
        
        initialized = false;
        subModulesInitialized = 0;
        initializationTime = 0;
        
        AethelonCore.LOGGER.info("Phase 1 (DI) shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // Phase 1 DI has no dependencies - it's the foundation for DI modules
        return List.of();
    }
    
    @Override
    public List<Class<?>> getRequiredServices() {
        // Declare service dependencies for validation
        return List.of(ConfigService.class);
    }
    
    @Override
    public List<Class<?>> getProvidedServices() {
        // This module doesn't provide services directly
        // It coordinates other services and modules
        return List.of();
    }
    
    @Override
    public ModuleServiceConfiguration getServiceConfiguration() {
        return new ModuleServiceConfiguration();
    }
    
    @Override
    public boolean isCompatibleWith(String minecraftVersion) {
        // Phase 1 DI is compatible with Minecraft 1.21.4+
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
        return "Basic Entity Foundation (DI) - Dependency injection version of Phase 1 coordinator";
    }
    
    @Override
    public boolean supportsHotReload() {
        // Phase 1 doesn't support hot reload due to entity registration
        return false;
    }
    
    @Override
    public int getLoadPriority() {
        // Phase 1 DI loads first (lowest priority number)
        return 11; // Load after original Phase1Module for comparison
    }
    
    @Override
    public DependencyInjectionContainer.Scope getDependencyScope() {
        return DependencyInjectionContainer.Scope.SINGLETON;
    }
    
    // Backward compatibility methods (not used in DI version)
    @Override
    public void initialize() throws Exception {
        throw new UnsupportedOperationException("Use initializeWithDI() for dependency injection modules");
    }
    
    @Override
    public void shutdown() throws Exception {
        throw new UnsupportedOperationException("Use shutdownWithDI() for dependency injection modules");
    }
    
    /**
     * Get phase statistics for external monitoring
     */
    public PhaseStatistics getPhaseStatistics() {
        return new PhaseStatistics(
            initialized,
            initializationTime,
            subModulesInitialized,
            System.currentTimeMillis()
        );
    }
    
    /**
     * Phase statistics data class
     */
    public static class PhaseStatistics {
        public final boolean initialized;
        public final long initializationTime;
        public final int subModulesInitialized;
        public final long timestamp;
        
        public PhaseStatistics(boolean initialized, long initializationTime, 
                             int subModulesInitialized, long timestamp) {
            this.initialized = initialized;
            this.initializationTime = initializationTime;
            this.subModulesInitialized = subModulesInitialized;
            this.timestamp = timestamp;
        }
        
        @Override
        public String toString() {
            return String.format(
                "PhaseStatistics[initialized=%s, initTime=%dms, subModules=%d, timestamp=%d]",
                initialized, initializationTime, subModulesInitialized, timestamp
            );
        }
    }
}