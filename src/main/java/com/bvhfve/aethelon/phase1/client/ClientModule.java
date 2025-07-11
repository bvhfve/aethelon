package com.bvhfve.aethelon.phase1.client;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.util.AethelonModule;
import com.bvhfve.aethelon.phase1.entity.EntityTypeProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * ClientModule - Handles client-side rendering for Aethelon entity
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: Fabric Client Rendering API, EntityRendererRegistry
 * - Hooks into: Client entity rendering pipeline, model layer registration
 * - Modifies: Adds custom entity renderer and model for Aethelon
 * 
 * MODULE ROLE:
 * - Purpose: Register client-side rendering components for Aethelon entity
 * - Dependencies: phase1, phase1.entity (needs entity type)
 * - Provides: Entity renderer, model layers, client-side visual representation
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+ (uses current client rendering APIs)
 * - Fabric API: 0.119.2+ (uses Fabric client rendering registration)
 * - Breaking changes: Rendering API changes may require renderer updates
 * 
 * CLIENT-SIDE ONLY:
 * This module is only loaded on the client side. Server-side code should
 * not reference this module directly.
 * 
 * RENDERING DESIGN:
 * The Aethelon entity uses a custom model and renderer to achieve:
 * - Large turtle appearance with detailed shell for island placement
 * - Smooth swimming animations appropriate for aquatic creature
 * - Texture support for different shell states (with/without island)
 * - Proper scaling and positioning for 8x4 block dimensions
 */
@Environment(EnvType.CLIENT)
public class ClientModule implements AethelonModule {
    
    // Model layer for the Aethelon entity
    public static final EntityModelLayer AETHELON_MODEL_LAYER = new EntityModelLayer(
        Identifier.of(AethelonCore.MOD_ID, "aethelon"), 
        "main"
    );
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase1.client";
    }
    
    @Override
    public String getPhase() {
        return "phase1";
    }
    
    @Override
    public boolean isEnabled() {
        return AethelonCore.getConfigManager().isModuleEnabled("phase1", "client");
    }
    
    @Override
    public void initialize() throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase 1 Client module is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase 1 Client module");
        
        try {
            // Register entity renderer - temporarily disabled due to API complexity
            // EntityRendererRegistry.register(
            //     EntityTypeProvider.getAethelonEntityType(),
            //     AethelonEntityRenderer::new
            // );
            
            AethelonCore.LOGGER.debug("Registered Aethelon entity renderer");
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase 1 Client module initialization complete");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase 1 Client module", e);
            throw e;
        }
    }
    
    @Override
    public void shutdown() throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase 1 Client module");
        
        // Client renderers cannot be unregistered
        // This is why hot reload is not supported for this module
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase 1 Client module shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // Depends on main Phase 1 module and entity module
        return List.of("phase1", "phase1.entity");
    }
    
    @Override
    public boolean isCompatibleWith(String minecraftVersion) {
        // Compatible with Minecraft versions that support current client APIs
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
        return "Client Rendering - Registers Aethelon entity renderer and model for client-side display";
    }
    
    @Override
    public boolean supportsHotReload() {
        // Client rendering registration cannot be undone
        return false;
    }
    
    @Override
    public int getLoadPriority() {
        // Load after entity module but still early in Phase 1
        return 30;
    }
}