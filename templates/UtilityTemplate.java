package com.bvhfve.aethelon.phase{PHASE_NUMBER}.{MODULE_NAME}.util;

import com.bvhfve.aethelon.core.AethelonCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * {CLASS_NAME} - {UTILITY_DESCRIPTION}
 * 
 * MINECRAFT INTEGRATION:
 * - Uses: Minecraft utility classes and math functions
 * - Hooks into: World querying, entity management, calculations
 * - Modifies: Provides helper functions for common operations
 * 
 * UTILITY ROLE:
 * - Purpose: {UTILITY_PURPOSE}
 * - Functions: {UTILITY_FUNCTIONS}
 * - Scope: {UTILITY_SCOPE}
 * - Performance: {PERFORMANCE_NOTES}
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: API changes may affect utility functions
 * 
 * IMPLEMENTATION NOTES:
 * {IMPLEMENTATION_NOTES}
 */
public class {CLASS_NAME} {
    
    private static final String MODULE_NAME = "phase{PHASE_NUMBER}.{MODULE_NAME}";
    
    // Constants for common calculations
    private static final double SQRT_2 = Math.sqrt(2.0);
    private static final double SQRT_3 = Math.sqrt(3.0);
    
    // TODO: Add utility constants
    // Examples:
    // private static final int DEFAULT_SEARCH_RADIUS = 16;
    // private static final double INTERACTION_RANGE = 6.0;
    // private static final float DEFAULT_SPEED_MULTIPLIER = 1.0f;
    
    /**
     * Private constructor to prevent instantiation
     */
    private {CLASS_NAME}() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Find entities within a radius of a position
     * 
     * @param world The world to search in
     * @param center Center position
     * @param radius Search radius
     * @param predicate Filter predicate for entities
     * @return List of matching entities
     */
    public static List<Entity> findEntitiesInRadius(World world, Vec3d center, double radius, 
                                                   Predicate<Entity> predicate) {
        if (!AethelonCore.getConfigManager().isModuleEnabled("phase{PHASE_NUMBER}", "{MODULE_NAME}")) {
            return List.of();
        }
        
        try {
            return world.getOtherEntities(null, 
                net.minecraft.util.math.Box.of(center, radius * 2, radius * 2, radius * 2),
                entity -> {
                    if (entity == null) return false;
                    
                    double distance = entity.getPos().distanceTo(center);
                    return distance <= radius && predicate.test(entity);
                });
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Error finding entities in radius", e);
            return List.of();
        }
    }
    
    /**
     * Find the nearest entity matching a predicate
     * 
     * @param world The world to search in
     * @param center Center position
     * @param maxRadius Maximum search radius
     * @param predicate Filter predicate for entities
     * @return Optional containing the nearest entity, or empty if none found
     */
    public static Optional<Entity> findNearestEntity(World world, Vec3d center, double maxRadius,
                                                    Predicate<Entity> predicate) {
        List<Entity> entities = findEntitiesInRadius(world, center, maxRadius, predicate);
        
        return entities.stream()
            .min((e1, e2) -> Double.compare(
                e1.getPos().distanceTo(center),
                e2.getPos().distanceTo(center)
            ));
    }
    
    /**
     * Check if a position is within interaction range of a player
     * 
     * @param player The player
     * @param position The position to check
     * @param maxRange Maximum interaction range
     * @return true if within range
     */
    public static boolean isWithinInteractionRange(PlayerEntity player, Vec3d position, double maxRange) {
        if (player == null || position == null) {
            return false;
        }
        
        double distance = player.getPos().distanceTo(position);
        return distance <= maxRange;
    }
    
    /**
     * Calculate the direction vector from one position to another
     * 
     * @param from Starting position
     * @param to Target position
     * @return Normalized direction vector
     */
    public static Vec3d getDirectionVector(Vec3d from, Vec3d to) {
        if (from == null || to == null) {
            return Vec3d.ZERO;
        }
        
        Vec3d direction = to.subtract(from);
        return direction.length() > 0 ? direction.normalize() : Vec3d.ZERO;
    }
    
    /**
     * Calculate the 2D distance between two positions (ignoring Y coordinate)
     * 
     * @param pos1 First position
     * @param pos2 Second position
     * @return 2D distance
     */
    public static double getDistance2D(Vec3d pos1, Vec3d pos2) {
        if (pos1 == null || pos2 == null) {
            return Double.MAX_VALUE;
        }
        
        double dx = pos1.x - pos2.x;
        double dz = pos1.z - pos2.z;
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    /**
     * Check if a block position is loaded in the world
     * 
     * @param world The world
     * @param pos Block position
     * @return true if the chunk containing the position is loaded
     */
    public static boolean isPositionLoaded(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }
        
        return world.isChunkLoaded(pos);
    }
    
