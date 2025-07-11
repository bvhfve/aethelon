package com.bvhfve.aethelon.core.util;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.config.ConfigManager;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.di.ServiceRegistry;
import com.bvhfve.aethelon.core.registry.RegistryManager;
import com.bvhfve.aethelon.core.services.ConfigService;

import java.util.*;

/**
 * EnhancedModuleLoader - Module loader with dependency injection support
 * 
 * This enhanced version of ModuleLoader adds dependency injection capabilities
 * while maintaining backward compatibility with existing modules.
 * 
 * FEATURES:
 * - Dependency injection for modules that support it
 * - Service registration and lifecycle management
 * - Enhanced dependency resolution with service validation
 * - Backward compatibility with legacy modules
 * - Comprehensive debugging and error reporting
 * 
 * MIGRATION STRATEGY:
 * - DI modules are now the default architecture
 * - Legacy modules require explicit enabling with deprecation warnings
 * - Gradual migration path with clear guidance
 * - Complete removal of legacy patterns in Phase 3
 */
public class EnhancedModuleLoader {
    
    private final ConfigManager configManager;
    private final RegistryManager registryManager;
    private final DependencyInjectionContainer diContainer;
    private final ServiceRegistry serviceRegistry;
    private final ConfigService configService;
    
    private final Map<String, AethelonModule> loadedModules = new HashMap<>();
    private final List<String> loadOrder = new ArrayList<>();
    private final Set<String> diEnabledModules = new HashSet<>();
    
    public EnhancedModuleLoader(ConfigManager configManager, RegistryManager registryManager) {
        this.configManager = configManager;
        this.registryManager = registryManager;
        this.diContainer = new DependencyInjectionContainer();
        this.serviceRegistry = new ServiceRegistry(diContainer);
        
        // Register core services
        serviceRegistry.registerCoreServices();
        
        // Get ConfigService for modern configuration access
        this.configService = serviceRegistry.getService(ConfigService.class, "core");
    }
    
    /**
     * Load all enabled modules with dependency injection support
     */
    public void loadEnabledModules() {
        AethelonCore.LOGGER.info("Loading enabled modules with DI support...");
        
        try {
            // Define available modules (including DI versions)
            Map<String, String> availableModules = getAvailableModules();
            
            // Determine which modules should be loaded
            Set<String> enabledModules = getEnabledModules(availableModules.keySet());
            
            if (configManager.getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("Enabled modules: {}", enabledModules);
                AethelonCore.LOGGER.debug("DI container statistics: {}", serviceRegistry.getStatistics());
            }
            
            // Determine which mode we're running in
            boolean forceLegacy = false;
            try {
                forceLegacy = configService.getConfigValue("legacy.forceLegacyModules", false, Boolean.class);
            } catch (Exception e) {
                // Default to DI mode if config access fails
            }
            AethelonCore.LOGGER.info("Module loading mode: {} (Legacy forced: {})", 
                forceLegacy ? "Legacy (DEPRECATED)" : "Dependency Injection", forceLegacy);
            
            // Load modules in dependency order with DI support
            loadModulesInOrder(enabledModules, availableModules);
            
            // Process any pending registrations
            registryManager.processPendingRegistrations();
            
            AethelonCore.LOGGER.info("Successfully loaded {} modules (DI: {}, Legacy: {}): {}", 
                loadedModules.size(), diEnabledModules.size(), 
                loadedModules.size() - diEnabledModules.size(), loadOrder);
                
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to load modules with DI support", e);
            throw new RuntimeException("Enhanced module loading failed", e);
        }
    }
    
    /**
     * Get available modules including DI versions
     */
    private Map<String, String> getAvailableModules() {
        Map<String, String> modules = new HashMap<>();
        
        // Phase 1 modules (original)
        modules.put("phase1", "com.bvhfve.aethelon.phase1.Phase1Module");
        modules.put("phase1.entity", "com.bvhfve.aethelon.phase1.entity.EntityModule");
        modules.put("phase1.client", "com.bvhfve.aethelon.phase1.client.ClientModule");
        modules.put("phase1.spawn", "com.bvhfve.aethelon.phase1.spawn.SpawnModule");
        
        // Phase 1 modules (DI versions) - NOW DEFAULT
        modules.put("phase1.di", "com.bvhfve.aethelon.phase1.Phase1ModuleDI");
        modules.put("phase1.entity.di", "com.bvhfve.aethelon.phase1.entity.EntityModuleDI");
        modules.put("phase1.spawn.di", "com.bvhfve.aethelon.phase1.spawn.SpawnModuleDI");
        modules.put("phase1.client.di", "com.bvhfve.aethelon.phase1.client.ClientModuleDI");
        
        // Future phase modules
        modules.put("phase2.ai", "com.bvhfve.aethelon.phase2.ai.AIModule");
        modules.put("phase2.state", "com.bvhfve.aethelon.phase2.state.StateModule");
        modules.put("phase2.pathfinding", "com.bvhfve.aethelon.phase2.pathfinding.PathfindingModule");
        modules.put("phase2", "com.bvhfve.aethelon.phase2.Phase2Module");
        
        return modules;
    }
    
