# Aethelon Mod Development Roadmap - Modular Architecture

## Mod Summary: Aethelon, the World Turtle

The Aethelon mod introduces a colossal, passive world turtle entity that carries a functional, self-contained island on its back. This island is a dynamic structure, loaded from .nbt files, that is captured and re-pasted block-by-block whenever the Aethelon moves. The turtle's behavior is governed by a simple state machine, causing it to idle for long periods before pathfinding through deep oceans to a new coastal location. Players can trigger its movement by damaging it, and killing the Aethelon destroys the island in a spectacular explosion.

## Modular Architecture

**Core System:**
- **AethelonCore** - Main mod coordination and module loading
- **ConfigManager** - JSON-based configuration with phase/module toggles
- **RegistryManager** - Centralized registry coordination with dependency resolution
- **ModuleLoader** - Dynamic module loading with dependency resolution

**Technical Foundation:**
- **Entity Framework**: Fabric EntityTypeBuilder with custom WaterCreatureEntity extension
- **AI System**: Custom Goal-based AI with state machine (IDLE, MOVING, TRANSITIONING, DAMAGED)
- **Biome Integration**: Fabric BiomeModifications API for spawn control
- **Structure System**: StructureTemplate for .nbt loading and block-by-block operations
- **Event Handling**: Fabric lifecycle events for damage detection and world persistence
- **Configuration**: JSON-based config with runtime reloading and module toggles

**Module Structure:**
```
core/                    # Always enabled core systems
phase1/                  # Basic Entity Foundation
├── entity/             # Entity registration and attributes
├── client/             # Client-side rendering and models
└── spawn/              # Biome spawning and spawn eggs
phase2/                  # Entity Behavior & Movement
├── ai/                 # AI goals and behavior trees
├── state/              # State management and transitions
└── pathfinding/        # Movement and navigation systems
[additional phases...]
```

## Development Phases

### **Phase 1: Basic Entity Foundation** ✅
**Goal**: Create a basic turtle entity that spawns and exists in the world

**Module Structure:**
```
phase1/
├── Phase1Module.java           # Phase coordinator and lifecycle management
├── entity/
│   ├── EntityModule.java       # Entity registration and attributes
│   ├── EntityTypeProvider.java # Entity type creation and access
│   └── AethelonEntity.java     # Core entity implementation
├── client/
│   ├── ClientModule.java       # Client-side coordination
│   ├── model/
│   │   ├── ModelModule.java    # Model registration and layers
│   │   └── AethelonEntityModel.java # 3D turtle model
│   └── render/
│       └── AethelonEntityRenderer.java # Entity rendering
└── spawn/
    ├── SpawnModule.java        # Spawn system coordination
    ├── BiomeSpawnManager.java  # Biome modification logic
    └── SpawnEggProvider.java   # Spawn egg registration
```

**Implementation Details:**
- **Entity Registration**: Custom WaterCreatureEntity with 1000 HP, slow movement speed
- **Client Rendering**: Large turtle model with shell details and animations
- **Spawn System**: Deep ocean biome spawning with configurable rates
- **Configuration**: Phase-level and module-level toggles

**Dependencies**: None (foundation phase)
**Status**: Core entity and registration complete, client and spawn modules in progress

---

### **Phase 2: Entity Behavior & Movement**
**Goal**: Implement AI behavior, state management, and basic movement

**Module Structure:**
```
phase2/
├── Phase2Module.java           # Phase coordinator and lifecycle management
├── ai/
│   ├── AiModule.java          # AI system coordination
│   ├── goals/
│   │   ├── GoalModule.java    # Goal registration and management
│   │   ├── IdleGoal.java      # Idle behavior implementation
│   │   └── WanderGoal.java    # Basic wandering behavior
│   └── behavior/
│       ├── BehaviorModule.java # Behavior tree coordination
│       └── StateMachine.java   # Entity state management
├── state/
│   ├── StateModule.java       # State system coordination
│   ├── StateManager.java      # State transition logic
│   └── states/
│       ├── IdleState.java     # Idle state implementation
│       ├── MovingState.java   # Movement state implementation
│       └── TransitionState.java # State transition handling
└── pathfinding/
    ├── PathfindingModule.java # Navigation system coordination
    ├── OceanPathfinder.java   # Deep ocean navigation
    └── MovementController.java # Movement execution
```

