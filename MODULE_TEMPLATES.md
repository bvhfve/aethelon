# Aethelon Module Templates

This document provides templates for creating new modules in the Aethelon mod following the established modular architecture pattern.

## Template Usage

1. Copy the appropriate template
2. Replace `{PHASE_NUMBER}`, `{MODULE_NAME}`, `{DESCRIPTION}` placeholders
3. Update dependencies, compatibility, and implementation details
4. Follow the established package structure

## Package Structure Convention

```
src/main/java/com/bvhfve/aethelon/
├── core/                           # Core system (always enabled)
├── phase{N}/                       # Phase N implementation
│   ├── Phase{N}Module.java         # Phase coordinator
│   ├── {module1}/                  # Sub-module 1
│   │   ├── {Module1}Module.java    # Sub-module coordinator
│   │   └── {Implementation}.java   # Implementation classes
│   ├── {module2}/                  # Sub-module 2
│   │   ├── {Module2}Module.java    # Sub-module coordinator
│   │   └── {Implementation}.java   # Implementation classes
│   └── ...                         # Additional sub-modules
```

---

## Phase Coordinator Template

```java
package com.bvhfve.aethelon.phase{PHASE_NUMBER};

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.util.AethelonModule;

import java.util.Arrays;
import java.util.List;

/**
 * Phase{PHASE_NUMBER}Module - Main coordinator for Phase {PHASE_NUMBER}: {PHASE_DESCRIPTION}
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: {MINECRAFT_APIS_USED}
 * - Hooks into: {MINECRAFT_HOOKS}
 * - Modifies: {MINECRAFT_MODIFICATIONS}
 * 
 * MODULE ROLE:
 * - Purpose: {MODULE_PURPOSE}
 * - Dependencies: {MODULE_DEPENDENCIES}
 * - Provides: {MODULE_PROVIDES}
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: {BREAKING_CHANGES}
 * 
 * PHASE {PHASE_NUMBER} OVERVIEW:
 * {DETAILED_PHASE_DESCRIPTION}
 * 
 * All Phase {PHASE_NUMBER} functionality can be disabled via configuration while maintaining
 * compatibility with other phases.
 */
public class Phase{PHASE_NUMBER}Module implements AethelonModule {
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase{PHASE_NUMBER}";
    }
    
    @Override
    public String getPhase() {
        return "phase{PHASE_NUMBER}";
    }
    
    @Override
    public boolean isEnabled() {
        return AethelonCore.getConfigManager().isPhaseEnabled("phase{PHASE_NUMBER}");
    }
    
    @Override
    public void initialize() throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase {PHASE_NUMBER} is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase {PHASE_NUMBER}: {PHASE_DESCRIPTION}");
        
        try {
            // Phase {PHASE_NUMBER} initialization is handled by sub-modules:
            // TODO: List sub-modules here
            // - phase{PHASE_NUMBER}.{submodule1}: {Description}
            // - phase{PHASE_NUMBER}.{submodule2}: {Description}
            
            // This module serves as a coordinator and dependency anchor
            // Sub-modules depend on this module being loaded first
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase {PHASE_NUMBER} initialization complete");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase {PHASE_NUMBER}", e);
            throw e;
        }
    }
    
    @Override
    public void shutdown() throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase {PHASE_NUMBER}");
        
        // Sub-modules handle their own shutdown
        // This module just tracks overall phase state
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase {PHASE_NUMBER} shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // TODO: Update dependencies based on phase requirements
        return Arrays.asList(/* "phase1", "phase2", etc. */);
    }
    
    @Override
    public boolean isCompatibleWith(String minecraftVersion) {
        // Phase {PHASE_NUMBER} is compatible with Minecraft 1.21.4+
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
        return "{PHASE_DESCRIPTION} - {DETAILED_DESCRIPTION}";
    }
    
    @Override
    public boolean supportsHotReload() {
        // TODO: Determine if this phase supports hot reload
        return false;
    }
    
    @Override
    public int getLoadPriority() {
        // TODO: Set appropriate load priority (lower numbers load first)
        // Phase 1: 10, Phase 2: 20, etc.
        return {PHASE_NUMBER}0;
    }
}
```

