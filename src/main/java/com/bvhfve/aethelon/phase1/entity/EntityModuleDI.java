package com.bvhfve.aethelon.phase1.entity;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.di.ServiceRegistry;
import com.bvhfve.aethelon.core.registry.RegistryManager;
import com.bvhfve.aethelon.core.util.InjectableAethelonModule;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import java.util.List;

/**
 * EntityModuleDI - Dependency injection version of EntityModule
 * 
 * This is a demonstration of how to migrate existing modules to use dependency injection.
 * It shows the improved patterns and practices for future module development.
 * 
 * IMPROVEMENTS OVER ORIGINAL:
 * - Injected dependencies instead of static access
 * - Service-oriented architecture
 * - Proper error handling and validation
 * - Testable design with interface abstractions
 * - Clear separation of concerns
 * 
 * MIGRATION NOTES:
 * - This runs alongside the original EntityModule for comparison
 * - In Phase 2, we would replace the original with this version
 * - Shows how to gradually adopt DI patterns
 * - Maintains compatibility with existing systems
 */
public class EntityModuleDI implements InjectableAethelonModule {
    
    // Injected dependencies
    @DependencyInjectionContainer.Inject
    private RegistryManager registryManager;
    
    @DependencyInjectionContainer.Inject
    private EntityTypeService entityTypeService;
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase1.entity.di";
    }
    
    @Override
    public String getPhase() {
        return "phase1";
    }
    
    @Override
    public boolean isEnabled() {
        // For demo purposes, this is disabled by default
        // In a real migration, this would replace the original module
        return AethelonCore.getConfigManager().isModuleEnabled("phase1", "entity") && 
               AethelonCore.getConfigManager().getDebugConfig().logModuleLoading; // Only enable in debug mode
    }
    
    @Override
    public void initializeWithDI(ServiceRegistry serviceRegistry) throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase 1 Entity DI module is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase 1 Entity module (DI version)");
        
        try {
            // Validate dependencies are available
            if (!validateDependencies(serviceRegistry)) {
                throw new IllegalStateException("Required dependencies not available");
            }
            
            // Create entity type through service
            entityTypeService.createAethelonEntityType();
            
            // Register entity attributes
            FabricDefaultAttributeRegistry.register(
                entityTypeService.getAethelonEntityType(),
                AethelonEntity.createAethelonAttributes()
            );
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase 1 Entity DI module initialization complete");
            
            // Log service information for debugging
            if (AethelonCore.getConfigManager().getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("EntityTypeService state: {}", 
                    ((EntityTypeServiceImpl) entityTypeService).getDebugInfo());
            }
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase 1 Entity DI module", e);
            throw e;
        }
    }
    
    @Override
    public void shutdownWithDI(ServiceRegistry serviceRegistry) throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase 1 Entity DI module");
        
        // Entity types cannot be unregistered in Minecraft
        // But we can clean up our service state
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase 1 Entity DI module shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // Depends on main Phase 1 module
        return List.of("phase1");
    }
    
    @Override
    public List<Class<?>> getRequiredServices() {
        // Declare service dependencies
        return List.of(
            RegistryManager.class,
            EntityTypeService.class
        );
    }
    
    @Override
    public List<Class<?>> getProvidedServices() {
        // This module doesn't provide services directly
        // The EntityTypeService is provided by the DI container
        return List.of();
    }
    
    @Override
    public ModuleServiceConfiguration getServiceConfiguration() {
        return new ModuleServiceConfiguration()
            .withScope(EntityTypeService.class, DependencyInjectionContainer.Scope.SINGLETON)
            .withQualifier(EntityTypeService.class, "phase1");
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
        return "Entity Registration (DI) - Dependency injection version of entity registration module";
    }
    
    @Override
    public boolean supportsHotReload() {
        return false; // Entity registration cannot be undone
    }
    
    @Override
    public int getLoadPriority() {
        return 21; // Load after original entity module for comparison
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
}