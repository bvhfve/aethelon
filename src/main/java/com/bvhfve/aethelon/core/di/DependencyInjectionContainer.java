package com.bvhfve.aethelon.core.di;

import com.bvhfve.aethelon.core.AethelonCore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * DependencyInjectionContainer - Lightweight DI container for Aethelon modules
 * 
 * DESIGN PRINCIPLES:
 * - Lightweight and focused on mod-specific needs
 * - Thread-safe for concurrent access during initialization
 * - Supports both singleton and factory patterns
 * - Provides clear lifecycle management
 * - Integrates with existing module system
 * 
 * FEATURES:
 * - Constructor injection via @Inject annotation
 * - Field injection for legacy compatibility
 * - Singleton and prototype scopes
 * - Lazy initialization support
 * - Circular dependency detection
 * - Module-scoped instances
 * 
 * USAGE PATTERNS:
 * - Services: EntityTypeProvider, ConfigManager, RegistryManager
 * - Factories: Entity creation, Item creation
 * - Modules: Phase modules with injected dependencies
 * - Utilities: Shared helper classes
 */
public class DependencyInjectionContainer {
    
    private final Map<Class<?>, ServiceDefinition<?>> services = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> singletonInstances = new ConcurrentHashMap<>();
    private final Map<String, Map<Class<?>, Object>> scopedInstances = new ConcurrentHashMap<>();
    private final Set<Class<?>> currentlyCreating = ConcurrentHashMap.newKeySet();
    
    /**
     * Service scope definitions
     */
    public enum Scope {
        SINGLETON,  // One instance per container
        PROTOTYPE,  // New instance each time
        MODULE      // One instance per module scope
    }
    
    /**
     * Annotation for marking injectable constructors and fields
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
    public @interface Inject {
        /**
         * Optional qualifier for disambiguation
         */
        String value() default "";
        
