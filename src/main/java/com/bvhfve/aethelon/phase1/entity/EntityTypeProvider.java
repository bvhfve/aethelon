package com.bvhfve.aethelon.phase1.entity;

import com.bvhfve.aethelon.core.AethelonCore;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * EntityTypeProvider - Provides EntityType creation and access for Aethelon entity
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: FabricEntityTypeBuilder for creating custom entity types
 * - Hooks into: Minecraft EntityType system for entity registration
 * - Modifies: Creates new EntityType with custom dimensions and spawn group
 * 
 * MODULE ROLE:
 * - Purpose: Centralize entity type creation and configuration
 * - Dependencies: None (utility class)
 * - Provides: EntityType instance, registry key, entity factory method
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+ (uses current EntityType API)
 * - Fabric API: 0.119.2+ (uses FabricEntityTypeBuilder)
 * - Breaking changes: EntityType API changes may require builder updates
 * 
 * ENTITY CONFIGURATION:
 * - SpawnGroup: WATER_CREATURE (enables natural aquatic behavior)
 * - Dimensions: 8x4 blocks (large enough for world turtle concept)
 * - Factory: AethelonEntity::new (creates entity instances)
 * - Registry: Uses proper RegistryKey for Fabric API compatibility
 */
public class EntityTypeProvider {
    
    /**
     * Registry key for the Aethelon entity type
     * Required by FabricEntityTypeBuilder.build() method
     */
    public static final RegistryKey<EntityType<?>> AETHELON_KEY = RegistryKey.of(
            RegistryKeys.ENTITY_TYPE, 
            Identifier.of(AethelonCore.MOD_ID, "aethelon")
    );
    
    /**
     * The Aethelon entity type instance
     * Initialized by createAethelonEntityType() and cached for reuse
     */
    private static EntityType<AethelonEntity> aethelonEntityType;
    
    /**
     * Create the Aethelon entity type with proper configuration
     * 
     * @return Configured EntityType for Aethelon entities
     * 
     * MINECRAFT CONTEXT:
     * - Called by: EntityModule during initialization
     * - Timing: During mod loading, before world generation
     * - Thread safety: Single-threaded during mod loading
     */
    public static EntityType<AethelonEntity> createAethelonEntityType() {
        if (aethelonEntityType == null) {
            aethelonEntityType = FabricEntityTypeBuilder
                    .create(SpawnGroup.WATER_CREATURE, AethelonEntity::new)
                    .dimensions(EntityDimensions.fixed(8.0f, 4.0f)) // Large turtle dimensions
                    .build(AETHELON_KEY);
            
            AethelonCore.LOGGER.debug("Created Aethelon entity type with dimensions 8x4 blocks");
        }
        
        return aethelonEntityType;
    }
    
    /**
     * Get the Aethelon entity type (must be created first)
     * 
     * @return The Aethelon EntityType instance
     * @throws IllegalStateException if entity type hasn't been created yet
     */
    public static EntityType<AethelonEntity> getAethelonEntityType() {
        if (aethelonEntityType == null) {
            throw new IllegalStateException("Aethelon entity type not created yet. Call createAethelonEntityType() first.");
        }
        return aethelonEntityType;
    }
    
    /**
     * Get the registry key for the Aethelon entity type
     * 
     * @return Registry key for entity type registration
     */
    public static RegistryKey<EntityType<?>> getRegistryKey() {
        return AETHELON_KEY;
    }
    
    /**
     * Get the identifier for the Aethelon entity type
     * 
     * @return Identifier used for registration
     */
    public static Identifier getIdentifier() {
        return AETHELON_KEY.getValue();
    }
}