---

## Sub-Module Coordinator Template

```java
package com.bvhfve.aethelon.phase{PHASE_NUMBER}.{MODULE_NAME};

import com.bvhfve.aethelon.core.AethelonCore;
import com.bvhfve.aethelon.core.util.AethelonModule;

import java.util.Arrays;
import java.util.List;

/**
 * {MODULE_NAME}Module - {MODULE_DESCRIPTION}
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: {MINECRAFT_APIS_USED}
 * - Hooks into: {MINECRAFT_HOOKS}
 * - Modifies: {MINECRAFT_MODIFICATIONS}
 * 
 * MODULE ROLE:
 * - Purpose: {MODULE_PURPOSE}
 * - Dependencies: {MODULE_DEPENDENCIES}
 * - Provides: {MODULE_PROVIDES}
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: {BREAKING_CHANGES}
 * 
 * IMPLEMENTATION DETAILS:
 * {IMPLEMENTATION_DETAILS}
 */
public class {MODULE_NAME}Module implements AethelonModule {
    
    private boolean initialized = false;
    
    @Override
    public String getModuleName() {
        return "phase{PHASE_NUMBER}.{MODULE_NAME}";
    }
    
    @Override
    public String getPhase() {
        return "phase{PHASE_NUMBER}";
    }
    
    @Override
    public boolean isEnabled() {
        return AethelonCore.getConfigManager().isModuleEnabled("phase{PHASE_NUMBER}", "{MODULE_NAME}");
    }
    
    @Override
    public void initialize() throws Exception {
        if (!isEnabled()) {
            AethelonCore.LOGGER.info("Phase {PHASE_NUMBER} {MODULE_NAME} module is disabled, skipping initialization");
            return;
        }
        
        AethelonCore.LOGGER.info("Initializing Phase {PHASE_NUMBER} {MODULE_NAME} module");
        
        try {
            // TODO: Implement module initialization
            // Register components, set up systems, etc.
            
            initialized = true;
            AethelonCore.LOGGER.info("Phase {PHASE_NUMBER} {MODULE_NAME} module initialization complete");
            
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize Phase {PHASE_NUMBER} {MODULE_NAME} module", e);
            throw e;
        }
    }
    
    @Override
    public void shutdown() throws Exception {
        if (!initialized) {
            return;
        }
        
        AethelonCore.LOGGER.info("Shutting down Phase {PHASE_NUMBER} {MODULE_NAME} module");
        
        // TODO: Implement module shutdown
        // Clean up resources, unregister components, etc.
        
        initialized = false;
        AethelonCore.LOGGER.info("Phase {PHASE_NUMBER} {MODULE_NAME} module shutdown complete");
    }
    
    @Override
    public List<String> getDependencies() {
        // TODO: Update dependencies
        return Arrays.asList("phase{PHASE_NUMBER}");
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
        return "{MODULE_DESCRIPTION}";
    }
    
    @Override
    public boolean supportsHotReload() {
        // TODO: Determine if this module supports hot reload
        return false;
    }
    
    @Override
    public int getLoadPriority() {
        // Sub-modules load after their phase coordinator
        return {PHASE_NUMBER}0 + {SUB_MODULE_PRIORITY};
    }
}
```

---

## Implementation Class Template

