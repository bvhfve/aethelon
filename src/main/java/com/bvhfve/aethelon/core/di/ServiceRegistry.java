package com.bvhfve.aethelon.core.di;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.config.ConfigManager;
import com.bvhfve.aethelon.core.registry.RegistryManager;
import com.bvhfve.aethelon.phase1.entity.EntityTypeService;
import com.bvhfve.aethelon.phase1.entity.EntityTypeServiceImpl;

/**
 * ServiceRegistry - Central registration point for all DI services
 * 
 * This class defines and registers all services available through dependency injection.
 * It serves as the configuration point for the DI container and ensures proper
 * service lifecycle management.
 * 
 * DESIGN PATTERNS:
 * - Service Locator: Central registry for service discovery
 * - Factory Pattern: Service creation and configuration
 * - Singleton Pattern: Core services with single instances
 * - Module Pattern: Phase-scoped services
 * 
 * REGISTRATION STRATEGY:
 * - Core services: Singleton scope (ConfigManager, RegistryManager)
 * - Entity services: Module scope (per-phase instances)
 * - Utility services: Prototype scope (new instances)
 * - Configuration: Singleton with lazy initialization
 */
public class ServiceRegistry {
    
    private final DependencyInjectionContainer container;
    private boolean initialized = false;
    
    public ServiceRegistry(DependencyInjectionContainer container) {
        this.container = container;
    }
    
    /**
     * Register all core services with the DI container
     */
    public void registerCoreServices() {
        if (initialized) {
            AethelonCore.LOGGER.warn("ServiceRegistry already initialized, skipping core service registration");
            return;
        }
        
        AethelonCore.LOGGER.debug("Registering core services with DI container");
        
        // Core infrastructure services (Singleton)
        registerInfrastructureServices();
        
        // Phase 1 services
        registerPhase1Services();
        
        // Future phase services (placeholders)
        registerFuturePhaseServices();
        
        initialized = true;
        AethelonCore.LOGGER.info("Registered {} services with DI container", 
            container.getStatistics().get("registeredServices"));
    }
    
    /**
     * Register core infrastructure services
     */
    private void registerInfrastructureServices() {
        // Configuration management service
        container.registerService(
            com.bvhfve.aethelon.core.services.ConfigService.class,
            com.bvhfve.aethelon.core.services.ConfigServiceImpl.class,
            DependencyInjectionContainer.Scope.SINGLETON,
            "core"
        );
        
        // Registry coordination service
        container.registerService(
            com.bvhfve.aethelon.core.services.RegistryService.class,
            com.bvhfve.aethelon.core.services.RegistryServiceImpl.class,
            DependencyInjectionContainer.Scope.SINGLETON,
            "core"
        );
        
        // Legacy compatibility - register existing instances
        container.registerInstance(ConfigManager.class, 
            AethelonCore.getConfigManager(), "legacy");
        
        container.registerInstance(RegistryManager.class, 
            AethelonCore.getRegistryManager(), "legacy");
        
        // Module loading (if needed by services)
        container.registerFactory(
            com.bvhfve.aethelon.core.util.ModuleLoader.class,
            () -> AethelonCore.getModuleLoader(),
            DependencyInjectionContainer.Scope.SINGLETON,
            "core"
        );
        
        AethelonCore.LOGGER.debug("Registered infrastructure services: ConfigService, RegistryService");
    }
    
    /**
     * Register Phase 1 services
     */
    private void registerPhase1Services() {
        // Entity type management
        container.registerService(
            com.bvhfve.aethelon.phase1.entity.EntityTypeService.class,
            com.bvhfve.aethelon.phase1.entity.EntityTypeServiceImpl.class,
            DependencyInjectionContainer.Scope.SINGLETON,
            "phase1"
        );
        
        // Spawn management
        container.registerService(
            com.bvhfve.aethelon.phase1.services.SpawnService.class,
            com.bvhfve.aethelon.phase1.services.SpawnServiceImpl.class,
            DependencyInjectionContainer.Scope.MODULE,
            "phase1"
        );
        
        // Client rendering (client-side only)
        container.registerService(
            com.bvhfve.aethelon.phase1.services.ClientRenderService.class,
            com.bvhfve.aethelon.phase1.services.ClientRenderServiceImpl.class,
            DependencyInjectionContainer.Scope.MODULE,
            "phase1"
        );
        
        AethelonCore.LOGGER.debug("Registered Phase 1 services: EntityTypeService, SpawnService, ClientRenderService");
    }
    
