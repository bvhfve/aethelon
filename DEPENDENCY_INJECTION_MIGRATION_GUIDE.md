# Dependency Injection Migration Guide

## Overview

This guide outlines the migration strategy from static dependencies to a modern dependency injection (DI) architecture for the Aethelon mod. The migration is designed to be gradual, maintaining backward compatibility while introducing modern patterns.

## Current Problems with Static Dependencies

### 1. **Initialization Order Issues**
```java
// PROBLEM: Static access without guaranteed initialization
EntityTypeProvider.getAethelonEntityType() // Throws if not created yet
AethelonConfig.INSTANCE.phases.phase1     // NullPointerException if not initialized
```

### 2. **Testing Difficulties**
```java
// PROBLEM: Hard to mock static dependencies
public class SpawnModule {
    private void configureSpawning() {
        // Cannot mock EntityTypeProvider for testing
        EntityType<?> type = EntityTypeProvider.getAethelonEntityType();
    }
}
```

### 3. **Tight Coupling**
```java
// PROBLEM: Direct static dependencies create tight coupling
AethelonCore.getConfigManager()    // Hard-coded dependency
AethelonCore.getRegistryManager()  // Cannot substitute implementations
```

## Dependency Injection Solution

### 1. **Service Interfaces**
```java
// SOLUTION: Interface-based services
public interface EntityTypeService {
    EntityType<AethelonEntity> createAethelonEntityType();
    EntityType<AethelonEntity> getAethelonEntityType();
    boolean isEntityTypeCreated();
}
```

### 2. **Dependency Injection**
```java
// SOLUTION: Injected dependencies
public class SpawnModuleDI implements InjectableAethelonModule {
    @Inject
    private EntityTypeService entityTypeService;
    
    @Inject
    private ConfigManager configManager;
    
    private void configureSpawning() {
        // Clean, testable, guaranteed initialization
        EntityType<?> type = entityTypeService.getAethelonEntityType();
    }
}
```

### 3. **Proper Lifecycle Management**
```java
// SOLUTION: Service lifecycle with DI container
@Service(scope = Scope.SINGLETON)
public class EntityTypeServiceImpl implements EntityTypeService {
    @Inject
    public EntityTypeServiceImpl(RegistryManager registryManager) {
        // Dependencies guaranteed to be available
    }
}
```

## Migration Strategy

### Phase 1: Foundation (Current)
- ✅ **DI Container**: Lightweight container with annotation support
- ✅ **Service Registry**: Central service registration and discovery
- ✅ **Enhanced Module Interface**: `InjectableAethelonModule` with DI support
- ✅ **Backward Compatibility**: Existing modules continue to work

### Phase 2: Service Migration (Next)
- 🔄 **Core Services**: Migrate ConfigManager, RegistryManager to services
- 🔄 **Entity Services**: Replace EntityTypeProvider with EntityTypeService
- 🔄 **Module Migration**: Convert Phase 1 modules to use DI
- 🔄 **Testing Framework**: Add DI-based testing utilities

### Phase 3: Full DI Adoption (Future)
- ⏳ **New Modules**: All new modules use DI by default
- ⏳ **Legacy Cleanup**: Remove static access patterns
- ⏳ **Performance Optimization**: Optimize DI container for mod loading
- ⏳ **Documentation**: Complete DI usage documentation

### Phase 4: Advanced Features (Long-term)
- ⏳ **Configuration Injection**: Dynamic configuration updates
- ⏳ **Event-Driven Architecture**: DI-based event system
- ⏳ **Plugin System**: Third-party module support via DI
- ⏳ **Hot Reload**: Runtime module replacement with DI

## Implementation Examples

### 1. Service Definition
```java
// Define service interface
public interface SpawnService {
    void configureNaturalSpawning();
    void registerSpawnEgg();
    SpawnConfiguration getConfiguration();
}

// Implement with DI
@Service(scope = Scope.MODULE)
public class SpawnServiceImpl implements SpawnService {
    
    @Inject
    private EntityTypeService entityTypeService;
    
    @Inject
    private ConfigManager configManager;
    
    @Inject
    private RegistryManager registryManager;
    
    @Override
    public void configureNaturalSpawning() {
        // Implementation with injected dependencies
        EntityType<?> entityType = entityTypeService.getAethelonEntityType();
        // ... rest of implementation
    }
}
```

### 2. Module Migration
```java
// Original module (legacy)
public class SpawnModule implements AethelonModule {
    private void registerSpawnEgg() {
        // Static access - fragile
        EntityType<?> type = EntityTypeProvider.getAethelonEntityType();
        ConfigManager config = AethelonCore.getConfigManager();
    }
}

// Migrated module (DI)
public class SpawnModuleDI implements InjectableAethelonModule {
    
    @Inject
    private SpawnService spawnService;
    
    @Override
    public void initializeWithDI(ServiceRegistry serviceRegistry) {
        // Clean, testable implementation
        spawnService.configureNaturalSpawning();
        spawnService.registerSpawnEgg();
    }
    
    @Override
    public List<Class<?>> getRequiredServices() {
        return List.of(SpawnService.class);
    }
}
```

### 3. Testing with DI
```java
// Easy testing with mocked dependencies
@Test
public void testSpawnConfiguration() {
    // Arrange
    EntityTypeService mockEntityService = mock(EntityTypeService.class);
    ConfigManager mockConfig = mock(ConfigManager.class);
    
    DependencyInjectionContainer container = new DependencyInjectionContainer();
    container.registerInstance(EntityTypeService.class, mockEntityService, "test");
    container.registerInstance(ConfigManager.class, mockConfig, "test");
    
    SpawnServiceImpl spawnService = container.getService(SpawnServiceImpl.class);
    
    // Act & Assert
    spawnService.configureNaturalSpawning();
    verify(mockEntityService).getAethelonEntityType();
}
```

