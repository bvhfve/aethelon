package com.bvhfve.aethelon.core.util;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.config.ConfigManager;
import com.bvhfve.aethelon.core.registry.RegistryManager;

import java.util.*;

/**
 * ModuleLoader - Dynamic loading and initialization of mod phases and modules
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None directly (coordinates other systems)
 * - Hooks into: Fabric mod loading lifecycle
 * - Modifies: None (coordination layer)
 * 
 * MODULE ROLE:
 * - Purpose: Load and initialize modules based on configuration and dependencies
 * - Dependencies: ConfigManager, RegistryManager
 * - Provides: Module lifecycle management, dependency resolution
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no direct Minecraft dependencies)
 * - Fabric API: Any (uses reflection for module loading)
 * - Breaking changes: Module interface changes may require updates
 */
public class ModuleLoader {
    
    private final ConfigManager configManager;
    private final RegistryManager registryManager;
    private final Map<String, AethelonModule> loadedModules = new HashMap<>();
    private final List<String> loadOrder = new ArrayList<>();
    
    public ModuleLoader(ConfigManager configManager, RegistryManager registryManager) {
        this.configManager = configManager;
        this.registryManager = registryManager;
    }
    
    /**
     * Load all enabled modules in dependency order
     * 
     * MINECRAFT CONTEXT:
     * - Called by: AethelonCore during mod initialization
     * - Timing: After core systems initialized, before world generation
     * - Thread safety: Single-threaded during mod loading
     */
    public void loadEnabledModules() {
        AethelonCore.LOGGER.info("Loading enabled modules...");
        
        // Define available modules and their classes
        Map<String, String> availableModules = getAvailableModules();
        
        // Determine which modules should be loaded based on configuration
        Set<String> enabledModules = getEnabledModules(availableModules.keySet());
        
        if (configManager.getDebugConfig().logModuleLoading) {
            AethelonCore.LOGGER.debug("Enabled modules: {}", enabledModules);
        }
        
        // Load modules in dependency order
        loadModulesInOrder(enabledModules, availableModules);
        
        // Process any pending registrations
        registryManager.processPendingRegistrations();
        
        AethelonCore.LOGGER.info("Successfully loaded {} modules: {}", 
                loadedModules.size(), loadOrder);
    }
    
    /**
     * Get map of available modules and their implementation classes
     * 
     * @return Map of module name to class name
     */
    private Map<String, String> getAvailableModules() {
        Map<String, String> modules = new HashMap<>();
        
        // Phase 1 modules
        modules.put("phase1.entity", "com.bvhfve.aethelon.phase1.entity.EntityModule");
        modules.put("phase1.client", "com.bvhfve.aethelon.phase1.client.ClientModule");
        modules.put("phase1.spawn", "com.bvhfve.aethelon.phase1.spawn.SpawnModule");
        modules.put("phase1", "com.bvhfve.aethelon.phase1.Phase1Module");
        
        // Phase 2 modules (future)
        modules.put("phase2.ai", "com.bvhfve.aethelon.phase2.ai.AIModule");
        modules.put("phase2.state", "com.bvhfve.aethelon.phase2.state.StateModule");
        modules.put("phase2.pathfinding", "com.bvhfve.aethelon.phase2.pathfinding.PathfindingModule");
        modules.put("phase2", "com.bvhfve.aethelon.phase2.Phase2Module");
        
        return modules;
    }
    
    /**
     * Determine which modules should be loaded based on configuration
     * 
     * @param availableModules Set of all available module names
     * @return Set of module names that should be loaded
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
     * Check if a specific module should be loaded based on configuration
     * 
     * @param moduleName Full module name (e.g., "phase1.entity", "phase1")
     * @return true if module should be loaded
     */
    private boolean shouldLoadModule(String moduleName) {
        String[] parts = moduleName.split("\\.");
        
        if (parts.length == 1) {
            // Phase-level module (e.g., "phase1")
            return configManager.isPhaseEnabled(parts[0]);
        } else if (parts.length == 2) {
            // Sub-module (e.g., "phase1.entity")
            String phase = parts[0];
            String module = parts[1];
            return configManager.isModuleEnabled(phase, module);
        }
        
        return false;
    }
    
