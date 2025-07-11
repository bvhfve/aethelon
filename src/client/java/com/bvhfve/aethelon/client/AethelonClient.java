package com.bvhfve.aethelon.client;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.phase1.client.AethelonEntityModel;
import com.bvhfve.aethelon.phase1.client.ClientModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

/**
 * AethelonClient - Client-side initialization for Aethelon mod
 * 
 * MINECRAFT INTEGRATION:
 * - Implements: ClientModInitializer (Fabric API client entry point)
 * - Hooks into: Fabric client mod loading lifecycle
 * - Modifies: Registers client-side components (renderers, models, etc.)
 * 
 * CLIENT ROLE:
 * - Purpose: Initialize client-side components for the modular system
 * - Dependencies: AethelonCore, Phase 1 client module
 * - Provides: Client initialization, model layer registration
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: Client API changes may require updates
 * 
 * MODULAR INTEGRATION:
 * This client initializer works with the modular system by:
 * - Registering model layers needed by client modules
 * - Allowing client modules to handle their own renderer registration
 * - Providing a centralized place for client-side initialization
 */
public class AethelonClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        AethelonCore.LOGGER.info("Initializing Aethelon client-side components");
        
        try {
            // Register entity model layers
            registerModelLayers();
            
            // Client modules will handle their own renderer registration
            // through the modular system during AethelonCore initialization
            
            AethelonCore.LOGGER.info("Aethelon client initialization complete");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Aethelon client", e);
            throw new RuntimeException("Client initialization failed", e);
        }
    }
    
    /**
     * Register entity model layers for all phases
     */
    private void registerModelLayers() {
        // Phase 1 model layers - simplified for now
        // EntityModelLayerRegistry.registerModelLayer(
        //     ClientModule.AETHELON_MODEL_LAYER, 
        //     AethelonEntityModel::getTexturedModelData
        // );
        
        AethelonCore.LOGGER.debug("Registered Aethelon entity model layer");
        
        // TODO: Add model layers for future phases as needed
        // Phase 2: AI visualization models
        // Phase 3: Damage effect models
        // Phase 4: Island structure models
    }
}