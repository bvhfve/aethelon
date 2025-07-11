package com.bvhfve.aethelon.phase1.services;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.services.ConfigService;
import com.bvhfve.aethelon.core.services.RegistryService;
import com.bvhfve.aethelon.phase1.entity.AethelonEntity;
import com.bvhfve.aethelon.phase1.entity.EntityTypeService;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;

/**
 * SpawnServiceImpl - Implementation of SpawnService using dependency injection
 * 
 * This implementation replaces the spawn logic from SpawnModule with a clean,
 * testable service that properly manages dependencies and configuration.
 * 
 * IMPROVEMENTS OVER ORIGINAL:
 * - Dependency injection instead of static access
 * - Proper error handling and validation
 * - Configuration abstraction
 * - Thread-safe initialization
 * - Clear separation of concerns
 * 
 * DEPENDENCIES:
 * - EntityTypeService: For entity type access
 * - ConfigService: For configuration management
 * - RegistryService: For item registration
 */
@DependencyInjectionContainer.Service(scope = DependencyInjectionContainer.Scope.MODULE)
public class SpawnServiceImpl implements SpawnService {
    
    // Injected dependencies
    @DependencyInjectionContainer.Inject
    private EntityTypeService entityTypeService;
    
    @DependencyInjectionContainer.Inject
    private ConfigService configService;
    
    @DependencyInjectionContainer.Inject
    private RegistryService registryService;
    
    // Service state
    private SpawnEggItem spawnEgg;
    private SpawnConfiguration currentConfiguration;
    private boolean spawnEggRegistered = false;
    private boolean naturalSpawningConfigured = false;
    private final Object lock = new Object();
    
    /**
     * Constructor with dependency injection
     */
    @DependencyInjectionContainer.Inject
    public SpawnServiceImpl(EntityTypeService entityTypeService, 
                           ConfigService configService, 
                           RegistryService registryService) {
        this.entityTypeService = entityTypeService;
        this.configService = configService;
        this.registryService = registryService;
        this.currentConfiguration = loadConfigurationFromService();
        
        AethelonCore.LOGGER.debug("SpawnService initialized with configuration: {}", currentConfiguration);
    }
    
