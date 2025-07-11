package com.bvhfve.aethelon.phase1.entity;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.registry.RegistryManager;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * EntityTypeServiceImpl - Implementation of EntityTypeService using dependency injection
 * 
 * This implementation replaces the static EntityTypeProvider with a proper service
 * that can be injected into modules and properly managed through the DI container.
 * 
 * IMPROVEMENTS OVER STATIC APPROACH:
 * - Proper dependency injection of RegistryManager
 * - Thread-safe initialization with proper synchronization
 * - Clear error handling and state management
 * - Testable design with interface abstraction
 * - Configurable entity type parameters
 * 
 * LIFECYCLE:
 * 1. Service is created by DI container
 * 2. Dependencies are injected (RegistryManager)
 * 3. createAethelonEntityType() is called during module initialization
 * 4. getAethelonEntityType() can be safely called by dependent modules
 * 5. Service is cleaned up when module scope is destroyed
 */
@DependencyInjectionContainer.Service(scope = DependencyInjectionContainer.Scope.SINGLETON)
public class EntityTypeServiceImpl implements EntityTypeService {
    
    private final RegistryManager registryManager;
    private final EntityTypeConfiguration configuration;
    private final Object lock = new Object();
    
    private volatile EntityType<AethelonEntity> aethelonEntityType;
    private volatile boolean created = false;
    
    /**
     * Constructor with dependency injection
     */
    @DependencyInjectionContainer.Inject
    public EntityTypeServiceImpl(RegistryManager registryManager) {
        this.registryManager = registryManager;
        this.configuration = EntityTypeConfiguration.getDefault();
        
        AethelonCore.LOGGER.debug("EntityTypeService created with injected dependencies");
    }
    
    @Override
    public EntityType<AethelonEntity> createAethelonEntityType() {
        if (created) {
            throw new IllegalStateException("Aethelon entity type already created");
        }
        
        synchronized (lock) {
            if (created) {
                throw new IllegalStateException("Aethelon entity type already created");
            }
            
            try {
                // Create entity type using Fabric API
                FabricEntityTypeBuilder<AethelonEntity> builder = FabricEntityTypeBuilder
                        .create(SpawnGroup.WATER_CREATURE, AethelonEntity::new)
                        .dimensions(EntityDimensions.fixed(configuration.width, configuration.height))
                        .spawnableFarFromPlayer()
                        .trackRangeChunks(configuration.maxTrackingRange / 16)
                        .trackedUpdateRate(configuration.trackingTickInterval);
                
                // Add fire immunity if configured
                if (configuration.fireImmune) {
                    builder = builder.fireImmune();
                }
                
                aethelonEntityType = builder.build(getRegistryKey());
                
                // Register with the registry manager
                registryManager.register(
                    net.minecraft.registry.Registries.ENTITY_TYPE,
                    getIdentifier(),
                    aethelonEntityType,
                    "phase1.entity"
                );
                
                created = true;
                
                AethelonCore.LOGGER.info("Created Aethelon entity type: {} ({}x{} blocks)", 
                    getIdentifier(), configuration.width, configuration.height);
                
                return aethelonEntityType;
                
            } catch (Exception e) {
                AethelonCore.LOGGER.error("Failed to create Aethelon entity type", e);
                throw new RuntimeException("Entity type creation failed", e);
            }
        }
    }
    
    @Override
    public EntityType<AethelonEntity> getAethelonEntityType() {
        if (!created || aethelonEntityType == null) {
            throw new IllegalStateException("Aethelon entity type not created yet. Call createAethelonEntityType() first.");
        }
        return aethelonEntityType;
    }
    
    @Override
    public boolean isEntityTypeCreated() {
        return created && aethelonEntityType != null;
    }
    
    @Override
    public RegistryKey<EntityType<?>> getRegistryKey() {
        return RegistryKey.of(
            RegistryKeys.ENTITY_TYPE, 
            Identifier.of(AethelonCore.MOD_ID, "aethelon")
        );
    }
    
    @Override
    public Identifier getIdentifier() {
        return getRegistryKey().getValue();
    }
    
    @Override
    public AethelonEntity createEntity(World world) {
        EntityType<AethelonEntity> entityType = getAethelonEntityType();
        return new AethelonEntity(entityType, world);
    }
    
    @Override
    public EntityTypeConfiguration getConfiguration() {
        return configuration;
    }
    
    @Override
    public void updateConfiguration(EntityTypeConfiguration configuration) {
        throw new UnsupportedOperationException(
            "Runtime entity type configuration changes not supported. " +
            "Entity types must be configured during registration."
        );
    }
    
    /**
     * Get debug information about the service state
     */
    public String getDebugInfo() {
        return String.format(
            "EntityTypeService[created=%s, entityType=%s, config=%dx%d]",
            created,
            aethelonEntityType != null ? aethelonEntityType.toString() : "null",
            configuration.width,
            configuration.height
        );
    }
}