## Benefits of Migration

### 1. **Reliability**
- ✅ **Guaranteed Initialization**: Dependencies injected in correct order
- ✅ **Clear Error Messages**: Missing dependencies detected early
- ✅ **Thread Safety**: Proper synchronization in DI container

### 2. **Maintainability**
- ✅ **Loose Coupling**: Interface-based dependencies
- ✅ **Single Responsibility**: Services focused on specific concerns
- ✅ **Clear Dependencies**: Explicit dependency declarations

### 3. **Testability**
- ✅ **Mock Support**: Easy to mock dependencies for testing
- ✅ **Isolated Testing**: Test modules in isolation
- ✅ **Integration Testing**: Test with real or mock services

### 4. **Flexibility**
- ✅ **Multiple Implementations**: Swap implementations via configuration
- ✅ **Conditional Services**: Enable/disable services based on configuration
- ✅ **Scoped Services**: Different service instances per module/phase

## Migration Checklist

### For Each Module:
- [ ] **Identify Dependencies**: List all static dependencies
- [ ] **Create Service Interfaces**: Define clean service contracts
- [ ] **Implement Services**: Create DI-enabled implementations
- [ ] **Update Module**: Convert to `InjectableAethelonModule`
- [ ] **Add Tests**: Create unit tests with mocked dependencies
- [ ] **Update Documentation**: Document service usage

### For Each Service:
- [ ] **Define Interface**: Clear contract with all required methods
- [ ] **Choose Scope**: Singleton, Module, or Prototype
- [ ] **Add Annotations**: `@Service`, `@Inject` where appropriate
- [ ] **Handle Lifecycle**: Proper initialization and cleanup
- [ ] **Add Configuration**: Support for service-specific configuration

## Best Practices

### 1. **Service Design**
```java
// ✅ GOOD: Interface-based, focused responsibility
public interface EntityTypeService {
    EntityType<AethelonEntity> getAethelonEntityType();
    boolean isEntityTypeCreated();
}

// ❌ BAD: Concrete class, multiple responsibilities
public class EntityAndSpawnManager {
    public static EntityType<?> ENTITY_TYPE;
    public static void configureSpawning() { ... }
    public static void registerRenderer() { ... }
}
```

### 2. **Dependency Declaration**
```java
// ✅ GOOD: Constructor injection, immutable dependencies
@Service
public class SpawnServiceImpl implements SpawnService {
    private final EntityTypeService entityTypeService;
    private final ConfigManager configManager;
    
    @Inject
    public SpawnServiceImpl(EntityTypeService entityTypeService, ConfigManager configManager) {
        this.entityTypeService = entityTypeService;
        this.configManager = configManager;
    }
}

// ❌ BAD: Field injection, mutable state
public class SpawnService {
    @Inject
    public EntityTypeService entityTypeService; // Public, mutable
}
```

### 3. **Error Handling**
```java
// ✅ GOOD: Clear error messages, early validation
@Override
public void initializeWithDI(ServiceRegistry serviceRegistry) {
    if (!validateDependencies(serviceRegistry)) {
        throw new IllegalStateException("Required services not available: " + 
            getRequiredServices().stream()
                .filter(service -> !serviceRegistry.isServiceRegistered(service))
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", ")));
    }
}

// ❌ BAD: Generic errors, late failure
public void initialize() {
    EntityTypeProvider.getAethelonEntityType(); // Throws generic IllegalStateException
}
```

## Performance Considerations

### 1. **Service Creation**
- **Singleton Services**: Created once, cached for reuse
- **Module Services**: Created per module scope, cleaned up on module unload
- **Prototype Services**: Created on each request, no caching

### 2. **Dependency Resolution**
- **Eager Loading**: Services created during module initialization
- **Lazy Loading**: Services created on first access
- **Circular Detection**: Prevents infinite loops during creation

### 3. **Memory Management**
- **Scope Cleanup**: Module-scoped services cleaned up when module unloads
- **Weak References**: Optional for prototype services to prevent memory leaks
- **Service Lifecycle**: Proper shutdown hooks for resource cleanup

## Future Enhancements

### 1. **Configuration Integration**
```java
// Future: Configuration injection
@Service
public class SpawnServiceImpl implements SpawnService {
    @Inject @Config("phase1.spawn")
    private SpawnConfiguration config;
    
    @ConfigChangeListener("phase1.spawn.spawnWeight")
    public void onSpawnWeightChanged(int newWeight) {
        // React to configuration changes
    }
}
```

### 2. **Event System Integration**
```java
// Future: Event-driven architecture
@Service
public class SpawnServiceImpl implements SpawnService {
    @Inject
    private EventBus eventBus;
    
    @EventHandler
    public void onEntityTypeCreated(EntityTypeCreatedEvent event) {
        // React to entity type creation
        configureNaturalSpawning();
    }
}
```

### 3. **Plugin System**
```java
// Future: Third-party module support
@PluginService
public interface CustomSpawnProvider {
    void configureCustomSpawning(EntityType<?> entityType);
}

// Third-party implementations automatically discovered and injected
```

This migration guide provides a comprehensive roadmap for transitioning to a modern, maintainable, and testable dependency injection architecture while preserving the existing functionality and ensuring a smooth migration path.