**Implementation Details:**
- **AI Goals**: Custom goal system for long idle periods and movement triggers
- **State Machine**: IDLE, MOVING, TRANSITIONING, DAMAGED states with transitions
- **Pathfinding**: Ocean-specific navigation avoiding shallow water and land
- **Movement**: Slow, deliberate movement with turning animations

**Dependencies**: Phase 1 (entity foundation)
**Status**: Not started

---

### **Phase 3: Damage Response & Player Interaction**
**Goal**: Handle player damage, implement movement triggers, and basic interactions

**Module Structure:**
```
phase3/
├── Phase3Module.java           # Phase coordinator and lifecycle management
├── damage/
│   ├── DamageModule.java      # Damage system coordination
│   ├── DamageHandler.java     # Damage event processing
│   └── DamageStateManager.java # Damage-triggered state changes
├── interaction/
│   ├── InteractionModule.java # Player interaction coordination
│   ├── PlayerDetection.java   # Player proximity detection
│   └── InteractionHandler.java # Right-click and other interactions
└── triggers/
    ├── TriggerModule.java     # Movement trigger coordination
    ├── DamageTrigger.java     # Damage-based movement activation
    └── TimerTrigger.java      # Time-based movement activation
```

**Implementation Details:**
- **Damage Detection**: Listen for damage events and trigger movement
- **Player Interaction**: Basic right-click interactions and proximity detection
- **Movement Triggers**: Multiple ways to activate turtle movement
- **State Integration**: Connect damage events to Phase 2 state machine

**Dependencies**: Phase 1 (entity), Phase 2 (behavior)
**Status**: Not started

---

### **Phase 4: Island Structure System**
**Goal**: Implement island loading, storage, and basic management

**Module Structure:**
```
phase4/
├── Phase4Module.java           # Phase coordinator and lifecycle management
├── structure/
│   ├── StructureModule.java   # Structure system coordination
│   ├── IslandLoader.java      # NBT structure loading
│   ├── IslandStorage.java     # Island data persistence
│   └── StructureValidator.java # Island structure validation
├── attachment/
│   ├── AttachmentModule.java  # Island attachment coordination
│   ├── AttachmentPoint.java   # Turtle-island connection point
│   └── OffsetManager.java     # Relative positioning system
└── management/
    ├── ManagementModule.java  # Island lifecycle coordination
    ├── IslandRegistry.java    # Track loaded islands
    └── IslandMetadata.java    # Island properties and data
```

**Implementation Details:**
- **NBT Loading**: Load island structures from .nbt files
- **Attachment System**: Attach islands to turtle entities with proper offsets
- **Data Persistence**: Save/load island state with world data
- **Structure Validation**: Ensure island structures are valid and safe

**Dependencies**: Phase 1 (entity foundation)
**Status**: Not started

---

### **Phase 5: Dynamic Island Movement**
**Goal**: Implement block-by-block island capture and placement during movement

**Module Structure:**
```
phase5/
├── Phase5Module.java           # Phase coordinator and lifecycle management
├── capture/
│   ├── CaptureModule.java     # Block capture coordination
│   ├── BlockCapture.java      # Individual block capture logic
│   ├── ChunkCapture.java      # Chunk-based capture optimization
│   └── CaptureValidator.java  # Validate captured blocks
├── placement/
│   ├── PlacementModule.java   # Block placement coordination
│   ├── BlockPlacer.java       # Individual block placement logic
│   ├── PlacementQueue.java    # Async placement queue
│   └── PlacementValidator.java # Validate block placement
└── synchronization/
    ├── SyncModule.java        # Movement synchronization
    ├── MovementSync.java      # Sync island with turtle movement
    └── PerformanceManager.java # Performance optimization
```

**Implementation Details:**
- **Block Capture**: Capture island blocks before turtle movement
- **Block Placement**: Place blocks at new turtle position
- **Performance**: Async processing and chunk-based operations
- **Synchronization**: Keep island perfectly aligned with turtle

