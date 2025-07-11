package com.bvhfve.aethelon.core.config;

import com.bvhfve.aethelon.core.AethelonCore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PhaseConfigRegistry - Registry for phase-specific configuration classes
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: None (pure configuration management)
 * - Hooks into: Configuration loading system
 * - Modifies: None (configuration coordination only)
 * 
 * MODULE ROLE:
 * - Purpose: Manage phase-specific configuration classes and validation
 * - Dependencies: None (core configuration system)
 * - Provides: Phase config registration, validation, and access
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: Any (no Minecraft dependencies)
 * - Fabric API: Any (no Fabric dependencies)
 * - Breaking changes: Config structure evolution may require migration
 * 
 * CONFIGURATION ARCHITECTURE:
 * This registry allows each phase to define its own configuration structure
 * while maintaining centralized configuration management. Each phase can
 * register its config class and provide validation logic.
 */
public class PhaseConfigRegistry {
    
    private final Map<String, PhaseConfigDefinition> phaseConfigs = new HashMap<>();
    
    /**
     * Register a phase configuration definition
     * 
     * @param phaseName Name of the phase (e.g., "phase1", "phase2")
     * @param definition Configuration definition for the phase
     */
    public void registerPhaseConfig(String phaseName, PhaseConfigDefinition definition) {
        phaseConfigs.put(phaseName, definition);
        AethelonCore.LOGGER.debug("Registered configuration for phase: {}", phaseName);
    }
    
    /**
     * Get all registered phase names
     * 
     * @return Set of registered phase names
     */
    public Set<String> getRegisteredPhases() {
        return phaseConfigs.keySet();
    }
    
    /**
     * Get configuration definition for a phase
     * 
     * @param phaseName Name of the phase
     * @return Configuration definition or null if not registered
     */
    public PhaseConfigDefinition getPhaseConfigDefinition(String phaseName) {
        return phaseConfigs.get(phaseName);
    }
    
    /**
     * Validate configuration for a specific phase
     * 
     * @param phaseName Name of the phase
     * @param config Configuration object to validate
     * @return true if configuration is valid, false otherwise
     */
    public boolean validatePhaseConfig(String phaseName, Object config) {
        PhaseConfigDefinition definition = phaseConfigs.get(phaseName);
        if (definition == null) {
            AethelonCore.LOGGER.warn("No configuration definition found for phase: {}", phaseName);
            return false;
        }
        
        try {
            return definition.validator.validate(config);
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to validate configuration for phase: {}", phaseName, e);
            return false;
        }
    }
    
    /**
     * Create default configuration for a phase
     * 
     * @param phaseName Name of the phase
     * @return Default configuration object or null if not registered
     */
    public Object createDefaultConfig(String phaseName) {
        PhaseConfigDefinition definition = phaseConfigs.get(phaseName);
        if (definition == null) {
            return null;
        }
        
        try {
            return definition.defaultFactory.create();
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to create default configuration for phase: {}", phaseName, e);
            return null;
        }
    }
    
    /**
     * Apply defaults to existing configuration
     * 
     * @param phaseName Name of the phase
     * @param config Configuration object to apply defaults to
     */
    public void applyDefaults(String phaseName, Object config) {
        PhaseConfigDefinition definition = phaseConfigs.get(phaseName);
        if (definition == null || config == null) {
            return;
        }
        
        try {
            definition.defaultApplier.applyDefaults(config);
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to apply defaults for phase: {}", phaseName, e);
        }
    }
    
    /**
     * Definition of a phase configuration
     */
    public static class PhaseConfigDefinition {
        public final Class<?> configClass;
        public final ConfigValidator validator;
        public final DefaultConfigFactory defaultFactory;
        public final DefaultConfigApplier defaultApplier;
        public final String description;
        
        public PhaseConfigDefinition(Class<?> configClass, 
                                   ConfigValidator validator,
                                   DefaultConfigFactory defaultFactory,
                                   DefaultConfigApplier defaultApplier,
                                   String description) {
            this.configClass = configClass;
            this.validator = validator;
            this.defaultFactory = defaultFactory;
            this.defaultApplier = defaultApplier;
            this.description = description;
        }
    }
    
    /**
     * Interface for validating phase configuration
     */
    @FunctionalInterface
    public interface ConfigValidator {
        boolean validate(Object config);
    }
    
    /**
     * Interface for creating default configuration
     */
    @FunctionalInterface
    public interface DefaultConfigFactory {
        Object create();
    }
    
    /**
     * Interface for applying defaults to existing configuration
     */
    @FunctionalInterface
    public interface DefaultConfigApplier {
        void applyDefaults(Object config);
    }
}