```java
package com.bvhfve.aethelon.phase{PHASE_NUMBER}.{MODULE_NAME};

import com.bvhfve.aethelon.core.AethelonCore;

/**
 * {CLASS_NAME} - {CLASS_DESCRIPTION}
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: {MINECRAFT_APIS_USED}
 * - Hooks into: {MINECRAFT_HOOKS}
 * - Modifies: {MINECRAFT_MODIFICATIONS}
 * 
 * CLASS ROLE:
 * - Purpose: {CLASS_PURPOSE}
 * - Dependencies: {CLASS_DEPENDENCIES}
 * - Provides: {CLASS_PROVIDES}
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: {BREAKING_CHANGES}
 * 
 * IMPLEMENTATION NOTES:
 * {IMPLEMENTATION_NOTES}
 */
public class {CLASS_NAME} {
    
    private static final String MODULE_NAME = "phase{PHASE_NUMBER}.{MODULE_NAME}";
    
    // TODO: Add class fields
    
    /**
     * Initialize the {CLASS_NAME}
     * 
     * MINECRAFT CONTEXT:
     * - Called by: {CALLER_CONTEXT}
     * - Timing: {TIMING_CONTEXT}
     * - Thread safety: {THREAD_SAFETY}
     */
    public void initialize() {
        AethelonCore.LOGGER.debug("Initializing {CLASS_NAME}");
        
        try {
            // TODO: Implement initialization logic
            
            AethelonCore.LOGGER.debug("{CLASS_NAME} initialized successfully");
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to initialize {CLASS_NAME}", e);
            throw new RuntimeException("Failed to initialize {CLASS_NAME}", e);
        }
    }
    
    /**
     * Shutdown the {CLASS_NAME}
     * 
     * MINECRAFT CONTEXT:
     * - Called by: {CALLER_CONTEXT}
     * - Timing: {TIMING_CONTEXT}
     * - Thread safety: {THREAD_SAFETY}
     */
    public void shutdown() {
        AethelonCore.LOGGER.debug("Shutting down {CLASS_NAME}");
        
        try {
            // TODO: Implement shutdown logic
            
            AethelonCore.LOGGER.debug("{CLASS_NAME} shutdown complete");
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Failed to shutdown {CLASS_NAME}", e);
        }
    }
    
    // TODO: Add implementation methods
}
```

---

## Configuration Template

```java
package com.bvhfve.aethelon.phase{PHASE_NUMBER};

/**
 * Phase{PHASE_NUMBER}Config - Configuration data for Phase {PHASE_NUMBER}
 * 
 * This class defines all configurable parameters for Phase {PHASE_NUMBER} modules.
 * All fields should have sensible defaults and be documented with their purpose
 * and valid value ranges.
 */
public class Phase{PHASE_NUMBER}Config {
    
    // Phase-level configuration
    public boolean enabled = true;
    public boolean debugMode = false;
    
    // Sub-module configuration
    public {MODULE_NAME}Config {MODULE_NAME} = new {MODULE_NAME}Config();
    
    /**
     * Configuration for {MODULE_NAME} module
     */
    public static class {MODULE_NAME}Config {
        public boolean enabled = true;
        
        // TODO: Add module-specific configuration fields
        // Example:
        // public int maxEntities = 10;
        // public double spawnRate = 0.1;
        // public String[] allowedBiomes = {"minecraft:ocean", "minecraft:deep_ocean"};
    }
    
    /**
     * Validate configuration values
     * 
     * @return true if configuration is valid, false otherwise
     */
    public boolean validate() {
        // TODO: Implement validation logic
        // Check ranges, validate arrays, etc.
        return true;
    }
    
    /**
     * Apply default values for any missing configuration
     */
    public void applyDefaults() {
        if ({MODULE_NAME} == null) {
            {MODULE_NAME} = new {MODULE_NAME}Config();
        }
        
        // TODO: Apply other defaults as needed
    }
}
```

---

## Test Template