    /**
     * Load modules in dependency order
     * 
     * @param enabledModules Set of modules to load
     * @param moduleClasses Map of module names to class names
     */
    private void loadModulesInOrder(Set<String> enabledModules, Map<String, String> moduleClasses) {
        Set<String> remaining = new HashSet<>(enabledModules);
        Set<String> processed = new HashSet<>();
        int iteration = 0;
        
        if (configManager.getDebugConfig().logModuleLoading) {
            AethelonCore.LOGGER.debug("Starting dependency resolution for modules: {}", enabledModules);
            // Log dependencies for each module
            for (String moduleName : enabledModules) {
                List<String> deps = getModuleDependencies(moduleName, moduleClasses);
                AethelonCore.LOGGER.debug("Module '{}' dependencies: {}", moduleName, deps);
            }
        }
        
        // Simple dependency resolution loop
        while (!remaining.isEmpty()) {
            iteration++;
            Set<String> readyToLoad = new HashSet<>();
            
            if (configManager.getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("Dependency resolution iteration {}: remaining={}, processed={}", 
                    iteration, remaining, processed);
            }
            
            for (String moduleName : remaining) {
                boolean canLoad = canLoadModule(moduleName, processed);
                if (configManager.getDebugConfig().logModuleLoading) {
                    List<String> deps = getModuleDependencies(moduleName, moduleClasses);
                    List<String> missingDeps = new ArrayList<>();
                    for (String dep : deps) {
                        if (!processed.contains(dep)) {
                            missingDeps.add(dep);
                        }
                    }
                    AethelonCore.LOGGER.debug("Module '{}' can load: {} (missing deps: {})", 
                        moduleName, canLoad, missingDeps);
                }
                
                if (canLoad) {
                    readyToLoad.add(moduleName);
                }
            }
            
            if (readyToLoad.isEmpty()) {
                AethelonCore.LOGGER.error("Circular dependency detected in modules: {}", remaining);
                AethelonCore.LOGGER.error("Processed modules: {}", processed);
                // Log detailed dependency information for debugging
                for (String moduleName : remaining) {
                    List<String> deps = getModuleDependencies(moduleName, moduleClasses);
                    AethelonCore.LOGGER.error("Stuck module '{}' dependencies: {}", moduleName, deps);
                }
                break;
            }
            
            if (configManager.getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("Ready to load in iteration {}: {}", iteration, readyToLoad);
            }
            
            // Load ready modules
            for (String moduleName : readyToLoad) {
                loadModule(moduleName, moduleClasses.get(moduleName));
                processed.add(moduleName);
                remaining.remove(moduleName);
            }
        }
        
        if (configManager.getDebugConfig().logModuleLoading) {
            AethelonCore.LOGGER.debug("Dependency resolution completed in {} iterations", iteration);
            AethelonCore.LOGGER.debug("Final load order: {}", loadOrder);
        }
    }
    
    /**
     * Check if a module can be loaded (all dependencies satisfied)
     * 
     * @param moduleName Name of the module to check
     * @param loadedModules Set of already loaded module names
     * @return true if module can be loaded now
     */
    private boolean canLoadModule(String moduleName, Set<String> loadedModules) {
        try {
            // Try to instantiate the module to check its dependencies
            Map<String, String> moduleClasses = getAvailableModules();
            String className = moduleClasses.get(moduleName);
            
            if (className != null) {
                Class<?> moduleClass = Class.forName(className);
                Object moduleInstance = moduleClass.getDeclaredConstructor().newInstance();
                
                if (moduleInstance instanceof AethelonModule) {
                    AethelonModule module = (AethelonModule) moduleInstance;
                    List<String> dependencies = module.getDependencies();
                    
                    // Check if all dependencies are loaded
                    for (String dependency : dependencies) {
                        if (!loadedModules.contains(dependency)) {
                            if (configManager.getDebugConfig().logModuleLoading) {
                                AethelonCore.LOGGER.debug("Module '{}' cannot load: missing dependency '{}'", 
                                    moduleName, dependency);
                            }
                            return false;
                        }
                    }
                    
                    if (configManager.getDebugConfig().logModuleLoading) {
                        AethelonCore.LOGGER.debug("Module '{}' dependencies satisfied: {}", 
                            moduleName, dependencies);
                    }
                }
            }
        } catch (Exception e) {
            if (configManager.getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("Failed to check dependencies for module '{}', using fallback: {}", 
                    moduleName, e.getMessage());
            }
            // If we can't instantiate the module, fall back to simple phase check
            if (moduleName.contains(".")) {
                String phase = moduleName.split("\\.")[0];
                return loadedModules.contains(phase);
            }
        }
        
        // Phase modules can be loaded immediately if enabled
        return true;
    }
    
