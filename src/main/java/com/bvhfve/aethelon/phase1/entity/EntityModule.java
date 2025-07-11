package com.bvhfve.aethelon.phase1.entity;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.util.AethelonModule;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * EntityModule - Handles Aethelon entity registration and attributes
 * 
 * MINECRAFT INTEGRATION:
 * - Extends: Uses WaterCreatureEntity as base class for aquatic behavior
 * - Hooks into: Minecraft entity registration system via Registry.register()
 * - Modifies: Adds new entity type to EntityType registry
 * 
 * MODULE ROLE:
 * - Purpose: Register Aethelon entity type and configure attributes
 * - Dependencies: phase1 (main phase coordinator)
 * - Provides: AethelonEntity class, entity type registration, attribute configuration
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+ (uses current EntityType and attribute APIs)
 * - Fabric API: 0.119.2+ (uses FabricEntityTypeBuilder and FabricDefaultAttributeRegistry)
 * - Breaking changes: Entity API changes may require attribute registration updates
 * 
 * ENTITY DESIGN:
 * The Aethelon entity is designed as a massive, passive water creature:
 * - Health: 1000 HP (much higher than vanilla mobs for "world turtle" scale)
 * - Speed: 0.1 (very slow movement appropriate for massive creature)
 * - Size: 8x4 blocks (large enough to carry island structures)
 * - Behavior: Aquatic creature that swims naturally in water
 * - Knockback: Immune (too massive to be pushed around)
 */
public class EntityModule implements AethelonModule {
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase1.entity";
    }
    
    @Override
    public String getPhase() {
        return "phase1";
    }
    
    @Override
    public boolean isEnabled() {
        return AethelonCore.getConfigManager().isModuleEnabled("phase1", "entity");
    }
    
    @Override
    public void initialize() throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase 1 Entity module is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase 1 Entity module");
        
        try {
            // Register entity type using centralized registry manager
            AethelonCore.getRegistryManager().register(
                    Registries.ENTITY_TYPE,
                    Identifier.of(AethelonCore.MOD_ID, "aethelon"),
                    EntityTypeProvider.createAethelonEntityType(),
                    getModuleName()
            );
            
            // Register entity attributes
            FabricDefaultAttributeRegistry.register(
                    EntityTypeProvider.getAethelonEntityType(),
                    AethelonEntity.createAethelonAttributes()
            );
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase 1 Entity module initialization complete");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase 1 Entity module", e);
            throw e;
        }
    }
    
    @Override
    public void shutdown() throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase 1 Entity module");
        
        // Entity types cannot be unregistered in Minecraft
        // This is why hot reload is not supported for this module
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase 1 Entity module shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // Depends on main Phase 1 module
        return List.of("phase1");
    }
    
    @Override
    public boolean isCompatibleWith(String minecraftVersion) {
        // Compatible with Minecraft versions that support current entity APIs
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
        return "Entity Registration - Registers Aethelon turtle entity with attributes and behavior";
    }
    
    @Override
    public boolean supportsHotReload() {
        // Entity registration cannot be undone, so hot reload not supported
        return false;
    }
    
    @Override
    public int getLoadPriority() {
        // Load early within Phase 1 (other modules may depend on entity type)
        return 20;
    }
}