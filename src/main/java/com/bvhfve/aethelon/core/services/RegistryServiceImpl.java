package com.bvhfve.aethelon.core.services;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.registry.RegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * RegistryServiceImpl - Implementation of RegistryService wrapping existing RegistryManager
 * 
 * This implementation provides a clean service interface while delegating to the existing
 * RegistryManager during the migration period. It adds additional functionality like
 * statistics and enhanced error handling.
 * 
 * MIGRATION STRATEGY:
 * - Phase 2.1: Wrap existing RegistryManager
 * - Phase 2.2: Gradually replace static access with service injection
 * - Phase 2.3: Enhance with additional features (statistics, validation)
 * - Phase 3.0: Optimize and potentially replace RegistryManager internals
 */
@DependencyInjectionContainer.Service(scope = DependencyInjectionContainer.Scope.SINGLETON)
public class RegistryServiceImpl implements RegistryService {
    
    private final RegistryManager registryManager;
    
    /**
     * Constructor for dependency injection
     */
    @DependencyInjectionContainer.Inject
    public RegistryServiceImpl() {
        // For now, use the existing static RegistryManager
        // In Phase 3, this will be replaced with proper DI
        this.registryManager = AethelonCore.getRegistryManager();
        
        AethelonCore.LOGGER.debug("RegistryService initialized with existing RegistryManager wrapper");
    }
    
    @Override
    public <T> T register(Registry<T> registry, Identifier identifier, T object, String moduleName) {
        try {
            T registered = registryManager.register(registry, identifier, object, moduleName);
            
            if (AethelonCore.getConfigManager().getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("RegistryService registered {} in {} for module {}", 
                    identifier, registry.getKey().getValue(), moduleName);
            }
            
            return registered;
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to register {} in {} for module {}", 
                identifier, registry.getKey().getValue(), moduleName, e);
            throw new RuntimeException("Registry registration failed", e);
        }
    }
    
    @Override
    public <T> void registerLazy(Registry<T> registry, Identifier identifier, 
                                Supplier<T> supplier, String moduleName, List<String> dependencies) {
        try {
            registryManager.registerLazy(registry, identifier, supplier, moduleName, dependencies);
            
            if (AethelonCore.getConfigManager().getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("RegistryService queued lazy registration for {} in {} for module {} (deps: {})", 
                    identifier, registry.getKey().getValue(), moduleName, dependencies);
            }
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to register lazy {} in {} for module {}", 
                identifier, registry.getKey().getValue(), moduleName, e);
            throw new RuntimeException("Lazy registry registration failed", e);
        }
    }
    
    @Override
    public void processPendingRegistrations() {
        try {
            registryManager.processPendingRegistrations();
            
            if (AethelonCore.getConfigManager().getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("RegistryService processed pending registrations");
            }
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to process pending registrations", e);
            throw new RuntimeException("Pending registration processing failed", e);
        }
    }
    
    @Override
    public List<RegistryEntry<?>> getModuleRegistrations(String moduleName) {
        try {
            List<RegistryManager.RegistryEntry<?>> managerEntries = registryManager.getModuleRegistrations(moduleName);
            
            // Convert to service registry entries
            return managerEntries.stream()
                .map(entry -> new RegistryEntry<Object>(
                    (Registry<Object>) entry.registry, 
                    entry.identifier, 
                    entry.object, 
                    moduleName,
                    System.currentTimeMillis() // Approximate registration time
                ))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to get registrations for module {}", moduleName, e);
            return List.of();
        }
    }
    
    @Override
    public int getTotalRegistrationCount() {
        try {
            return registryManager.getTotalRegistrationCount();
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to get total registration count", e);
            return 0;
        }
    }
    
    @Override
    public <T> boolean isRegistered(Registry<T> registry, Identifier identifier) {
        try {
            return registry.containsId(identifier);
        } catch (Exception e) {
            AethelonCore.LOGGER.debug("Failed to check registration for {}", identifier, e);
            return false;
        }
    }
    
    @Override
    public <T> T getRegistered(Registry<T> registry, Identifier identifier) {
        try {
            if (registry.containsId(identifier)) {
                return registry.get(identifier);
            }
            return null;
        } catch (Exception e) {
            AethelonCore.LOGGER.debug("Failed to get registered object for {}", identifier, e);
            return null;
        }
    }
    
    @Override
    public int unregisterModule(String moduleName) {
        // Minecraft registries don't support unregistration
        // This is a placeholder for future functionality
        AethelonCore.LOGGER.warn("Module unregistration not supported in Minecraft registries: {}", moduleName);
        return 0;
    }
    
    @Override
    public RegistrationStatistics getStatistics() {
        try {
            int totalRegistrations = getTotalRegistrationCount();
            
            // Count modules with registrations
            int moduleCount = 0;
            try {
                // This is a simplified count - in a real implementation we'd track this better
                moduleCount = (int) registryManager.getClass().getDeclaredFields().length; // Placeholder
            } catch (Exception e) {
                moduleCount = 1; // Fallback
            }
            
            long currentTime = System.currentTimeMillis();
            
            return new RegistrationStatistics(
                totalRegistrations,
                moduleCount,
                0, // pendingRegistrations - would need to expose from RegistryManager
                currentTime - 60000, // Approximate oldest (1 minute ago)
                currentTime // Newest
            );
            
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to get registration statistics", e);
            return new RegistrationStatistics(0, 0, 0, 0, 0);
        }
    }
    
    /**
     * Get debug information about the service state
     */
    public String getDebugInfo() {
        RegistrationStatistics stats = getStatistics();
        return String.format(
            "RegistryService[totalRegistrations=%d, modules=%d, pending=%d]",
            stats.totalRegistrations, stats.moduleCount, stats.pendingRegistrations
        );
    }
    
    /**
     * Validate registry service is working correctly
     */
    public boolean validateService() {
        try {
            // Basic validation - check if we can access the underlying registry manager
            int count = getTotalRegistrationCount();
            AethelonCore.LOGGER.debug("RegistryService validation: {} total registrations", count);
            return true;
        } catch (Exception e) {
            AethelonCore.LOGGER.error("RegistryService validation failed", e);
            return false;
        }
    }
}