package com.bvhfve.aethelon.phase1.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

/**
 * EntityTypeService - Service interface for entity type management
 * 
 * This interface abstracts entity type creation and access, replacing the static
 * EntityTypeProvider pattern with a proper service-oriented approach.
 * 
 * BENEFITS OF SERVICE PATTERN:
 * - Dependency injection instead of static access
 * - Testable with mock implementations
 * - Clear lifecycle management
 * - Thread-safe initialization
 * - Proper error handling
 * 
 * USAGE PATTERNS:
 * - Inject into modules that need entity types
 * - Use for entity registration and spawning
 * - Access entity metadata and configuration
 * - Handle entity type lifecycle events
 */
public interface EntityTypeService {
    
    /**
     * Create and register the Aethelon entity type
     * This method should be called once during module initialization
     * 
     * @return The created EntityType instance
     * @throws IllegalStateException if entity type already exists
     */
    EntityType<AethelonEntity> createAethelonEntityType();
    
    /**
     * Get the Aethelon entity type
     * 
     * @return The EntityType instance
     * @throws IllegalStateException if entity type hasn't been created yet
     */
    EntityType<AethelonEntity> getAethelonEntityType();
    
    /**
     * Check if the Aethelon entity type has been created
     * 
     * @return true if entity type exists, false otherwise
     */
    boolean isEntityTypeCreated();
    
    /**
     * Get the registry key for the Aethelon entity type
     * 
     * @return Registry key for entity type registration
     */
    RegistryKey<EntityType<?>> getRegistryKey();
    
    /**
     * Get the identifier for the Aethelon entity type
     * 
     * @return Identifier used for registration
     */
    Identifier getIdentifier();
    
    /**
     * Create a new Aethelon entity instance
     * 
     * @param world The world to create the entity in
     * @return New AethelonEntity instance
     * @throws IllegalStateException if entity type hasn't been created yet
     */
    AethelonEntity createEntity(net.minecraft.world.World world);
    
    /**
     * Get entity type configuration
     * 
     * @return Configuration object for entity type
     */
    EntityTypeConfiguration getConfiguration();
    
    /**
     * Update entity type configuration (if supported)
     * 
     * @param configuration New configuration
     * @throws UnsupportedOperationException if runtime configuration changes not supported
     */
    void updateConfiguration(EntityTypeConfiguration configuration);
    
    /**
     * Configuration data for entity type
     */
    class EntityTypeConfiguration {
        public final float width;
        public final float height;
        public final boolean fireImmune;
        public final boolean canSpawnFarFromPlayer;
        public final int maxTrackingRange;
        public final int trackingTickInterval;
        
        public EntityTypeConfiguration(float width, float height, boolean fireImmune, 
                                     boolean canSpawnFarFromPlayer, int maxTrackingRange, 
                                     int trackingTickInterval) {
            this.width = width;
            this.height = height;
            this.fireImmune = fireImmune;
            this.canSpawnFarFromPlayer = canSpawnFarFromPlayer;
            this.maxTrackingRange = maxTrackingRange;
            this.trackingTickInterval = trackingTickInterval;
        }
        
        public static EntityTypeConfiguration getDefault() {
            return new EntityTypeConfiguration(
                8.0f,    // width - large turtle
                4.0f,    // height - appropriate for world turtle
                false,   // not fire immune
                true,    // can spawn far from player
                256,     // max tracking range
                1        // tracking tick interval
            );
        }
    }
}