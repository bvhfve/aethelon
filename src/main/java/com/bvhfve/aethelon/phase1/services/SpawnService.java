package com.bvhfve.aethelon.phase1.services;

import net.minecraft.item.SpawnEggItem;

/**
 * SpawnService - Service interface for spawn management
 * 
 * This interface encapsulates all spawn-related functionality, replacing the
 * spawn logic scattered throughout SpawnModule with a clean service contract.
 * 
 * REPLACES:
 * - SpawnModule.registerSpawnEgg()
 * - SpawnModule.configureNaturalSpawning()
 * - Static spawn egg access
 * - Direct configuration access for spawn settings
 * 
 * BENEFITS:
 * - Centralized spawn logic
 * - Easy testing with mocked dependencies
 * - Clear separation of concerns
 * - Configuration abstraction
 */
public interface SpawnService {
    
    /**
     * Register the Aethelon spawn egg item
     * Creates and registers the spawn egg with proper configuration
     * 
     * @throws IllegalStateException if entity type not available
     * @throws RuntimeException if registration fails
     */
    void registerSpawnEgg();
    
    /**
     * Configure natural spawning for Aethelon entities
     * Sets up biome modifications for natural entity spawning
     * 
     * @throws IllegalStateException if entity type not available
     */
    void configureNaturalSpawning();
    
    /**
     * Get the registered spawn egg item
     * 
     * @return Spawn egg item instance, or null if not registered yet
     */
    SpawnEggItem getSpawnEgg();
    
    /**
     * Check if spawn egg has been registered
     * 
     * @return true if spawn egg is registered and available
     */
    boolean isSpawnEggRegistered();
    
    /**
     * Check if natural spawning is configured
     * 
     * @return true if natural spawning has been set up
     */
    boolean isNaturalSpawningConfigured();
    
    /**
     * Get current spawn configuration
     * 
     * @return Current spawn configuration
     */
    SpawnConfiguration getSpawnConfiguration();
    
    /**
     * Update spawn configuration (if supported)
     * 
     * @param configuration New spawn configuration
     * @throws UnsupportedOperationException if runtime updates not supported
     */
    void updateSpawnConfiguration(SpawnConfiguration configuration);
    
    /**
     * Enable or disable natural spawning
     * 
     * @param enabled Whether natural spawning should be enabled
     */
    void setNaturalSpawningEnabled(boolean enabled);
    
    /**
     * Set spawn weight for natural spawning
     * 
     * @param weight Spawn weight (higher = more common)
     */
    void setSpawnWeight(int weight);
    
    /**
     * Set group size for natural spawning
     * 
     * @param minSize Minimum group size
     * @param maxSize Maximum group size
     */
    void setGroupSize(int minSize, int maxSize);
    
    /**
     * Configuration data for spawn settings
     */
    class SpawnConfiguration {
        public final boolean naturalSpawningEnabled;
        public final int spawnWeight;
        public final int minGroupSize;
        public final int maxGroupSize;
        public final String[] allowedBiomes;
        public final int minWaterDepth;
        public final int maxWaterDepth;
        public final boolean requiresDeepOcean;
        public final boolean enableSpawnEgg;
        
        public SpawnConfiguration(boolean naturalSpawningEnabled, int spawnWeight, 
                                int minGroupSize, int maxGroupSize, String[] allowedBiomes,
                                int minWaterDepth, int maxWaterDepth, boolean requiresDeepOcean,
                                boolean enableSpawnEgg) {
            this.naturalSpawningEnabled = naturalSpawningEnabled;
            this.spawnWeight = spawnWeight;
            this.minGroupSize = minGroupSize;
            this.maxGroupSize = maxGroupSize;
            this.allowedBiomes = allowedBiomes;
            this.minWaterDepth = minWaterDepth;
            this.maxWaterDepth = maxWaterDepth;
            this.requiresDeepOcean = requiresDeepOcean;
            this.enableSpawnEgg = enableSpawnEgg;
        }
        
        public static SpawnConfiguration getDefault() {
            return new SpawnConfiguration(
                false,  // naturalSpawningEnabled - disabled by default for safety
                5,      // spawnWeight - rare spawning
                1,      // minGroupSize - single entities
                1,      // maxGroupSize - single entities
                new String[]{
                    "minecraft:deep_ocean",
                    "minecraft:deep_cold_ocean",
                    "minecraft:deep_frozen_ocean",
                    "minecraft:deep_lukewarm_ocean"
                },
                10,     // minWaterDepth
                256,    // maxWaterDepth
                true,   // requiresDeepOcean
                true    // enableSpawnEgg
            );
        }
        
        @Override
        public String toString() {
            return String.format(
                "SpawnConfiguration[enabled=%s, weight=%d, groupSize=%d-%d, biomes=%d, depth=%d-%d, deepOcean=%s, spawnEgg=%s]",
                naturalSpawningEnabled, spawnWeight, minGroupSize, maxGroupSize,
                allowedBiomes.length, minWaterDepth, maxWaterDepth, requiresDeepOcean, enableSpawnEgg
            );
        }
    }
}