```java
package com.bvhfve.aethelon.phase{PHASE_NUMBER};

import com.bvhfve.aethelon.core.AethelonCore;
import net.fabricmc.api.ModInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Phase{PHASE_NUMBER}Module
 * 
 * Tests module loading, initialization, configuration, and basic functionality.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Phase{PHASE_NUMBER}ModuleTest {
    
    private Phase{PHASE_NUMBER}Module module;
    
    @BeforeEach
    void setUp() {
        module = new Phase{PHASE_NUMBER}Module();
    }
    
    @Test
    void testModuleMetadata() {
        assertEquals("phase{PHASE_NUMBER}", module.getModuleName());
        assertEquals("phase{PHASE_NUMBER}", module.getPhase());
        assertNotNull(module.getDescription());
        assertTrue(module.getLoadPriority() > 0);
    }
    
    @Test
    void testCompatibility() {
        assertTrue(module.isCompatibleWith("1.21.4"));
        assertTrue(module.isCompatibleWith("1.21.5"));
        assertNotNull(module.getRequiredFabricApiVersion());
    }
    
    @Test
    void testDependencies() {
        assertNotNull(module.getDependencies());
        // TODO: Verify specific dependencies
    }
    
    @Test
    void testInitialization() {
        assertDoesNotThrow(() -> {
            if (module.isEnabled()) {
                module.initialize();
                module.shutdown();
            }
        });
    }
    
    // TODO: Add more specific tests for module functionality
}
```

---

## Quick Start Guide

### Creating a New Phase

1. **Create Phase Directory**: `src/main/java/com/bvhfve/aethelon/phase{N}/`

2. **Create Phase Coordinator**: Copy Phase Coordinator Template, replace placeholders

3. **Create Sub-Module Directories**: Based on roadmap design

4. **Create Sub-Module Coordinators**: Copy Sub-Module Template for each sub-module

5. **Create Implementation Classes**: Copy Implementation Template for each class

6. **Update Configuration**: Add phase config to main configuration system

7. **Add Tests**: Copy Test Template and implement specific tests

### Example: Creating Phase 2

```bash
# Create directories
mkdir -p src/main/java/com/bvhfve/aethelon/phase2/{ai,state,pathfinding}

# Create phase coordinator
# Copy Phase Coordinator Template -> Phase2Module.java

# Create sub-module coordinators
# Copy Sub-Module Template -> ai/AiModule.java
# Copy Sub-Module Template -> state/StateModule.java
# Copy Sub-Module Template -> pathfinding/PathfindingModule.java

# Create implementation classes as needed
```

### Placeholder Reference

- `{PHASE_NUMBER}`: 1, 2, 3, etc.
- `{MODULE_NAME}`: ai, state, pathfinding, etc.
- `{CLASS_NAME}`: Specific class name
- `{DESCRIPTION}`: Human-readable description
- `{MINECRAFT_APIS_USED}`: Fabric APIs, Minecraft classes used
- `{MODULE_PURPOSE}`: What this module does
- `{IMPLEMENTATION_DETAILS}`: Technical implementation notes

---

## Specialized Module Templates

### AI Goal Template

For creating custom AI goals for entities:

**File:** `templates/AIGoalTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your goal class name (e.g., `SwimToPlayerGoal`)
- Replace `{GOAL_DESCRIPTION}` with goal purpose
- Replace `{GOAL_PRIORITY}` with numeric priority (lower = higher priority)
- Implement `canStartGoal()`, `shouldContinueGoal()`, `startGoal()`, `stopGoal()`, and `tickGoal()` methods

**Features:**
- Extends Minecraft's Goal class
- Built-in module enable/disable checks
- Event statistics tracking
- Comprehensive documentation structure
- Cooldown and state management

---

### Client Rendering Template

For creating custom entity renderers (client-side only):

**File:** `templates/ClientRenderingTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your renderer class name (e.g., `AethelonEntityRenderer`)
- Replace `{ENTITY_TYPE}` with the entity type being rendered
- Replace `{TEXTURE_NAME}` with texture file name
- Replace `{SHADOW_RADIUS}` with shadow size (0.0f for no shadow)

**Features:**
- Extends EntityRenderer with proper client-side annotations
- Dynamic texture selection support
- Animation and transformation helpers
- Post-render effects support
- Debug rendering capabilities

---

### State Manager Template

For managing persistent entity or world state:

**File:** `templates/StateManagerTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your state manager name (e.g., `AethelonStateManager`)
- Replace `{STATE_CLASS}` with your state data class name
- Replace `{STATE_DESCRIPTION}` with state purpose
- Implement state data fields and update logic

**Features:**
- Singleton pattern for global state management
- NBT serialization for persistence
- Client-server synchronization support
- Automatic cleanup and statistics
- UUID-based entity state tracking

---

### Networking Template

For client-server communication:

**File:** `templates/NetworkingTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your networking class name (e.g., `AethelonNetworking`)
- Replace `{PACKET_NAME}` with packet identifier
- Replace `{PACKET_DATA_CLASS}` with packet data class name
- Implement packet data serialization/deserialization

**Features:**
- Fabric Networking API integration
- Multiple send methods (to player, to all, to tracking, to range)
- Packet validation and error handling
- Server-side packet processing
- Comprehensive packet data management

---

### Event Handler Template

For handling Minecraft game events:

**File:** `templates/EventHandlerTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your event handler name (e.g., `AethelonEventHandler`)
- Replace `{EVENT_DESCRIPTION}` with event handling purpose
- Implement specific event handling methods
- Register additional event types as needed

**Features:**
- Fabric Event API integration
- Server lifecycle event handling
- Player interaction event handling
- World event handling support
- Event statistics and performance tracking

---

### Utility Template

For helper functions and common operations:

**File:** `templates/UtilityTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your utility class name (e.g., `AethelonUtils`)
- Replace `{UTILITY_DESCRIPTION}` with utility purpose
- Add static utility methods as needed
- Follow the established pattern for error handling

**Features:**
- Static utility class pattern
- Entity and world querying helpers
- Mathematical calculation functions
- Position and distance utilities
- Safety and validation helpers

---

### Block Template

For creating custom blocks:

**File:** `templates/BlockTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your block class name (e.g., `AethelonBlock`)
- Replace `{BLOCK_MATERIAL}` with material type (STONE/WOOD/METAL/etc.)
- Replace `{BLOCK_HARDNESS}` and `{BLOCK_RESISTANCE}` with numeric values
- Implement block interaction and behavior methods

**Features:**
- Extends Block with comprehensive state management
- Player interaction handling
- Block breaking and placement logic
- Random tick support for growth/decay
- Neighbor update handling for redstone
- Custom properties and rendering support

---

### Item Template

For creating custom items:

**File:** `templates/ItemTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your item class name (e.g., `AethelonItem`)
- Replace `{ITEM_TYPE}` with item category
- Replace `{ITEM_STACK_SIZE}` with maximum stack size
- Replace `{USE_DURATION}` with use animation duration

**Features:**
- Extends Item with full interaction support
- Use action handling (eat/drink/block/etc.)
- Block interaction capabilities
- Tooltip customization
- Durability and enchantment support
- Cooldown management

---

### World Generation Template

For creating world generation features:

**File:** `templates/WorldGenerationTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your world gen class name (e.g., `AethelonWorldGen`)
- Replace `{FEATURE_NAME}` with feature identifier
- Implement configured and placed feature creation
- Add biome modification logic

**Features:**
- Configured and placed feature registration
- Biome modification support
- Custom dimension type creation
- Ore generation helpers
- Structure placement logic
- Height and rarity configuration

---

### Block Entity Template

For creating block entities with data storage:

**File:** `templates/BlockEntityTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your block entity class name (e.g., `AethelonBlockEntity`)
- Replace `{INVENTORY_SIZE}` with inventory slot count
- Replace `{MAX_PROGRESS}` with processing duration
- Implement tick logic and data persistence

**Features:**
- Extends BlockEntity with inventory support
- Server and client tick handling
- NBT data persistence
- Client-server synchronization
- Progress tracking and automation
- Energy and fluid support framework

---

### Screen Handler Template

For creating GUI screen handlers:

