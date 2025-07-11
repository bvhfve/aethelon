package com.bvhfve.aethelon.core.services;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.config.ConfigAdapter;
import com.bvhfve.aethelon.core.config.ConfigManager;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;

import java.util.Set;

/**
 * ConfigServiceImpl - Implementation of ConfigService using existing configuration system
 * 
 * This implementation wraps the existing ConfigManager and provides a clean service interface
 * while maintaining backward compatibility during the migration period.
 * 
 * MIGRATION STRATEGY:
 * - Phase 2.1: Wrap existing ConfigManager
 * - Phase 2.2: Gradually replace static access with service injection
 * - Phase 2.3: Optimize and enhance configuration system
 * - Phase 3.0: Remove legacy ConfigManager dependency
 */
@DependencyInjectionContainer.Service(scope = DependencyInjectionContainer.Scope.SINGLETON)
public class ConfigServiceImpl implements ConfigService {
    
    private final ConfigManager configManager;
    private final ConfigAdapter configAdapter;
    private final DebugConfigImpl debugConfig;
    
    /**
     * Constructor for dependency injection
     * Initially wraps existing ConfigManager, will be enhanced in future phases
     */
    @DependencyInjectionContainer.Inject
    public ConfigServiceImpl() {
        // For now, use the existing static ConfigManager
        // In Phase 3, this will be replaced with proper DI
        this.configManager = AethelonCore.getConfigManager();
        this.configAdapter = new ConfigAdapter();
        this.debugConfig = new DebugConfigImpl();
        
        AethelonCore.LOGGER.debug("ConfigService initialized with existing ConfigManager wrapper");
    }
    
    @Override
    public boolean isPhaseEnabled(String phaseName) {
        try {
            return configManager.isPhaseEnabled(phaseName);
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to check if phase '{}' is enabled, defaulting to false", phaseName, e);
            return false;
        }
    }
    
    @Override
    public boolean isModuleEnabled(String phaseName, String moduleName) {
        try {
            return configManager.isModuleEnabled(phaseName, moduleName);
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to check if module '{}.{}' is enabled, defaulting to false", 
                phaseName, moduleName, e);
            return false;
        }
    }
    
    @Override
    public <T> T getPhaseConfig(String phaseName, Class<T> configClass) {
        try {
            return configManager.getPhaseConfig(phaseName, configClass);
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to get phase config for '{}', returning null", phaseName, e);
            return null;
        }
    }
    
    @Override
    public DebugConfig getDebugConfig() {
        return debugConfig;
    }
    
    @Override
    public String getConfigSummary() {
        try {
            return configManager.getConfigSummary();
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to get config summary", e);
            return "Configuration summary unavailable: " + e.getMessage();
        }
    }
    
    @Override
    public Set<String> getAvailablePhases() {
        try {
            return configManager.getAvailablePhases();
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to get available phases", e);
            return Set.of("phase1"); // Fallback to known phases
        }
    }
    
    @Override
    public boolean isConfigurationReady() {
        try {
            // Check if the underlying configuration system is ready
            return configManager != null && 
                   com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE != null;
        } catch (Exception e) {
            AethelonCore.LOGGER.debug("Configuration readiness check failed", e);
            return false;
        }
    }
    
    @Override
    public <T> T getConfigValue(String path, T defaultValue, Class<T> valueClass) {
        try {
            // Parse path and navigate configuration structure
            String[] pathParts = path.split("\\.");
            
            if (pathParts.length >= 2 && "phases".equals(pathParts[0])) {
                String phaseName = pathParts[1];
                
                // Handle phase-level configuration access
                if (pathParts.length == 3) {
                    return getPhaseConfigValue(phaseName, pathParts[2], defaultValue, valueClass);
                } else if (pathParts.length == 4) {
                    return getModuleConfigValue(phaseName, pathParts[2], pathParts[3], defaultValue, valueClass);
                }
            }
            
            AethelonCore.LOGGER.debug("Unsupported config path: {}, using default value", path);
            return defaultValue;
            
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to get config value for path '{}', using default", path, e);
            return defaultValue;
        }
    }
    
    /**
     * Get phase-level configuration value
     */
    @SuppressWarnings("unchecked")
    private <T> T getPhaseConfigValue(String phaseName, String property, T defaultValue, Class<T> valueClass) {
        try {
            if ("phase1".equals(phaseName)) {
                var config = com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE;
                if (config != null && config.phases != null && config.phases.phase1 != null) {
                    switch (property) {
                        case "enableNaturalSpawning":
                            if (valueClass == Boolean.class) {
                                return (T) Boolean.valueOf(config.phases.phase1.enableNaturalSpawning);
                            }
                            break;
                        case "spawnWeight":
                            if (valueClass == Integer.class) {
                                return (T) Integer.valueOf(config.phases.phase1.spawnWeight);
                            }
                            break;
                        case "minGroupSize":
                            if (valueClass == Integer.class) {
                                return (T) Integer.valueOf(config.phases.phase1.minGroupSize);
                            }
                            break;
                        case "maxGroupSize":
                            if (valueClass == Integer.class) {
                                return (T) Integer.valueOf(config.phases.phase1.maxGroupSize);
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            AethelonCore.LOGGER.debug("Failed to get phase config value: {}.{}", phaseName, property, e);
        }
        
        return defaultValue;
    }
    
    /**
     * Get module-level configuration value
     */
    private <T> T getModuleConfigValue(String phaseName, String moduleName, String property, T defaultValue, Class<T> valueClass) {
        // Future implementation for module-specific configuration
        AethelonCore.LOGGER.debug("Module-level config access not yet implemented: {}.{}.{}", 
            phaseName, moduleName, property);
        return defaultValue;
    }
    
    /**
     * Implementation of DebugConfig interface
     */
    private class DebugConfigImpl implements DebugConfig {
        
        @Override
        public boolean isModuleLoadingEnabled() {
            try {
                return configManager.getDebugConfig().logModuleLoading;
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean isConfigSummaryEnabled() {
            try {
                return configManager.getDebugConfig().logModuleLoading; // Use existing field
            } catch (Exception e) {
                return true; // Default to enabled for important logging
            }
        }
        
        @Override
        public boolean isHotReloadEnabled() {
            try {
                return false; // Not implemented yet
            } catch (Exception e) {
                return false; // Default to disabled for safety
            }
        }
        
        @Override
        public boolean isStateParticlesEnabled() {
            try {
                return false; // Not implemented yet
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean isVerboseLoggingEnabled() {
            try {
                return configManager.getDebugConfig().logModuleLoading; // Use existing field
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean isDevCommandsEnabled() {
            try {
                return false; // Not implemented yet
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean isEntitySpawningEnabled() {
            try {
                return false; // Not implemented yet
            } catch (Exception e) {
                return false;
            }
        }
        
        @Override
        public boolean isPhaseTransitionsEnabled() {
            try {
                return false; // Not implemented yet
            } catch (Exception e) {
                return false;
            }
        }
    }
}