**Dependencies**: Phase 2 (movement), Phase 4 (island system)
**Status**: Not started

---

### **Phase 6: Explosion & Destruction System**
**Goal**: Implement spectacular island destruction when turtle dies

**Module Structure:**
```
phase6/
├── Phase6Module.java           # Phase coordinator and lifecycle management
├── explosion/
│   ├── ExplosionModule.java   # Explosion system coordination
│   ├── ExplosionManager.java  # Explosion sequence management
│   ├── ExplosionEffects.java  # Visual and audio effects
│   └── ExplosionPhysics.java  # Block destruction physics
├── destruction/
│   ├── DestructionModule.java # Destruction coordination
│   ├── IslandDestroyer.java   # Island destruction logic
│   ├── BlockScatter.java      # Scattered block physics
│   └── DropManager.java       # Item drop handling
└── effects/
    ├── EffectsModule.java     # Visual effects coordination
    ├── ParticleEffects.java   # Particle system integration
    ├── SoundEffects.java      # Sound effect management
    └── ScreenShake.java       # Camera shake effects
```

**Implementation Details:**
- **Death Detection**: Listen for turtle death events
- **Explosion Sequence**: Multi-stage explosion with escalating effects
- **Block Physics**: Realistic block scattering and destruction
- **Visual Effects**: Particles, sounds, and screen effects

**Dependencies**: Phase 1 (entity), Phase 4 (island system)
**Status**: Not started

---

### **Phase 7: Spawn Control & Population Management**
**Goal**: Implement intelligent spawning and population limits

**Module Structure:**
```
phase7/
├── Phase7Module.java           # Phase coordinator and lifecycle management
├── spawning/
│   ├── SpawningModule.java    # Spawn system coordination
│   ├── SpawnController.java   # Intelligent spawn control
│   ├── BiomeSpawning.java     # Biome-specific spawn rules
│   └── SpawnConditions.java   # Environmental spawn conditions
├── population/
│   ├── PopulationModule.java  # Population management
│   ├── PopulationTracker.java # Track turtle populations
│   ├── PopulationLimits.java  # Enforce population limits
│   └── DistributionManager.java # Geographic distribution
└── migration/
    ├── MigrationModule.java   # Migration system coordination
    ├── MigrationRoutes.java   # Predefined migration paths
    └── SeasonalMigration.java # Time-based migration patterns
```

**Implementation Details:**
- **Smart Spawning**: Prevent overcrowding and ensure proper distribution
- **Population Limits**: Configurable limits per dimension/biome
- **Migration Patterns**: Seasonal or event-based turtle migration
- **Spawn Conditions**: Complex environmental requirements

**Dependencies**: Phase 1 (entity), Phase 2 (movement)
**Status**: Not started

---

### **Phase 8: Configuration & Balancing**
**Goal**: Comprehensive configuration system and gameplay balancing

**Module Structure:**
```
phase8/
├── Phase8Module.java           # Phase coordinator and lifecycle management
├── config/
│   ├── ConfigModule.java      # Configuration system coordination
│   ├── GameplayConfig.java    # Gameplay balance settings
│   ├── PerformanceConfig.java # Performance optimization settings
│   └── ConfigValidator.java   # Configuration validation
├── balancing/
│   ├── BalancingModule.java   # Balance system coordination
│   ├── EntityBalance.java     # Entity stats and behavior balance
│   ├── SpawnBalance.java      # Spawn rate and distribution balance
│   └── PerformanceBalance.java # Performance vs features balance
└── presets/
    ├── PresetModule.java      # Configuration preset coordination
    ├── PresetManager.java     # Preset loading and saving
    └── DefaultPresets.java    # Built-in configuration presets
```

**Implementation Details:**
- **Comprehensive Config**: All aspects configurable via JSON
- **Runtime Updates**: Hot-reload configuration changes
- **Balance Presets**: Easy, Normal, Hard, Custom difficulty presets
- **Performance Tuning**: Configurable performance optimizations