    @Override
    public void registerSpawnEgg() {
        if (!currentConfiguration.enableSpawnEgg) {
            AethelonCore.LOGGER.info("Spawn egg registration disabled in configuration");
            return;
        }
        
        synchronized (lock) {
            if (spawnEggRegistered) {
                AethelonCore.LOGGER.warn("Spawn egg already registered, skipping");
                return;
            }
            
            try {
                // Get entity type through service
                EntityType<AethelonEntity> entityType = entityTypeService.getAethelonEntityType();
                
                // Create spawn egg
                spawnEgg = new SpawnEggItem(entityType, new Item.Settings());
                
                // Register through registry service
                registryService.register(
                    Registries.ITEM,
                    Identifier.of(AethelonCore.MOD_ID, "aethelon_spawn_egg"),
                    spawnEgg,
                    "phase1.spawn"
                );
                
                // Add to creative inventory
                ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
                    content.addAfter(Items.TURTLE_SPAWN_EGG, spawnEgg);
                });
                
                spawnEggRegistered = true;
                AethelonCore.LOGGER.info("Successfully registered Aethelon spawn egg");
                
            } catch (Exception e) {
                AethelonCore.LOGGER.error("Failed to register spawn egg", e);
                throw new RuntimeException("Spawn egg registration failed", e);
            }
        }
    }
    
    @Override
    public void configureNaturalSpawning() {
        if (!currentConfiguration.naturalSpawningEnabled) {
            AethelonCore.LOGGER.info("Natural spawning disabled in configuration");
            return;
        }
        
        synchronized (lock) {
            if (naturalSpawningConfigured) {
                AethelonCore.LOGGER.warn("Natural spawning already configured, skipping");
                return;
            }
            
            try {
                // Get entity type through service
                EntityType<AethelonEntity> entityType = entityTypeService.getAethelonEntityType();
                
                // Configure biome spawning
                BiomeModifications.addSpawn(
                    BiomeSelectors.includeByKey(
                        BiomeKeys.DEEP_OCEAN,
                        BiomeKeys.DEEP_COLD_OCEAN,
                        BiomeKeys.DEEP_FROZEN_OCEAN,
                        BiomeKeys.DEEP_LUKEWARM_OCEAN
                    ),
                    SpawnGroup.WATER_CREATURE,
                    entityType,
                    currentConfiguration.spawnWeight,
                    currentConfiguration.minGroupSize,
                    currentConfiguration.maxGroupSize
                );
                
                naturalSpawningConfigured = true;
                AethelonCore.LOGGER.info("Successfully configured natural spawning: weight={}, groupSize={}-{}", 
                    currentConfiguration.spawnWeight, currentConfiguration.minGroupSize, currentConfiguration.maxGroupSize);
                
            } catch (Exception e) {
                AethelonCore.LOGGER.error("Failed to configure natural spawning", e);
                throw new RuntimeException("Natural spawning configuration failed", e);
            }
        }
    }
    
    @Override
    public SpawnEggItem getSpawnEgg() {
        return spawnEgg;
    }
    
    @Override
    public boolean isSpawnEggRegistered() {
        return spawnEggRegistered;
    }
    
    @Override
    public boolean isNaturalSpawningConfigured() {
        return naturalSpawningConfigured;
    }
    
    @Override
    public SpawnConfiguration getSpawnConfiguration() {
        return currentConfiguration;
    }
    
    @Override
    public void updateSpawnConfiguration(SpawnConfiguration configuration) {
        throw new UnsupportedOperationException(
            "Runtime spawn configuration updates not supported. " +
            "Spawn settings must be configured before module initialization."
        );
    }
    
    @Override
    public void setNaturalSpawningEnabled(boolean enabled) {
        throw new UnsupportedOperationException(
            "Runtime spawning enable/disable not supported. " +
            "Use configuration file to change spawn settings."
        );
    }
    
    @Override
    public void setSpawnWeight(int weight) {
        throw new UnsupportedOperationException(
            "Runtime spawn weight changes not supported. " +
            "Use configuration file to change spawn settings."
        );
    }
    
    @Override
    public void setGroupSize(int minSize, int maxSize) {
        throw new UnsupportedOperationException(
            "Runtime group size changes not supported. " +
            "Use configuration file to change spawn settings."
        );
    }
    
    /**
     * Load configuration from ConfigService
     */
    private SpawnConfiguration loadConfigurationFromService() {
        try {
            // Use ConfigService to safely access configuration
            boolean naturalSpawningEnabled = configService.getConfigValue(
                "phases.phase1.enableNaturalSpawning", false, Boolean.class);
            int spawnWeight = configService.getConfigValue(
                "phases.phase1.spawnWeight", 5, Integer.class);
            int minGroupSize = configService.getConfigValue(
                "phases.phase1.minGroupSize", 1, Integer.class);
            int maxGroupSize = configService.getConfigValue(
                "phases.phase1.maxGroupSize", 1, Integer.class);
            
            // Create configuration with loaded values
            return new SpawnConfiguration(
                naturalSpawningEnabled,
                spawnWeight,
                minGroupSize,
                maxGroupSize,
                SpawnConfiguration.getDefault().allowedBiomes, // Use default biomes
                SpawnConfiguration.getDefault().minWaterDepth,
                SpawnConfiguration.getDefault().maxWaterDepth,
                SpawnConfiguration.getDefault().requiresDeepOcean,
                true // enableSpawnEgg - always enabled for now
            );
            
        } catch (Exception e) {
            AethelonCore.LOGGER.warn("Failed to load spawn configuration, using defaults", e);
            return SpawnConfiguration.getDefault();
        }
    }
    
    /**
     * Get debug information about the service state
     */
    public String getDebugInfo() {
        return String.format(
            "SpawnService[spawnEggRegistered=%s, naturalSpawningConfigured=%s, config=%s]",
            spawnEggRegistered, naturalSpawningConfigured, currentConfiguration
        );
    }
}