package com.bvhfve.aethelon.core.util;

import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.di.ServiceRegistry;

/**
 * InjectableAethelonModule - Enhanced module interface with dependency injection support
 * 
 * This interface extends the base AethelonModule with dependency injection capabilities,
 * providing a modern, testable, and maintainable approach to module development.
 * 
 * BENEFITS OVER STATIC DEPENDENCIES:
 * - Testable: Easy to mock dependencies for unit testing
 * - Maintainable: Clear dependency declarations and lifecycle
 * - Flexible: Runtime dependency resolution and configuration
 * - Safe: Proper initialization order and error handling
 * - Scalable: Easy to add new services and dependencies
 * 
 * MIGRATION STRATEGY:
 * - Phase 1: Keep existing modules working with backward compatibility
 * - Phase 2: Migrate modules to use dependency injection
 * - Phase 3: Remove static access patterns
 * - Phase 4: Full DI-based architecture
 * 
 * USAGE PATTERNS:
 * - Implement this interface instead of AethelonModule
 * - Use @Inject annotations for dependencies
 * - Access services through injected interfaces
 * - Declare service dependencies in getDependencies()
 */
public interface InjectableAethelonModule extends AethelonModule {
    
    /**
     * Inject dependencies into this module
     * Called by the module loader before initialize()
     * 
     * @param serviceRegistry Service registry for dependency resolution
     * @throws Exception if dependency injection fails
     */
    default void injectDependencies(ServiceRegistry serviceRegistry) throws Exception {
        // Default implementation does nothing for backward compatibility
        // Modules can override this to perform custom injection logic
    }
    
    /**
     * Get the list of service classes this module requires
     * Used for dependency validation and service registration order
     * 
     * @return List of service classes this module depends on
     */
    default java.util.List<Class<?>> getRequiredServices() {
        return java.util.List.of();
    }
    
    /**
     * Get the list of service classes this module provides
     * Used for service registration and dependency resolution
     * 
     * @return List of service classes this module provides
     */
    default java.util.List<Class<?>> getProvidedServices() {
        return java.util.List.of();
    }
    
    /**
     * Initialize the module with dependency injection support
     * This method replaces the standard initialize() method for DI-enabled modules
     * 
     * @param serviceRegistry Service registry for accessing dependencies
     * @throws Exception if initialization fails
     */
    default void initializeWithDI(ServiceRegistry serviceRegistry) throws Exception {
        // Default implementation calls standard initialize() for backward compatibility
        initialize();
    }
    
    /**
     * Shutdown the module with dependency injection support
     * This method replaces the standard shutdown() method for DI-enabled modules
     * 
     * @param serviceRegistry Service registry for cleanup
     * @throws Exception if shutdown fails
     */
    default void shutdownWithDI(ServiceRegistry serviceRegistry) throws Exception {
        // Default implementation calls standard shutdown() for backward compatibility
        shutdown();
    }
    
    /**
     * Validate that all required dependencies are available
     * Called before initialization to ensure module can start successfully
     * 
     * @param serviceRegistry Service registry to check dependencies against
     * @return true if all dependencies are satisfied, false otherwise
     */
    default boolean validateDependencies(ServiceRegistry serviceRegistry) {
        for (Class<?> serviceClass : getRequiredServices()) {
            if (!serviceRegistry.isServiceRegistered(serviceClass)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get the dependency injection scope for this module
     * Determines how services are created and managed for this module
     * 
     * @return DI scope (default: MODULE for phase-scoped services)
     */
    default DependencyInjectionContainer.Scope getDependencyScope() {
        return DependencyInjectionContainer.Scope.MODULE;
    }
    
    /**
     * Check if this module supports dependency injection
     * Used for migration and backward compatibility
     * 
     * @return true if module uses dependency injection, false for legacy modules
     */
    default boolean supportsDependencyInjection() {
        return true;
    }
    
    /**
     * Get module-specific service configuration
     * Allows modules to customize how their services are registered and managed
     * 
     * @return Service configuration for this module
     */
    default ModuleServiceConfiguration getServiceConfiguration() {
        return new ModuleServiceConfiguration();
    }
    
    /**
     * Configuration for module-specific services
     */
    class ModuleServiceConfiguration {
        private final java.util.Map<Class<?>, DependencyInjectionContainer.Scope> serviceScopes = new java.util.HashMap<>();
        private final java.util.Map<Class<?>, String> serviceQualifiers = new java.util.HashMap<>();
        private final java.util.Set<Class<?>> lazyServices = new java.util.HashSet<>();
        
        /**
         * Configure scope for a service
         */
        public <T> ModuleServiceConfiguration withScope(Class<T> serviceClass, DependencyInjectionContainer.Scope scope) {
            serviceScopes.put(serviceClass, scope);
            return this;
        }
        
        /**
         * Configure qualifier for a service
         */
        public <T> ModuleServiceConfiguration withQualifier(Class<T> serviceClass, String qualifier) {
            serviceQualifiers.put(serviceClass, qualifier);
            return this;
        }
        
        /**
         * Mark a service as lazy (created on first access)
         */
        public <T> ModuleServiceConfiguration asLazy(Class<T> serviceClass) {
            lazyServices.add(serviceClass);
            return this;
        }
        
        // Getters
        public java.util.Map<Class<?>, DependencyInjectionContainer.Scope> getServiceScopes() {
            return serviceScopes;
        }
        
        public java.util.Map<Class<?>, String> getServiceQualifiers() {
            return serviceQualifiers;
        }
        
        public java.util.Set<Class<?>> getLazyServices() {
            return lazyServices;
        }
    }
}