        /**
         * Whether injection is required (default true)
         */
        boolean required() default true;
    }
    
    /**
     * Annotation for marking service classes
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Service {
        Scope scope() default Scope.SINGLETON;
        String qualifier() default "";
    }
    
    /**
     * Service definition holder
     */
    private static class ServiceDefinition<T> {
        final Class<T> serviceClass;
        final Class<? extends T> implementationClass;
        final Supplier<T> factory;
        final Scope scope;
        final String qualifier;
        
        ServiceDefinition(Class<T> serviceClass, Class<? extends T> implementationClass, 
                         Supplier<T> factory, Scope scope, String qualifier) {
            this.serviceClass = serviceClass;
            this.implementationClass = implementationClass;
            this.factory = factory;
            this.scope = scope;
            this.qualifier = qualifier;
        }
    }
    
    /**
     * Register a service with automatic scope detection
     */
    public <T> void registerService(Class<T> serviceClass, Class<? extends T> implementationClass) {
        Service annotation = implementationClass.getAnnotation(Service.class);
        Scope scope = annotation != null ? annotation.scope() : Scope.SINGLETON;
        String qualifier = annotation != null ? annotation.qualifier() : "";
        
        registerService(serviceClass, implementationClass, scope, qualifier);
    }
    
    /**
     * Register a service with explicit scope
     */
    public <T> void registerService(Class<T> serviceClass, Class<? extends T> implementationClass, 
                                   Scope scope, String qualifier) {
        services.put(serviceClass, new ServiceDefinition<>(
            serviceClass, implementationClass, null, scope, qualifier));
        
        AethelonCore.LOGGER.debug("Registered service: {} -> {} (scope: {}, qualifier: '{}')", 
            serviceClass.getSimpleName(), implementationClass.getSimpleName(), scope, qualifier);
    }
    
    /**
     * Register a service with factory method
     */
    public <T> void registerFactory(Class<T> serviceClass, Supplier<T> factory, 
                                   Scope scope, String qualifier) {
        services.put(serviceClass, new ServiceDefinition<>(
            serviceClass, null, factory, scope, qualifier));
        
        AethelonCore.LOGGER.debug("Registered factory for: {} (scope: {}, qualifier: '{}')", 
            serviceClass.getSimpleName(), scope, qualifier);
    }
    
    /**
     * Register a singleton instance
     */
    public <T> void registerInstance(Class<T> serviceClass, T instance, String qualifier) {
        services.put(serviceClass, new ServiceDefinition<>(
            serviceClass, null, () -> instance, Scope.SINGLETON, qualifier));
        singletonInstances.put(serviceClass, instance);
        
        AethelonCore.LOGGER.debug("Registered instance: {} (qualifier: '{}')", 
            serviceClass.getSimpleName(), qualifier);
    }
    
    /**
     * Get service instance with dependency injection
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        return getService(serviceClass, "");
    }
    
    /**
     * Get service instance with qualifier
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass, String qualifier) {
        ServiceDefinition<?> definition = services.get(serviceClass);
        if (definition == null) {
            throw new IllegalArgumentException("Service not registered: " + serviceClass.getName());
        }
        
        if (!qualifier.equals(definition.qualifier)) {
            throw new IllegalArgumentException("Service qualifier mismatch: expected '" + 
                qualifier + "', found '" + definition.qualifier + "'");
        }
        
        return (T) createInstance(definition, null);
    }
    
    /**
     * Get service instance with module scope
     */
    public <T> T getServiceWithScope(Class<T> serviceClass, String moduleScope) {
        return getService(serviceClass, "", moduleScope);
    }
    
    /**
     * Get service instance with qualifier and module scope
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass, String qualifier, String moduleScope) {
        ServiceDefinition<?> definition = services.get(serviceClass);
        if (definition == null) {
            throw new IllegalArgumentException("Service not registered: " + serviceClass.getName());
        }
        
        return (T) createInstance(definition, moduleScope);
    }
    
    /**
     * Create service instance based on scope
     */
    private Object createInstance(ServiceDefinition<?> definition, String moduleScope) {
        // Check for circular dependencies
        if (currentlyCreating.contains(definition.serviceClass)) {
            throw new IllegalStateException("Circular dependency detected for: " + 
                definition.serviceClass.getName());
        }
        
        try {
            currentlyCreating.add(definition.serviceClass);
            
            switch (definition.scope) {
                case SINGLETON:
                    return singletonInstances.computeIfAbsent(definition.serviceClass, 
                        k -> createNewInstance(definition));
                        
                case MODULE:
                    if (moduleScope == null) {
                        throw new IllegalArgumentException("Module scope required for service: " + 
                            definition.serviceClass.getName());
                    }
                    return scopedInstances.computeIfAbsent(moduleScope, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(definition.serviceClass, k -> createNewInstance(definition));
                        
                case PROTOTYPE:
                default:
                    return createNewInstance(definition);
            }
        } finally {
            currentlyCreating.remove(definition.serviceClass);
        }
    }
    
    /**
     * Create new instance using factory or constructor injection
     */
    private Object createNewInstance(ServiceDefinition<?> definition) {
        if (definition.factory != null) {
            return definition.factory.get();
        }
        
        if (definition.implementationClass == null) {
            throw new IllegalStateException("No implementation or factory for: " + 
                definition.serviceClass.getName());
        }
        
        return createInstanceWithInjection(definition.implementationClass);
    }
    
    /**
     * Create instance using constructor injection
     */
    private <T> T createInstanceWithInjection(Class<T> clazz) {
        try {
            // Find injectable constructor
            Constructor<T> constructor = findInjectableConstructor(clazz);
            
            if (constructor != null) {
                // Inject constructor parameters
                Class<?>[] paramTypes = constructor.getParameterTypes();
                Object[] params = new Object[paramTypes.length];
                
                for (int i = 0; i < paramTypes.length; i++) {
                    params[i] = getService(paramTypes[i]);
                }
                
                T instance = constructor.newInstance(params);
                injectFields(instance);
                return instance;
            } else {
                // Use default constructor and field injection
                T instance = clazz.getDeclaredConstructor().newInstance();
                injectFields(instance);
                return instance;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of: " + clazz.getName(), e);
        }
    }
    
    /**
     * Find constructor marked with @Inject
     */
    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findInjectableConstructor(Class<T> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }
    
    /**
     * Inject fields marked with @Inject
     */
    private void injectFields(Object instance) {
        Class<?> clazz = instance.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    field.setAccessible(true);
                    Inject annotation = field.getAnnotation(Inject.class);
                    
                    Object value = getService(field.getType(), annotation.value());
                    field.set(instance, value);
                } catch (Exception e) {
                    Inject annotation = field.getAnnotation(Inject.class);
                    if (annotation.required()) {
                        throw new RuntimeException("Failed to inject field: " + field.getName(), e);
                    } else {
                        AethelonCore.LOGGER.warn("Optional injection failed for field: {}", field.getName());
                    }
                }
            }
        }
    }
    
    /**
     * Clear module-scoped instances
     */
    public void clearModuleScope(String moduleScope) {
        scopedInstances.remove(moduleScope);
        AethelonCore.LOGGER.debug("Cleared module scope: {}", moduleScope);
    }
    
    /**
     * Clear all instances (for shutdown)
     */
    public void clearAll() {
        singletonInstances.clear();
        scopedInstances.clear();
        currentlyCreating.clear();
        AethelonCore.LOGGER.debug("Cleared all DI container instances");
    }
    
    /**
     * Get registration statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("registeredServices", services.size());
        stats.put("singletonInstances", singletonInstances.size());
        stats.put("moduleScopes", scopedInstances.size());
        stats.put("currentlyCreating", currentlyCreating.size());
        return stats;
    }
}