    /**
     * Register placeholder services for future phases
     */
    private void registerFuturePhaseServices() {
        // Phase 2: AI and Behavior services
        // container.registerService(AIService.class, AIServiceImpl.class,
        //     DependencyInjectionContainer.Scope.MODULE, "phase2");
        // container.registerService(PathfindingService.class, PathfindingServiceImpl.class,
        //     DependencyInjectionContainer.Scope.MODULE, "phase2");
        
        // Phase 3: Damage and Interaction services
        // container.registerService(DamageService.class, DamageServiceImpl.class,
        //     DependencyInjectionContainer.Scope.MODULE, "phase3");
        // container.registerService(InteractionService.class, InteractionServiceImpl.class,
        //     DependencyInjectionContainer.Scope.MODULE, "phase3");
        
        // Phase 4: Structure services
        // container.registerService(StructureService.class, StructureServiceImpl.class,
        //     DependencyInjectionContainer.Scope.MODULE, "phase4");
        // container.registerService(IslandService.class, IslandServiceImpl.class,
        //     DependencyInjectionContainer.Scope.MODULE, "phase4");
        
        AethelonCore.LOGGER.debug("Registered future phase service placeholders");
    }
    
    /**
     * Register module-specific services for a phase
     */
    public void registerModuleServices(String phaseName) {
        switch (phaseName) {
            case "phase1":
                registerPhase1ModuleServices();
                break;
            case "phase2":
                registerPhase2ModuleServices();
                break;
            // Add more phases as needed
            default:
                AethelonCore.LOGGER.debug("No module-specific services for phase: {}", phaseName);
        }
    }
    
    /**
     * Register Phase 1 module-specific services
     */
    private void registerPhase1ModuleServices() {
        // Services that need to be created per-module instance
        // These are registered when the phase is actually loaded
        AethelonCore.LOGGER.debug("Registered Phase 1 module-specific services");
    }
    
    /**
     * Register Phase 2 module-specific services (future)
     */
    private void registerPhase2ModuleServices() {
        // Future implementation
        AethelonCore.LOGGER.debug("Phase 2 module services not yet implemented");
    }
    
    /**
     * Unregister services for a specific module scope
     */
    public void unregisterModuleServices(String moduleName) {
        container.clearModuleScope(moduleName);
        AethelonCore.LOGGER.debug("Unregistered services for module: {}", moduleName);
    }
    
    /**
     * Get service instance with automatic dependency injection
     */
    public <T> T getService(Class<T> serviceClass) {
        return container.getService(serviceClass);
    }
    
    /**
     * Get service instance with qualifier
     */
    public <T> T getService(Class<T> serviceClass, String qualifier) {
        return container.getService(serviceClass, qualifier);
    }
    
    /**
     * Get service instance with module scope
     */
    public <T> T getService(Class<T> serviceClass, String qualifier, String moduleScope) {
        return container.getService(serviceClass, qualifier, moduleScope);
    }
    
    /**
     * Check if a service is registered
     */
    public boolean isServiceRegistered(Class<?> serviceClass) {
        try {
            container.getService(serviceClass);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Get container statistics for debugging
     */
    public java.util.Map<String, Object> getStatistics() {
        return container.getStatistics();
    }
    
    /**
     * Shutdown and cleanup all services
     */
    public void shutdown() {
        container.clearAll();
        initialized = false;
        AethelonCore.LOGGER.info("ServiceRegistry shutdown complete");
    }
}