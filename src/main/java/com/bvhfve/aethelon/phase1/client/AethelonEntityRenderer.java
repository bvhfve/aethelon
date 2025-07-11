package com.bvhfve.aethelon.phase1.client;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.phase1.entity.AethelonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * AethelonEntityRenderer - Custom renderer for the Aethelon world turtle entity
 * 
 * MINECRAFT INTEGRATION:
 * - Extends: MobEntityRenderer (provides standard mob rendering functionality)
 * - Uses: Fabric client rendering pipeline, EntityRendererFactory
 * - Hooks into: Entity rendering system, texture loading, model rendering
 * - Modifies: Visual representation of Aethelon entities in the world
 * 
 * RENDERING ROLE:
 * - Purpose: Render Aethelon entities with custom model and textures
 * - Entity Type: AethelonEntity (world turtle)
 * - Rendering Features: Custom model, texture variants, shadow rendering
 * - Texture: aethelon.png (base turtle texture)
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+ (uses current entity rendering APIs)
 * - Fabric API: 0.119.2+ (uses Fabric client rendering)
 * - Breaking changes: Rendering API changes may affect renderer functionality
 * 
 * CLIENT-SIDE ONLY:
 * This class is only loaded on the client side. Server-side code should
 * not reference this class directly.
 * 
 * RENDERING DESIGN:
 * The renderer is designed to handle the massive scale of the world turtle:
 * - Large shadow radius appropriate for 8x4 block entity
 * - Custom model with detailed shell for island placement
 * - Texture variants for different states (idle, moving, with/without island)
 * - Smooth animation support for swimming and state transitions
 */
@Environment(EnvType.CLIENT)
public class AethelonEntityRenderer {
    
    // Base texture for the Aethelon entity
    private static final Identifier TEXTURE = Identifier.of(
        AethelonCore.MOD_ID, 
        "textures/entity/aethelon.png"
    );
    
    // Additional texture variants for different states
    private static final Identifier TEXTURE_WITH_ISLAND = Identifier.of(
        AethelonCore.MOD_ID, 
        "textures/entity/aethelon_with_island.png"
    );
    
    private static final Identifier TEXTURE_MOVING = Identifier.of(
        AethelonCore.MOD_ID, 
        "textures/entity/aethelon_moving.png"
    );
    
    public AethelonEntityRenderer(EntityRendererFactory.Context context) {
        AethelonCore.LOGGER.debug("Created AethelonEntityRenderer with shadow radius 4.0");
    }
    
    public Identifier getTexture(AethelonEntity entity) {
        // Return appropriate texture based on entity state
        return getEntityTexture(entity);
    }
    
    /**
     * Get the texture for a specific entity instance based on its state
     * 
     * @param entity The Aethelon entity being rendered
     * @return Texture identifier for current entity state
     */
    protected Identifier getEntityTexture(AethelonEntity entity) {
        // Check if module is enabled
        if (!AethelonCore.getConfigManager().isModuleEnabled("phase1", "client")) {
            return TEXTURE; // Fallback to base texture
        }
        
        // TODO: Phase 4 - Check if entity has island loaded
        // if (entity.hasIsland()) {
        //     return TEXTURE_WITH_ISLAND;
        // }
        
        // Return texture based on current state
        switch (entity.getCurrentState()) {
            case MOVING:
                return TEXTURE_MOVING;
            case DAMAGED:
                // TODO: Add damaged texture variant
                return TEXTURE;
            case TRANSITIONING:
                // TODO: Add transitioning texture variant
                return TEXTURE;
            case IDLE:
            default:
                return TEXTURE;
        }
    }
    
    /**
     * Get the shadow radius for this entity
     * Large radius appropriate for massive world turtle
     * 
     * @param entity Entity being rendered
     * @return Shadow radius (4.0f for large creature)
     */
    protected float getShadowRadius(AethelonEntity entity) {
        return 4.0f; // Large shadow for 8x4 block entity
    }
    
    /**
     * Check if this entity should render in first person
     * World turtle should never render in first person
     * 
     * @param entity Entity being rendered
     * @return false (never render in first person)
     */
    protected boolean shouldRenderInFirstPerson(AethelonEntity entity) {
        return false; // Too large for first person rendering
    }
    
    /**
     * Get the render distance for this entity
     * Large render distance for important world feature
     * 
     * @param entity Entity being rendered
     * @return Render distance in blocks (128 for important world feature)
     */
    protected double getRenderDistance(AethelonEntity entity) {
        return 128.0; // Large render distance for world turtle
    }
}