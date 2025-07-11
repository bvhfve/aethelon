package com.bvhfve.aethelon.phase1.spawn;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.util.AethelonModule;
import com.bvhfve.aethelon.phase1.entity.EntityTypeProvider;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;

import java.util.List;

/**
 * SpawnModule - Handles Aethelon entity spawning and spawn eggs
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: Fabric BiomeModifications API for natural spawning
 * - Hooks into: Biome spawn lists, item registration, creative tabs
 * - Modifies: Adds Aethelon spawning to ocean biomes, adds spawn egg item
 * 
 * MODULE ROLE:
 * - Purpose: Configure natural spawning and provide spawn egg for testing
 * - Dependencies: phase1, phase1.entity (needs entity type)
 * - Provides: Natural spawning in deep oceans, spawn egg for creative/testing
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+ (uses current biome and spawning APIs)
 * - Fabric API: 0.119.2+ (uses Fabric biome modification API)
 * - Breaking changes: Biome API changes may require spawn configuration updates
 * 
 * SPAWNING DESIGN:
 * The Aethelon entity spawning is designed for rarity and appropriate locations:
 * - Deep ocean biomes only (appropriate for world turtle concept)
 * - Very rare spawning (1-2 per world region to maintain uniqueness)
 * - Spawn conditions: deep water, away from players, specific depth requirements
 * - Spawn egg available for creative mode and testing purposes
 */
public class SpawnModule implements AethelonModule {
    
    // Spawn egg item for Aethelon entity
    private static SpawnEggItem aethelonSpawnEgg;
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase1.spawn";
    }
    
    @Override
    public String getPhase() {
        return "phase1";
    }
    
    @Override
    public boolean isEnabled() {
        return AethelonCore.getConfigManager().isModuleEnabled("phase1", "spawn");
    }
    
    @Override
    public void initialize() throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase 1 Spawn module is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase 1 Spawn module");
        
        try {
            // Register spawn egg item
            registerSpawnEgg();
            
            // Configure natural spawning
            configureNaturalSpawning();
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase 1 Spawn module initialization complete");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase 1 Spawn module", e);
            throw e;
        }
    }
    
    /**
     * Register the Aethelon spawn egg item
     */
    private void registerSpawnEgg() {
        // Create spawn egg with appropriate colors
        aethelonSpawnEgg = new SpawnEggItem(
            EntityTypeProvider.getAethelonEntityType(),
            new net.minecraft.item.Item.Settings()
        );
        
        // Register the spawn egg
        AethelonCore.getRegistryManager().register(
            Registries.ITEM,
            Identifier.of(AethelonCore.MOD_ID, "aethelon_spawn_egg"),
            aethelonSpawnEgg,
            getModuleName()
        );
        
        // Add to creative inventory
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.addAfter(Items.TURTLE_SPAWN_EGG, aethelonSpawnEgg);
        });
        
        AethelonCore.LOGGER.debug("Registered Aethelon spawn egg");
    }
    
    /**
     * Configure natural spawning for Aethelon entities
     */
    private void configureNaturalSpawning() {
        // Check if natural spawning is enabled
        if (com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE != null && 
            com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE.phases.phase1.enableNaturalSpawning) {
            // Add spawning to deep ocean biomes
            BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                    BiomeKeys.DEEP_OCEAN,
                    BiomeKeys.DEEP_COLD_OCEAN,
                    BiomeKeys.DEEP_FROZEN_OCEAN,
                    BiomeKeys.DEEP_LUKEWARM_OCEAN
                ),
                SpawnGroup.WATER_CREATURE,
                EntityTypeProvider.getAethelonEntityType(),
                com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE.phases.phase1.spawnWeight,
                com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE.phases.phase1.minGroupSize,
                com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE.phases.phase1.maxGroupSize
            );
            
            AethelonCore.LOGGER.debug("Configured Aethelon natural spawning in deep ocean biomes");
            AethelonCore.LOGGER.debug("Spawn weight: {}, Group size: {}-{}", 
                com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE.phases.phase1.spawnWeight,
                com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE.phases.phase1.minGroupSize,
                com.bvhfve.aethelon.core.config.AethelonConfig.INSTANCE.phases.phase1.maxGroupSize);
        } else {
            AethelonCore.LOGGER.info("Natural spawning disabled in configuration");
        }
    }
    
    @Override
    public void shutdown() throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase 1 Spawn module");
        
        // Spawn configurations and items cannot be unregistered
        // This is why hot reload is not supported for this module
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase 1 Spawn module shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // Depends on main Phase 1 module and entity module
        return List.of("phase1", "phase1.entity");
    }
    
    @Override
    public boolean isCompatibleWith(String minecraftVersion) {
        // Compatible with Minecraft versions that support current spawning APIs
        return minecraftVersion.startsWith("1.21.4") || 
               minecraftVersion.startsWith("1.21.5") ||
               minecraftVersion.startsWith("1.22");
    }
    
    @Override
    public String getRequiredFabricApiVersion() {
        return "0.119.2";
    }
    
    @Override
    public String getDescription() {
        return "Spawn Management - Configures Aethelon natural spawning and provides spawn egg";
    }
    
    @Override
    public boolean supportsHotReload() {
        // Spawn configuration and item registration cannot be undone
        return false;
    }
    
    @Override
    public int getLoadPriority() {
        // Load after entity module, can be last in Phase 1
        return 40;
    }
    
    /**
     * Get the Aethelon spawn egg item
     * 
     * @return Spawn egg item instance
     */
    public static SpawnEggItem getSpawnEgg() {
        return aethelonSpawnEgg;
    }
}