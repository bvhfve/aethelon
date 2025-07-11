package com.bvhfve.aethelon.phase1.services;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;

/**
 * ClientRenderService - Service interface for client-side rendering management
 * 
 * This interface encapsulates all client-side rendering functionality, replacing
 * the rendering logic scattered throughout ClientModule with a clean service contract.
 * 
 * REPLACES:
 * - ClientModule.registerEntityRenderer()
 * - ClientModule.registerModelLayers()
 * - Static model layer access
 * - Direct renderer registration
 * 
 * BENEFITS:
 * - Centralized rendering logic
 * - Easy testing with mocked dependencies
 * - Clear separation of concerns
 * - Configuration abstraction
 * - Client-side only isolation
 * 
 * CLIENT-SIDE ONLY:
 * This service is only available on the client side. Server-side code should
 * not reference this service directly.
 */
@Environment(EnvType.CLIENT)
public interface ClientRenderService {
    
    /**
     * Register the Aethelon entity renderer
     * Sets up the entity renderer with proper model and texture configuration
     * 
     * @throws IllegalStateException if entity type not available
     * @throws RuntimeException if renderer registration fails
     */
    void registerEntityRenderer();
    
    /**
     * Register model layers for the Aethelon entity
     * Sets up the model layers required for entity rendering
     * 
     * @throws RuntimeException if model layer registration fails
     */
    void registerModelLayers();
    
    /**
     * Get the main model layer for the Aethelon entity
     * 
     * @return The primary entity model layer
     */
    EntityModelLayer getModelLayer();
    
    /**
     * Check if entity renderer has been registered
     * 
     * @return true if renderer is registered and available
     */
    boolean isRendererRegistered();
    
    /**
     * Check if model layers have been registered
     * 
     * @return true if model layers are registered and available
     */
    boolean areModelLayersRegistered();
    
    /**
     * Get current render configuration
     * 
     * @return Current render configuration
     */
    RenderConfiguration getRenderConfiguration();
    
    /**
     * Update render configuration (if supported)
     * 
     * @param configuration New render configuration
     * @throws UnsupportedOperationException if runtime updates not supported
     */
    void updateRenderConfiguration(RenderConfiguration configuration);
    
    /**
     * Set render distance for the entity
     * 
     * @param distance Render distance in blocks
     */
    void setRenderDistance(float distance);
    
    /**
     * Enable or disable level of detail (LOD) rendering
     * 
     * @param enabled Whether LOD should be enabled
     */
    void setLODEnabled(boolean enabled);
    
    /**
     * Enable or disable frustum culling for the entity
     * 
     * @param enabled Whether culling should be enabled
     */
    void setCullingEnabled(boolean enabled);
    
    /**
     * Configuration data for rendering settings
     */
    class RenderConfiguration {
        public final boolean enableCustomModel;
        public final boolean enableAnimations;
        public final boolean enableParticles;
        public final float renderDistance;
        public final boolean showHealthBar;
        public final boolean showNameTag;
        public final float nameTagDistance;
        public final boolean enableLOD;
        public final int maxRenderDistance;
        public final boolean enableCulling;
        
        public RenderConfiguration(boolean enableCustomModel, boolean enableAnimations, 
                                 boolean enableParticles, float renderDistance, 
                                 boolean showHealthBar, boolean showNameTag, 
                                 float nameTagDistance, boolean enableLOD, 
                                 int maxRenderDistance, boolean enableCulling) {
            this.enableCustomModel = enableCustomModel;
            this.enableAnimations = enableAnimations;
            this.enableParticles = enableParticles;
            this.renderDistance = renderDistance;
            this.showHealthBar = showHealthBar;
            this.showNameTag = showNameTag;
            this.nameTagDistance = nameTagDistance;
            this.enableLOD = enableLOD;
            this.maxRenderDistance = maxRenderDistance;
            this.enableCulling = enableCulling;
        }
        
        public static RenderConfiguration getDefault() {
            return new RenderConfiguration(
                true,   // enableCustomModel
                true,   // enableAnimations
                true,   // enableParticles
                128.0f, // renderDistance
                true,   // showHealthBar
                true,   // showNameTag
                32.0f,  // nameTagDistance
                true,   // enableLOD
                256,    // maxRenderDistance
                true    // enableCulling
            );
        }
        
        @Override
        public String toString() {
            return String.format(
                "RenderConfiguration[model=%s, animations=%s, particles=%s, distance=%.1f, healthBar=%s, nameTag=%s, LOD=%s, culling=%s]",
                enableCustomModel, enableAnimations, enableParticles, renderDistance,
                showHealthBar, showNameTag, enableLOD, enableCulling
            );
        }
    }
}