    /**
     * Get a safe spawn position near a target position
     * 
     * @param world The world
     * @param target Target position
     * @param radius Search radius for safe positions
     * @return Safe spawn position, or target position if none found
     */
    public static BlockPos getSafeSpawnPosition(World world, BlockPos target, int radius) {
        if (world == null || target == null) {
            return target;
        }
        
        // Check if target position is already safe
        if (isSafeSpawnPosition(world, target)) {
            return target;
        }
        
        // Search in expanding circles
        for (int r = 1; r <= radius; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (Math.abs(x) != r && Math.abs(z) != r) {
                        continue; // Only check perimeter
                    }
                    
                    BlockPos candidate = target.add(x, 0, z);
                    
                    // Check multiple Y levels
                    for (int y = -2; y <= 2; y++) {
                        BlockPos testPos = candidate.add(0, y, 0);
                        if (isSafeSpawnPosition(world, testPos)) {
                            return testPos;
                        }
                    }
                }
            }
        }
        
        // No safe position found, return target
        return target;
    }
    
    /**
     * Check if a position is safe for entity spawning
     * 
     * @param world The world
     * @param pos Position to check
     * @return true if position is safe for spawning
     */
    public static boolean isSafeSpawnPosition(World world, BlockPos pos) {
        if (world == null || pos == null || !isPositionLoaded(world, pos)) {
            return false;
        }
        
        try {
            // Check if there's solid ground
            if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
                return false;
            }
            
            // Check if spawn space is clear
            if (!world.getBlockState(pos).isAir() || !world.getBlockState(pos.up()).isAir()) {
                return false;
            }
            
            // TODO: Add additional safety checks
            // Examples:
            // - Check for lava/water
            // - Check for dangerous blocks
            // - Check light level
            // - Check for hostile entities nearby
            
            return true;
        } catch (Exception e) {
            AethelonCore.LOGGER.error("Error checking spawn position safety", e);
            return false;
        }
    }
    
    /**
     * Clamp a value between minimum and maximum bounds
     * 
     * @param value Value to clamp
     * @param min Minimum bound
     * @param max Maximum bound
     * @return Clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamp a value between minimum and maximum bounds
     * 
     * @param value Value to clamp
     * @param min Minimum bound
     * @param max Maximum bound
     * @return Clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamp a value between minimum and maximum bounds
     * 
     * @param value Value to clamp
     * @param min Minimum bound
     * @param max Maximum bound
     * @return Clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Linear interpolation between two values
     * 
     * @param start Start value
     * @param end End value
     * @param factor Interpolation factor (0.0 to 1.0)
     * @return Interpolated value
     */
    public static double lerp(double start, double end, double factor) {
        return start + (end - start) * clamp(factor, 0.0, 1.0);
    }
    
    /**
     * Linear interpolation between two values
     * 
     * @param start Start value
     * @param end End value
     * @param factor Interpolation factor (0.0 to 1.0)
     * @return Interpolated value
     */
    public static float lerp(float start, float end, float factor) {
        return start + (end - start) * clamp(factor, 0.0f, 1.0f);
    }
    
    /**
     * Convert degrees to radians
     * 
     * @param degrees Angle in degrees
     * @return Angle in radians
     */
    public static double toRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }
    
    /**
     * Convert radians to degrees
     * 
     * @param radians Angle in radians
     * @return Angle in degrees
     */
    public static double toDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }
    
    /**
     * Normalize an angle to be between -180 and 180 degrees
     * 
     * @param angle Angle in degrees
     * @return Normalized angle
     */
    public static double normalizeAngle(double angle) {
        angle = angle % 360.0;
        if (angle > 180.0) {
            angle -= 360.0;
        } else if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }
    
    /**
     * Check if a value is approximately equal to another value within a tolerance
     * 
     * @param a First value
     * @param b Second value
     * @param tolerance Tolerance for comparison
     * @return true if values are approximately equal
     */
    public static boolean approximately(double a, double b, double tolerance) {
        return Math.abs(a - b) <= tolerance;
    }
    
    /**
     * Check if a value is approximately equal to another value within a default tolerance
     * 
     * @param a First value
     * @param b Second value
     * @return true if values are approximately equal
     */
    public static boolean approximately(double a, double b) {
        return approximately(a, b, 1e-9);
    }
    
    // TODO: Add more utility methods as needed
    // Examples:
    /*
    public static Vec3d getRandomPositionInRadius(Vec3d center, double radius, Random random) {
        // Generate random position within radius
    }
    
    public static boolean hasLineOfSight(World world, Vec3d from, Vec3d to) {
        // Check if there's a clear line of sight between two positions
    }
    
    public static List<BlockPos> getBlocksInArea(BlockPos corner1, BlockPos corner2) {
        // Get all block positions in a rectangular area
    }
    
    public static String formatTime(long milliseconds) {
        // Format time duration as human-readable string
    }
    
    public static String formatDistance(double distance) {
        // Format distance as human-readable string
    }
    */
}