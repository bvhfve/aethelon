package com.bvhfve.aethelon.phase1.client;

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.di.DependencyInjectionContainer;
import com.bvhfve.aethelon.core.di.ServiceRegistry;
import com.bvhfve.aethelon.core.services.ConfigService;
import com.bvhfve.aethelon.core.util.InjectableAethelonModule;
import com.bvhfve.aethelon.phase1.services.ClientRenderService;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

/**
 * ClientModuleDI - Dependency injection version of ClientModule
 * 
 * This module demonstrates the improved client-side architecture using dependency injection
 * and service-oriented design. It replaces the original ClientModule's static dependencies
 * with clean, testable service injection.
 * 
 * IMPROVEMENTS OVER ORIGINAL:
 * - Service injection instead of static access
 * - Clear dependency declarations
 * - Testable design with mockable services
 * - Proper error handling and validation
 * - Clean separation of client/server concerns
 * 
 * MIGRATION NOTES:
 * - This runs alongside the original ClientModule during transition
 * - In Phase 2.2, this would replace the original implementation
 * - Shows the full potential of the DI architecture for client-side code
 * - Maintains all functionality while improving maintainability
 * 
 * CLIENT-SIDE ONLY:
 * This module is only loaded on the client side.
 */
@Environment(EnvType.CLIENT)
public class ClientModuleDI implements InjectableAethelonModule {
    
    // Injected services
    @DependencyInjectionContainer.Inject
    private ClientRenderService clientRenderService;
    
    @DependencyInjectionContainer.Inject
    private ConfigService configService;
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase1.client.di";
    }
    
    @Override
    public String getPhase() {
        return "phase1";
    }
    
    @Override
    public boolean isEnabled() {
        // Enable DI version when DI mode is active
        return configService.isModuleEnabled("phase1", "client");
    }
    
    @Override
    public void initializeWithDI(ServiceRegistry serviceRegistry) throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase 1 Client module (DI) is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase 1 Client module (DI version)");
        
        try {
            // Validate all dependencies are available
            if (!validateDependencies(serviceRegistry)) {
                throw new IllegalStateException("Required services not available for ClientModuleDI");
            }
            
            // Use services to perform client-side initialization
            clientRenderService.registerEntityRenderer();
            clientRenderService.registerModelLayers();
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase 1 Client module (DI) initialization complete");
            
            // Log service state for debugging
            if (configService.getDebugConfig().isModuleLoadingEnabled()) {
                AethelonCore.LOGGER.debug("ClientRenderService state: {}", 
                    ((com.bvhfve.aethelon.phase1.services.ClientRenderServiceImpl) clientRenderService).getDebugInfo());
                AethelonCore.LOGGER.debug("Render configuration: {}", clientRenderService.getRenderConfiguration());
            }
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase 1 Client module (DI)", e);
            throw e;
        }
    }
    
    @Override
    public void shutdownWithDI(ServiceRegistry serviceRegistry) throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase 1 Client module (DI)");
        
        // Client renderers cannot be unregistered in Minecraft
        // But we can clean up our service state and log the shutdown
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase 1 Client module (DI) shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // Depends on main Phase 1 module and DI entity module
        return List.of("phase1", "phase1.entity.di");
    }
    
    @Override
    public List<Class<?>> getRequiredServices() {
        // Declare service dependencies for validation
        return List.of(
            ClientRenderService.class,
            ConfigService.class
        );
    }
    
    @Override
    public List<Class<?>> getProvidedServices() {
        // This module doesn't provide services directly
        // The ClientRenderService is provided by the DI container
        return List.of();
    }
    
    @Override
    public ModuleServiceConfiguration getServiceConfiguration() {
        return new ModuleServiceConfiguration()
            .withScope(ClientRenderService.class, DependencyInjectionContainer.Scope.MODULE)
            .withQualifier(ClientRenderService.class, "phase1");
    }
    
    @Override
    public boolean isCompatibleWith(String minecraftVersion) {
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
        return "Client Rendering (DI) - Dependency injection version of client rendering module";
    }
    
    @Override
    public boolean supportsHotReload() {
        return false; // Client rendering registration cannot be undone
    }
    
    @Override
    public int getLoadPriority() {
        return 31; // Load after original client module for comparison
    }
    
    @Override
    public DependencyInjectionContainer.Scope getDependencyScope() {
        return DependencyInjectionContainer.Scope.MODULE;
    }
    
    // Backward compatibility methods (not used in DI version)
    @Override
    public void initialize() throws Exception {
        throw new UnsupportedOperationException("Use initializeWithDI() for dependency injection modules");
    }
    
    @Override
    public void shutdown() throws Exception {
        throw new UnsupportedOperationException("Use shutdownWithDI() for dependency injection modules");
    }
    
    /**
     * Get client render service for external access (if needed)
     */
    public ClientRenderService getClientRenderService() {
        return clientRenderService;
    }
}