**Dependencies**: All previous phases (configuration affects everything)
**Status**: Not started

---

### **Phase 9: Polish & Optimization**
**Goal**: Performance optimization, bug fixes, and user experience improvements

**Module Structure:**
```
phase9/
├── Phase9Module.java           # Phase coordinator and lifecycle management
├── performance/
│   ├── PerformanceModule.java # Performance system coordination
│   ├── ChunkOptimization.java # Chunk loading optimization
│   ├── RenderOptimization.java # Client rendering optimization
│   └── MemoryOptimization.java # Memory usage optimization
├── polish/
│   ├── PolishModule.java      # Polish system coordination
│   ├── AnimationPolish.java   # Smooth animations and transitions
│   ├── EffectPolish.java      # Enhanced visual effects
│   └── SoundPolish.java       # Audio improvements
└── debugging/
    ├── DebuggingModule.java   # Debug system coordination
    ├── DebugRenderer.java     # Visual debugging tools
    ├── PerformanceProfiler.java # Performance profiling
    └── LoggingEnhancement.java # Enhanced logging system
```

**Implementation Details:**
- **Performance Profiling**: Identify and fix performance bottlenecks
- **Visual Polish**: Smooth animations, better effects, improved models
- **Debug Tools**: In-game debugging and profiling tools
- **Code Optimization**: Refactor and optimize existing code

**Dependencies**: All previous phases (optimization affects everything)
**Status**: Not started

---

### **Phase 10: Advanced Features & Integration**
**Goal**: Advanced features, mod compatibility, and ecosystem integration

**Module Structure:**
```
phase10/
├── Phase10Module.java          # Phase coordinator and lifecycle management
├── advanced/
│   ├── AdvancedModule.java    # Advanced features coordination
│   ├── MultiTurtle.java       # Multiple turtle interactions
│   ├── TurtleEvolution.java   # Turtle growth and evolution
│   └── IslandCustomization.java # Advanced island features
├── integration/
│   ├── IntegrationModule.java # Mod integration coordination
│   ├── BiomeModIntegration.java # Biome mod compatibility
│   ├── StructureModIntegration.java # Structure mod compatibility
│   └── ApiIntegration.java    # External API integration
├── ecosystem/
│   ├── EcosystemModule.java   # Ecosystem coordination
│   ├── IslandEcosystem.java   # Island-specific ecosystems
│   ├── MarineLife.java        # Ocean ecosystem integration
│   └── WeatherIntegration.java # Weather system integration
└── api/
    ├── ApiModule.java         # Public API coordination
    ├── AethelonApi.java       # Main API interface
    ├── EventApi.java          # Event system API
    └── DataApi.java           # Data access API
```

**Implementation Details:**
- **Multi-Turtle Systems**: Turtle herds, interactions, and social behavior
- **Mod Compatibility**: Integration with popular biome and structure mods
- **Public API**: Allow other mods to interact with Aethelon
- **Advanced Ecosystems**: Complex island and ocean ecosystems

**Dependencies**: All previous phases (builds upon complete foundation)
**Status**: Not started

---

## Module Dependencies

**Dependency Graph:**
```
Core System (always enabled)
├── Phase 1: Entity Foundation
│   ├── Phase 2: Behavior & Movement
│   │   ├── Phase 3: Damage & Interaction
│   │   ├── Phase 5: Dynamic Movement (also needs Phase 4)
│   │   └── Phase 7: Spawn Control
│   ├── Phase 4: Island System
│   │   ├── Phase 5: Dynamic Movement (also needs Phase 2)
│   │   └── Phase 6: Explosion System
│   ├── Phase 8: Configuration (affects all phases)
│   ├── Phase 9: Polish & Optimization (affects all phases)
│   └── Phase 10: Advanced Features (needs most other phases)
```

**Configuration Control:**
- Each phase can be independently enabled/disabled
- Sub-modules within phases can be individually controlled
- Dependency validation ensures required phases are enabled
- Graceful degradation when dependencies are missing

**Next Steps**: Complete Phase 1 client and spawn modules, then proceed with Phase 2 implementation using the established modular patterns.