**File:** `templates/ScreenHandlerTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your screen handler name (e.g., `AethelonScreenHandler`)
- Replace slot position constants with actual coordinates
- Replace `{INVENTORY_SIZE}` and `{PROPERTY_COUNT}` with appropriate values
- Implement slot layout and item transfer logic

**Features:**
- Extends ScreenHandler with inventory management
- Custom slot layout support
- Shift-click item transfer logic
- Property synchronization for progress bars
- Input/output/fuel slot validation
- Player inventory integration

---

### Particle Template

For creating custom particle effects:

**File:** `templates/ParticleTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your particle class name (e.g., `AethelonParticle`)
- Replace `{DEFAULT_LIFETIME}` with particle duration in ticks
- Replace `{DEFAULT_SCALE}` and `{GRAVITY_STRENGTH}` with appropriate values
- Implement animation and physics behavior

**Features:**
- Extends SpriteBillboardParticle with full animation support
- Physics simulation (gravity, air resistance, rotation)
- Color and alpha transitions
- Scale and fade animations
- Client-side only with proper annotations
- Factory pattern for particle creation

---

### Sound Template

For managing sound events:

**File:** `templates/SoundTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your sound class name (e.g., `AethelonSounds`)
- Replace `{SOUND_NAME}` with sound identifier
- Add sound event definitions
- Implement convenience playback methods

**Features:**
- Sound event registration and management
- Position-based sound playback
- Player-specific sound playback
- Entity-based sound playback
- Volume and pitch control
- Distance-based volume adjustment

---

### Data Generator Template

For creating data generation providers:

**File:** `templates/DataGeneratorTemplate.java`

**Usage:**
- Replace `{CLASS_NAME}` with your data generator name (e.g., `AethelonDataGenerator`)
- Implement recipe, loot table, and model generation
- Add tag and advancement providers
- Configure output data types

**Features:**
- Complete data generation setup
- Recipe generation (shaped, shapeless, smelting)
- Loot table generation with conditions
- Block state and item model generation
- Tag generation for blocks and items
- Advancement creation with criteria
- Extensible provider system

---

## Template Usage Guidelines

### Choosing the Right Template

1. **Entity Creation**: Use `EntityTemplate.java`
2. **Entity AI Behavior**: Use `AIGoalTemplate.java`
3. **Visual Rendering**: Use `ClientRenderingTemplate.java`
4. **Data Persistence**: Use `StateManagerTemplate.java`
5. **Client-Server Communication**: Use `NetworkingTemplate.java`
6. **Game Event Handling**: Use `EventHandlerTemplate.java`
7. **Helper Functions**: Use `UtilityTemplate.java`
8. **Registry Management**: Use `RegistryTemplate.java`
9. **Configuration**: Use `ConfigTemplate.java`
10. **Module Coordination**: Use `SubModuleTemplate.java` or `PhaseCoordinatorTemplate.java`
11. **Block Creation**: Use `BlockTemplate.java`
12. **Item Creation**: Use `ItemTemplate.java`
13. **World Generation**: Use `WorldGenerationTemplate.java`
14. **Block Entities (Storage/Processing)**: Use `BlockEntityTemplate.java`
15. **GUI Screens**: Use `ScreenHandlerTemplate.java`
16. **Particle Effects**: Use `ParticleTemplate.java`
17. **Sound Management**: Use `SoundTemplate.java`
18. **Data Generation**: Use `DataGeneratorTemplate.java`

### Template Customization Process

1. **Copy Template**: Copy the appropriate template file
2. **Replace Placeholders**: Replace all `{PLACEHOLDER}` values
3. **Implement TODOs**: Fill in all `// TODO:` sections
4. **Add Documentation**: Update class and method documentation
5. **Test Integration**: Ensure module enable/disable works
6. **Add Error Handling**: Implement proper exception handling
7. **Performance Optimization**: Add caching and optimization where needed

### Best Practices

- **Always check module enabled state** before executing functionality
- **Use comprehensive logging** with appropriate log levels
- **Implement proper error handling** with meaningful error messages
- **Follow the established package structure** for consistency
- **Add performance monitoring** for resource-intensive operations
- **Include version compatibility information** in documentation
- **Use dependency injection patterns** for loose coupling

These templates ensure consistency across all phases and make development much faster while maintaining the high quality documentation and architecture standards established in Phase 1.