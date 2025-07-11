package com.bvhfve.aethelon.core.registry;

import com.bvhfve.aethelon.core.AethelonCore;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * RegistryManager - Centralized coordination of all mod registrations
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: Minecraft Registry system for registering game content
 * - Hooks into: Registry.register() calls across all registries
 * - Modifies: Game registries by adding mod content
 * 
 * MODULE ROLE:
 * - Purpose: Coordinate registration across modules, handle dependencies
 * - Dependencies: None (core system)
 * - Provides: Registration coordination, dependency resolution, cleanup
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+ (uses current Registry API)
 * - Fabric API: 0.119.2+ (uses Fabric registration patterns)
 * - Breaking changes: Registry API changes may require updates
 */
public class RegistryManager {
    
    /**
     * Map of module name to its registry entries
     * Used for tracking what each module has registered for cleanup/debugging
     */
    private final Map<String, List<RegistryEntry<?>>> moduleRegistrations = new HashMap<>();
    
    /**
     * List of pending registrations that depend on other modules
     * Processed after all modules are loaded
     */
    private final List<PendingRegistration> pendingRegistrations = new ArrayList<>();
    
    public RegistryManager() {
        AethelonCore.LOGGER.debug("Initialized RegistryManager");
    }
    
    /**
     * Register an object in a Minecraft registry, tracking it for a specific module
     * 
     * @param <T> Type of object being registered
     * @param registry The Minecraft registry to register in
     * @param identifier The identifier for the registered object
     * @param object The object to register
     * @param moduleName Name of the module performing the registration
     * @return The registered object
     * 
     * MINECRAFT CONTEXT:
     * - Called by: Module registration methods during initialization
     * - Timing: During mod loading, before world generation
     * - Thread safety: Single-threaded during mod loading
     */
    public <T> T register(Registry<T> registry, Identifier identifier, T object, String moduleName) {
        T registered = Registry.register(registry, identifier, object);
        
        // Track this registration for the module
        moduleRegistrations.computeIfAbsent(moduleName, k -> new ArrayList<>())
                .add(new RegistryEntry<>(registry, identifier, registered));
        
        if (AethelonCore.getConfigManager().getDebugConfig().logModuleLoading) {
            AethelonCore.LOGGER.debug("Module '{}' registered {} in {}", 
                    moduleName, identifier, registry.getKey().getValue());
        }
        
        return registered;
    }
    
    /**
     * Register an object with a supplier, allowing for lazy evaluation
     * Useful when registration depends on other modules being loaded first
     * 
     * @param <T> Type of object being registered
     * @param registry The Minecraft registry to register in
     * @param identifier The identifier for the registered object
     * @param supplier Supplier that creates the object when called
     * @param moduleName Name of the module performing the registration
     * @param dependencies List of module names this registration depends on
     */
    public <T> void registerLazy(Registry<T> registry, Identifier identifier, 
                                 Supplier<T> supplier, String moduleName, List<String> dependencies) {
        pendingRegistrations.add(new PendingRegistration(
                registry, identifier, supplier, moduleName, dependencies));
        
        if (AethelonCore.getConfigManager().getDebugConfig().logModuleLoading) {
            AethelonCore.LOGGER.debug("Module '{}' queued lazy registration for {} (depends on: {})", 
                    moduleName, identifier, dependencies);
        }
    }
    
    /**
     * Process all pending registrations after modules are loaded
     * Resolves dependencies and performs registrations in correct order
     * 
     * MINECRAFT CONTEXT:
     * - Called by: ModuleLoader after all modules are initialized
     * - Timing: After module loading, before world generation
     * - Thread safety: Single-threaded during mod loading
     */
    public void processPendingRegistrations() {
        AethelonCore.LOGGER.debug("Processing {} pending registrations", pendingRegistrations.size());
        
        List<PendingRegistration> remaining = new ArrayList<>(pendingRegistrations);
        List<PendingRegistration> processed = new ArrayList<>();
        
        // Simple dependency resolution - process registrations whose dependencies are satisfied
        while (!remaining.isEmpty() && !processed.isEmpty() || processed.isEmpty()) {
            processed.clear();
            
            for (PendingRegistration pending : remaining) {
                if (areDependenciesSatisfied(pending.dependencies)) {
                    processPendingRegistration(pending);
                    processed.add(pending);
                }
            }
            
            remaining.removeAll(processed);
            
            // Prevent infinite loop if dependencies can't be resolved
            if (processed.isEmpty() && !remaining.isEmpty()) {
                AethelonCore.LOGGER.error("Cannot resolve dependencies for {} registrations", remaining.size());
                for (PendingRegistration pending : remaining) {
                    AethelonCore.LOGGER.error("  {} depends on {}", pending.identifier, pending.dependencies);
                }
                break;
            }
        }
        
        pendingRegistrations.clear();
        AethelonCore.LOGGER.debug("Completed processing pending registrations");
    }
    
    /**
     * Check if all dependencies for a registration are satisfied
     * 
     * @param dependencies List of module names that must be loaded
     * @return true if all dependencies are satisfied
     */
    private boolean areDependenciesSatisfied(List<String> dependencies) {
        return dependencies.stream().allMatch(moduleRegistrations::containsKey);
    }
    
    /**
     * Process a single pending registration
     * 
     * @param pending The pending registration to process
     */
    @SuppressWarnings("unchecked")
    private void processPendingRegistration(PendingRegistration pending) {
        try {
            Object object = pending.supplier.get();
            Registry<Object> registry = (Registry<Object>) pending.registry;
            register(registry, pending.identifier, object, pending.moduleName);
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to process pending registration for {}", pending.identifier, e);
        }
    }
    
    /**
     * Get all registrations for a specific module
     * Useful for debugging and cleanup
     * 
     * @param moduleName Name of the module
     * @return List of registry entries for the module
     */
    public List<RegistryEntry<?>> getModuleRegistrations(String moduleName) {
        return moduleRegistrations.getOrDefault(moduleName, new ArrayList<>());
    }
    
    /**
     * Get total number of registrations across all modules
     * 
     * @return Total registration count
     */
    public int getTotalRegistrationCount() {
        return moduleRegistrations.values().stream()
                .mapToInt(List::size)
                .sum();
    }
    
    /**
     * Data class representing a registry entry
     */
    public static class RegistryEntry<T> {
        public final Registry<T> registry;
        public final Identifier identifier;
        public final T object;
        
        public RegistryEntry(Registry<T> registry, Identifier identifier, T object) {
            this.registry = registry;
            this.identifier = identifier;
            this.object = object;
        }
    }
    
    /**
     * Data class representing a pending registration with dependencies
     */
    private static class PendingRegistration {
        public final Registry<?> registry;
        public final Identifier identifier;
        public final Supplier<?> supplier;
        public final String moduleName;
        public final List<String> dependencies;
        
        public PendingRegistration(Registry<?> registry, Identifier identifier, 
                                 Supplier<?> supplier, String moduleName, List<String> dependencies) {
            this.registry = registry;
            this.identifier = identifier;
            this.supplier = supplier;
            this.moduleName = moduleName;
            this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
        }
    }
}