    /**
     * Get the dependencies for a module without initializing it
     * 
     * @param moduleName Name of the module
     * @param moduleClasses Map of module names to class names
     * @return List of dependencies, or empty list if cannot determine
     */
    private List<String> getModuleDependencies(String moduleName, Map<String, String> moduleClasses) {
        try {
            String className = moduleClasses.get(moduleName);
            if (className != null) {
                Class<?> moduleClass = Class.forName(className);
                Object moduleInstance = moduleClass.getDeclaredConstructor().newInstance();
                
                if (moduleInstance instanceof AethelonModule) {
                    AethelonModule module = (AethelonModule) moduleInstance;
                    return module.getDependencies();
                }
            }
        } catch (Exception e) {
            // Ignore errors when getting dependencies for debugging
        }
        return new ArrayList<>();
    }
    
    /**
     * Load a single module by instantiating and initializing it
     * 
     * @param moduleName Name of the module
     * @param className Full class name of the module implementation
     */
    private void loadModule(String moduleName, String className) {
        try {
            if (configManager.getDebugConfig().logModuleLoading) {
                AethelonCore.LOGGER.debug("Loading module: {} ({})", moduleName, className);
            }
            
            // Load and instantiate the module class
            Class<?> moduleClass = Class.forName(className);
            Object moduleInstance = moduleClass.getDeclaredConstructor().newInstance();
            
            if (!(moduleInstance instanceof AethelonModule)) {
                throw new IllegalStateException("Module " + className + " does not implement AethelonModule");
            }
            
            AethelonModule module = (AethelonModule) moduleInstance;
            
            // Initialize the module
            module.initialize();
            
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
     * Get a loaded module by name
     * 
     * @param moduleName Name of the module
     * @return The module instance, or null if not loaded
     */
    public AethelonModule getModule(String moduleName) {
        return loadedModules.get(moduleName);
    }
    
    /**
     * Check if a module is loaded
     * 
     * @param moduleName Name of the module
     * @return true if module is loaded
     */
    public boolean isModuleLoaded(String moduleName) {
        return loadedModules.containsKey(moduleName);
    }
    
    /**
     * Get the number of loaded modules
     * 
     * @return Number of loaded modules
     */
    public int getLoadedModuleCount() {
        return loadedModules.size();
    }
    
    /**
     * Get the load order of modules
     * 
     * @return List of module names in load order
     */
    public List<String> getLoadOrder() {
        return new ArrayList<>(loadOrder);
    }
    
    /**
     * Shutdown all loaded modules (for cleanup/hot reload)
     */
    public void shutdownAllModules() {
        AethelonCore.LOGGER.info("Shutting down all modules...");
        
        // Shutdown in reverse order
        List<String> reverseOrder = new ArrayList<>(loadOrder);
        Collections.reverse(reverseOrder);
        
        for (String moduleName : reverseOrder) {
            AethelonModule module = loadedModules.get(moduleName);
            if (module != null) {
                try {
                    module.shutdown();
                    AethelonCore.LOGGER.debug("Shutdown module: {}", moduleName);
                } catch (Exception e) {
                    AethelonCore.LOGGER.error("Error shutting down module: {}", moduleName, e);
                }
            }
        }
        
        loadedModules.clear();
        loadOrder.clear();
        
        AethelonCore.LOGGER.info("All modules shutdown complete");
    }
}