    /**
     * Determine enabled modules
     */
    private Set<String> getEnabledModules(Set<String> availableModules) {
        Set<String> enabled = new HashSet<>();
        
        for (String moduleName : availableModules) {
            if (shouldLoadModule(moduleName)) {
                enabled.add(moduleName);
            }
        }
        
        return enabled;
    }
    
    /**
     * Check if module should be loaded
     */
    private boolean shouldLoadModule(String moduleName) {
        String[] parts = moduleName.split("\\.");
        
        if (parts.length == 1) {
            // Phase-level module
            return configManager.isPhaseEnabled(parts[0]);
        } else if (parts.length >= 2) {
            // Sub-module
            String phase = parts[0];
            String module = parts[1];
            
            // NEW: DI modules are now the default, legacy requires explicit enabling
            boolean forceLegacy = false;
            try {
                forceLegacy = configService.getConfigValue("legacy.forceLegacyModules", false, Boolean.class);
            } catch (Exception e) {
                // Default to DI mode if config access fails
            }
            
            // Special handling for DI modules
            if (parts.length == 3 && "di".equals(parts[2])) {
                // DI module - load by default unless legacy is forced
                boolean baseEnabled = configManager.isModuleEnabled(phase, module);
                if (!forceLegacy && baseEnabled) {
                    AethelonCore.LOGGER.info("Loading DI module: {} (default mode)", moduleName);
                    return true;
                } else {
                    AethelonCore.LOGGER.debug("Skipping DI module: {} (legacy forced: {})", 
                        moduleName, forceLegacy);
                    return false;
                }
            } else {
                // Legacy module - only load if explicitly forced
                boolean baseEnabled = configManager.isModuleEnabled(phase, module);
                if (forceLegacy && baseEnabled) {
                    logLegacyDeprecationWarning(moduleName);
                    return true;
                } else {
                    AethelonCore.LOGGER.debug("Skipping legacy module: {} (DI is default)", moduleName);
                    return false;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Load modules in dependency order with DI support
     */
    private void loadModulesInOrder(Set<String> enabledModules, Map<String, String> moduleClasses) {
        Set<String> remaining = new HashSet<>(enabledModules);
        Set<String> processed = new HashSet<>();
        int iteration = 0;
        
        if (configManager.getDebugConfig().logModuleLoading) {
            AethelonCore.LOGGER.debug("Starting enhanced dependency resolution for modules: {}", enabledModules);
        }
        
        while (!remaining.isEmpty()) {
            iteration++;
            Set<String> readyToLoad = new HashSet<>();
            
            if (configManager.getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("Enhanced dependency resolution iteration {}: remaining={}, processed={}", 
                    iteration, remaining, processed);
            }
            
            for (String moduleName : remaining) {
                if (canLoadModuleWithDI(moduleName, processed, moduleClasses)) {
                    readyToLoad.add(moduleName);
                }
            }
            
            if (readyToLoad.isEmpty()) {
                AethelonCore.LOGGER.error("Circular dependency detected in enhanced modules: {}", remaining);
                break;
            }
            
            // Load ready modules
            for (String moduleName : readyToLoad) {
                loadModuleWithDI(moduleName, moduleClasses.get(moduleName));
                processed.add(moduleName);
                remaining.remove(moduleName);
            }
        }
        
        if (configManager.getDebugConfig().logModuleLoading) {
            AethelonCore.LOGGER.debug("Enhanced dependency resolution completed in {} iterations", iteration);
            AethelonCore.LOGGER.debug("Final load order: {}", loadOrder);
        }
    }
    
    /**
     * Check if module can be loaded with DI validation
     */
    private boolean canLoadModuleWithDI(String moduleName, Set<String> loadedModules, Map<String, String> moduleClasses) {
        try {
            String className = moduleClasses.get(moduleName);
            if (className != null) {
                Class<?> moduleClass = Class.forName(className);
                Object moduleInstance = moduleClass.getDeclaredConstructor().newInstance();
                
                if (moduleInstance instanceof InjectableAethelonModule) {
                    InjectableAethelonModule diModule = (InjectableAethelonModule) moduleInstance;
                    
                    // Check traditional dependencies
                    List<String> dependencies = diModule.getDependencies();
                    for (String dependency : dependencies) {
                        if (!loadedModules.contains(dependency)) {
                            return false;
                        }
                    }
                    
                    // Check service dependencies
                    if (!diModule.validateDependencies(serviceRegistry)) {
                        return false;
                    }
                    
                    return true;
                } else if (moduleInstance instanceof AethelonModule) {
                    // Legacy module - use original dependency checking
                    AethelonModule module = (AethelonModule) moduleInstance;
                    List<String> dependencies = module.getDependencies();
                    
                    for (String dependency : dependencies) {
                        if (!loadedModules.contains(dependency)) {
                            return false;
                        }
                    }
                    
                    return true;
                }
            }
        } catch (Exception e) {
            if (configManager.getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("Failed to check dependencies for module '{}': {}", 
                    moduleName, e.getMessage());
            }
        }
        
        return true; // Fallback for unknown modules
    }
    
    /**
     * Load module with DI support
     */
    private void loadModuleWithDI(String moduleName, String className) {
        try {
            if (configManager.getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("Loading module with DI: {} ({})", moduleName, className);
            }
            
            Class<?> moduleClass = Class.forName(className);
            Object moduleInstance = moduleClass.getDeclaredConstructor().newInstance();
            
            if (!(moduleInstance instanceof AethelonModule)) {
                throw new IllegalStateException("Module " + className + " does not implement AethelonModule");
            }
            
            AethelonModule module = (AethelonModule) moduleInstance;
            
            // Handle DI-enabled modules
            if (module instanceof InjectableAethelonModule) {
                InjectableAethelonModule diModule = (InjectableAethelonModule) module;
                
                // Inject dependencies
                diModule.injectDependencies(serviceRegistry);
                
                // Initialize with DI
                diModule.initializeWithDI(serviceRegistry);
                diEnabledModules.add(moduleName);
                
                AethelonCore.LOGGER.debug("Loaded DI-enabled module: {}", moduleName);
            } else {
                // Legacy module
                module.initialize();
                AethelonCore.LOGGER.debug("Loaded legacy module: {}", moduleName);
            }
            
            // Track the loaded module
            loadedModules.put(moduleName, module);
            loadOrder.add(moduleName);
            
            AethelonCore.LOGGER.info("Loaded module: {}", moduleName);
            
        } catch (ClassNotFoundException e) {
            AethelonCore.LOGGER.warn("Module class not found: {} ({}). This may be expected if the phase is not implemented yet.", 
                moduleName, className);
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to load module: {} ({})", moduleName, className, e);
        }
    }
    
    /**
     * Log comprehensive legacy deprecation warning
     */
    private void logLegacyDeprecationWarning(String moduleName) {
        AethelonCore.LOGGER.warn("=================================================================");
        AethelonCore.LOGGER.warn("                    DEPRECATION WARNING                         ");
        AethelonCore.LOGGER.warn("=================================================================");
        AethelonCore.LOGGER.warn("Module '{}' uses DEPRECATED static dependencies", moduleName);
        AethelonCore.LOGGER.warn("");
        AethelonCore.LOGGER.warn("RECOMMENDED ACTION:");
        AethelonCore.LOGGER.warn("• Remove 'legacy.forceLegacyModules: true' from config");
        AethelonCore.LOGGER.warn("• Use modern DI modules for better reliability");
        AethelonCore.LOGGER.warn("");
        AethelonCore.LOGGER.warn("MIGRATION SUPPORT:");
        AethelonCore.LOGGER.warn("• See DEPENDENCY_INJECTION_MIGRATION_GUIDE.md");
        AethelonCore.LOGGER.warn("• Legacy modules will be removed in Phase 3");
        AethelonCore.LOGGER.warn("=================================================================");
    }
    
    /**
     * Get service registry for external access
     */
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
    
    /**
     * Get DI container for external access
     */
    public DependencyInjectionContainer getDIContainer() {
        return diContainer;
    }
    
    /**
     * Shutdown all modules with DI support
     */
    public void shutdownAllModules() {
        AethelonCore.LOGGER.info("Shutting down all modules with DI support...");
        
        // Shutdown in reverse order
        List<String> reverseOrder = new ArrayList<>(loadOrder);
        Collections.reverse(reverseOrder);
        
        for (String moduleName : reverseOrder) {
            AethelonModule module = loadedModules.get(moduleName);
            if (module != null) {
                try {
                    if (module instanceof InjectableAethelonModule) {
                        ((InjectableAethelonModule) module).shutdownWithDI(serviceRegistry);
                    } else {
                        module.shutdown();
                    }
                    AethelonCore.LOGGER.debug("Shutdown module: {}", moduleName);
                } catch (Exception e) {
                    AethelonCore.LOGGER.error("Error shutting down module: {}", moduleName, e);
                }
            }
        }
        
        // Cleanup DI container
        serviceRegistry.shutdown();
        
        loadedModules.clear();
        loadOrder.clear();
        diEnabledModules.clear();
        
        AethelonCore.LOGGER.info("All modules shutdown complete (enhanced)");
    }
}