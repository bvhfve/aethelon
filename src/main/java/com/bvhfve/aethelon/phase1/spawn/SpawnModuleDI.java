package com.bvhfve.aethelon.phase1.spawn;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.di.ServiceRegistry;
import com.bvhfve.aethelon.core.services.ConfigService;
import com.bvhfve.aethelon.core.util.InjectableAethelonModule;
import com.bvhfve.aethelon.phase1.services.SpawnService;

import java.util.List;

/**
 * SpawnModuleDI - Dependency injection version of SpawnModule
 * 
 * This module demonstrates the improved architecture using dependency injection
 * and service-oriented design. It replaces the original SpawnModule's static
 * dependencies with clean, testable service injection.
 * 
 * IMPROVEMENTS OVER ORIGINAL:
 * - Service injection instead of static access
 * - Clear dependency declarations
 * - Testable design with mockable services
 * - Proper error handling and validation
 * - Clean separation of concerns
 * 
 * MIGRATION NOTES:
 * - This runs alongside the original SpawnModule during transition
 * - In Phase 2.2, this would replace the original implementation
 * - Shows the full potential of the DI architecture
 * - Maintains all functionality while improving maintainability
 */
public class SpawnModuleDI implements InjectableAethelonModule {
    
    // Injected services
    @DependencyInjectionContainer.Inject
    private SpawnService spawnService;
    
    @DependencyInjectionContainer.Inject
    private ConfigService configService;
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase1.spawn.di";
    }
    
    @Override
    public String getPhase() {
        return "phase1";
    }
    
    @Override
    public boolean isEnabled() {
        // Enable DI version only in debug mode during transition
        return configService.isModuleEnabled("phase1", "spawn") && 
               configService.getDebugConfig().isModuleLoadingEnabled();
    }
    
    @Override
    public void initializeWithDI(ServiceRegistry serviceRegistry) throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase 1 Spawn module (DI) is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase 1 Spawn module (DI version)");
        
        try {
            // Validate all dependencies are available
            if (!validateDependencies(serviceRegistry)) {
                throw new IllegalStateException("Required services not available for SpawnModuleDI");
            }
            
            // Use services to perform initialization
            spawnService.registerSpawnEgg();
            spawnService.configureNaturalSpawning();
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase 1 Spawn module (DI) initialization complete");
            
            // Log service state for debugging
            if (configService.getDebugConfig().isModuleLoadingEnabled()) {
                AethelonCore.LOGGER.debug("SpawnService state: {}", 
                    ((com.bvhfve.aethelon.phase1.services.SpawnServiceImpl) spawnService).getDebugInfo());
                AethelonCore.LOGGER.debug("Spawn configuration: {}", spawnService.getSpawnConfiguration());
            }
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase 1 Spawn module (DI)", e);
            throw e;
        }
    }
    
    @Override
    public void shutdownWithDI(ServiceRegistry serviceRegistry) throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase 1 Spawn module (DI)");
        
        // Spawn configurations and items cannot be unregistered in Minecraft
        // But we can clean up our service state and log the shutdown
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase 1 Spawn module (DI) shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // Depends on main Phase 1 module and DI entity module
        return List.of("phase1", "phase1.entity.di");
    }
    
    @Override
    public List<Class<?>> getRequiredServices() {
        // Declare service dependencies for validation
        return List.of(
            SpawnService.class,
            ConfigService.class
        );
    }
    
    @Override
    public List<Class<?>> getProvidedServices() {
        // This module doesn't provide services directly
        // The SpawnService is provided by the DI container
        return List.of();
    }
    
    @Override
    public ModuleServiceConfiguration getServiceConfiguration() {
        return new ModuleServiceConfiguration()
            .withScope(SpawnService.class, DependencyInjectionContainer.Scope.MODULE)
            .withQualifier(SpawnService.class, "phase1");
    }
    
    @Override
    public boolean isCompatibleWith(String minecraftVersion) {
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
        return "Spawn Management (DI) - Dependency injection version of spawn configuration module";
    }
    
    @Override
    public boolean supportsHotReload() {
        return false; // Spawn configuration cannot be undone
    }
    
    @Override
    public int getLoadPriority() {
        return 41; // Load after original spawn module for comparison
    }
    
    @Override
    public DependencyInjectionContainer.Scope getDependencyScope() {
        return DependencyInjectionContainer.Scope.MODULE;
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
     * Get spawn service for external access (if needed)
     */
    public SpawnService getSpawnService() {
        return spawnService;
    }
}