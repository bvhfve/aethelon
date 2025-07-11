package com.bvhfve.aethelon;

import com.bvhfve.aethelon.core.AethelonCore;

/**
 * Aethelon - Legacy main mod class (redirects to modular core)
 * 
 * MINECRAFT INTEGRATION:
 * - Implements: ModInitializer (Fabric API entry point)
 * - Hooks into: Fabric mod loading lifecycle
 * - Modifies: None (delegates to AethelonCore)
 * 
 * MODULE ROLE:
 * - Purpose: Maintain compatibility with existing fabric.mod.json entry point
 * - Dependencies: AethelonCore
 * - Provides: Legacy entry point, delegation to modular system
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: None (stable delegation pattern)
 * 
 * REFACTORING NOTE:
 * This class now serves as a compatibility layer that delegates to the new
 * modular AethelonCore system. This allows existing configurations to work
 * while providing the new modular architecture benefits.
 */
public class Aethelon extends AethelonCore {
    // This class now extends AethelonCore to maintain compatibility
    // while providing the new modular architecture
}