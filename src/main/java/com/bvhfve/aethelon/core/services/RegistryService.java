package com.bvhfve.aethelon.core.services;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

/**
 * RegistryService - Service interface for registry management
 * 
 * This interface replaces static access to RegistryManager with a proper service
 * that can be injected and tested.
 * 
 * REPLACES:
 * - AethelonCore.getRegistryManager()
 * - Direct RegistryManager static access
 * - Registry.register() calls scattered throughout modules
 * 
 * BENEFITS:
 * - Centralized registry coordination
 * - Dependency injection support
 * - Easy mocking for testing
 * - Clear service contract
 * - Registration tracking and debugging
 */
public interface RegistryService {
    
    /**
     * Register an object in a Minecraft registry, tracking it for a specific module
     * 
     * @param <T> Type of object being registered
     * @param registry The Minecraft registry to register in
     * @param identifier The identifier for the registered object
     * @param object The object to register
     * @param moduleName Name of the module performing the registration
     * @return The registered object
     */
    <T> T register(Registry<T> registry, Identifier identifier, T object, String moduleName);
    
    /**
     * Register an object with a supplier, allowing for lazy evaluation
     * 
     * @param <T> Type of object being registered
     * @param registry The Minecraft registry to register in
     * @param identifier The identifier for the registered object
     * @param supplier Supplier that creates the object when called
     * @param moduleName Name of the module performing the registration
     * @param dependencies List of module names this registration depends on
     */
    <T> void registerLazy(Registry<T> registry, Identifier identifier, 
                         Supplier<T> supplier, String moduleName, List<String> dependencies);
    
    /**
     * Process all pending registrations after modules are loaded
     */
    void processPendingRegistrations();
    
    /**
     * Get all registrations for a specific module
     * 
     * @param moduleName Name of the module
     * @return List of registry entries for the module
     */
    List<RegistryEntry<?>> getModuleRegistrations(String moduleName);
    
    /**
     * Get total number of registrations across all modules
     * 
     * @return Total registration count
     */
    int getTotalRegistrationCount();
    
    /**
     * Check if a specific object is registered
     * 
     * @param registry The registry to check
     * @param identifier The identifier to look for
     * @return true if object is registered
     */
    <T> boolean isRegistered(Registry<T> registry, Identifier identifier);
    
    /**
     * Get a registered object by identifier
     * 
     * @param registry The registry to search
     * @param identifier The identifier to look for
     * @return The registered object, or null if not found
     */
    <T> T getRegistered(Registry<T> registry, Identifier identifier);
    
    /**
     * Unregister all registrations for a specific module (if supported)
     * 
     * @param moduleName Name of the module
     * @return Number of registrations that were unregistered
     */
    int unregisterModule(String moduleName);
    
    /**
     * Get registration statistics for debugging
     * 
     * @return Registration statistics
     */
    RegistrationStatistics getStatistics();
    
    /**
     * Data class representing a registry entry
     */
    class RegistryEntry<T> {
        public final Registry<T> registry;
        public final Identifier identifier;
        public final T object;
        public final String moduleName;
        public final long registrationTime;
        
        public RegistryEntry(Registry<T> registry, Identifier identifier, T object, 
                           String moduleName, long registrationTime) {
            this.registry = registry;
            this.identifier = identifier;
            this.object = object;
            this.moduleName = moduleName;
            this.registrationTime = registrationTime;
        }
        
        @Override
        public String toString() {
            return String.format("RegistryEntry[%s:%s by %s]", 
                registry.getKey().getValue(), identifier, moduleName);
        }
    }
    
    /**
     * Registration statistics for debugging and monitoring
     */
    class RegistrationStatistics {
        public final int totalRegistrations;
        public final int moduleCount;
        public final int pendingRegistrations;
        public final long oldestRegistrationTime;
        public final long newestRegistrationTime;
        
        public RegistrationStatistics(int totalRegistrations, int moduleCount, 
                                    int pendingRegistrations, long oldestRegistrationTime, 
                                    long newestRegistrationTime) {
            this.totalRegistrations = totalRegistrations;
            this.moduleCount = moduleCount;
            this.pendingRegistrations = pendingRegistrations;
            this.oldestRegistrationTime = oldestRegistrationTime;
            this.newestRegistrationTime = newestRegistrationTime;
        }
        
        @Override
        public String toString() {
            return String.format(
                "RegistrationStatistics[total=%d, modules=%d, pending=%d, timespan=%dms]",
                totalRegistrations, moduleCount, pendingRegistrations, 
                newestRegistrationTime - oldestRegistrationTime
            );
        }
    }
}