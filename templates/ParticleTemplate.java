package com.bvhfve.aethelon.phase{PHASE_NUMBER}.{MODULE_NAME}.particle;

import com.bvhfve.aethelon.core.AethelonCore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

/**
 * {CLASS_NAME} - {PARTICLE_DESCRIPTION}
 * 
 * MINECRAFT INTEGRATION:
 * - Extends: SpriteBillboardParticle (Client-side particle system)
 * - Uses: Particle rendering pipeline, sprite animation, physics simulation
 * - Hooks into: Particle spawning, rendering, world effects
 * - Modifies: Visual effects, atmospheric rendering, feedback systems
 * 
 * PARTICLE ROLE:
 * - Purpose: {PARTICLE_PURPOSE}
 * - Behavior: {PARTICLE_BEHAVIOR}
 * - Lifetime: {PARTICLE_LIFETIME} ticks
 * - Physics: {PARTICLE_PHYSICS}
 * - Rendering: {PARTICLE_RENDERING}
 * 
 * VERSION COMPATIBILITY:
 * - Minecraft: 1.21.4+
 * - Fabric API: 0.119.2+
 * - Breaking changes: Particle system changes may affect visual effects
 * 
 * CLIENT-SIDE ONLY:
 * This class is only loaded on the client side. Server-side code should
 * not reference this class directly.
 * 
 * IMPLEMENTATION NOTES:
 * {IMPLEMENTATION_NOTES}
 */
@Environment(EnvType.CLIENT)
public class {CLASS_NAME} extends SpriteBillboardParticle {
    
    private static final String MODULE_NAME = "phase{PHASE_NUMBER}.{MODULE_NAME}";
    
    // Particle configuration
    private static final int DEFAULT_LIFETIME = {DEFAULT_LIFETIME};
    private static final float DEFAULT_SCALE = {DEFAULT_SCALE}f;
    private static final float GRAVITY_STRENGTH = {GRAVITY_STRENGTH}f;
    
    // Animation properties
    private final float initialScale;
    private final float targetScale;
    private final boolean fadeOut;
    private final boolean fadeIn;
    
    // Physics properties
    private float rotationSpeed;
    private float initialRotation;
    
    // TODO: Add custom properties
    // Examples:
    // private final Vec3d targetPosition;
    // private final float oscillationAmplitude;
    // private final float oscillationFrequency;
    // private final boolean followTarget;
    // private final int colorChangeRate;
    
    protected {CLASS_NAME}(ClientWorld world, double x, double y, double z, 
                          double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        
        // Set basic properties
        this.maxAge = DEFAULT_LIFETIME + world.random.nextInt(20) - 10; // Randomize lifetime
        this.scale = DEFAULT_SCALE;
        this.gravityStrength = GRAVITY_STRENGTH;
        
        // Initialize animation properties
        this.initialScale = DEFAULT_SCALE;
        this.targetScale = DEFAULT_SCALE * (0.5f + world.random.nextFloat() * 0.5f);
        this.fadeOut = true;
        this.fadeIn = true;
        
        // Initialize physics properties
        this.rotationSpeed = (world.random.nextFloat() - 0.5f) * 0.2f;
        this.initialRotation = world.random.nextFloat() * (float) Math.PI * 2;
        
        // Set initial color and alpha
        setColor(1.0f, 1.0f, 1.0f);
        setAlpha(fadeIn ? 0.0f : 1.0f);
        
        // TODO: Initialize custom properties
        // Examples:
        // this.targetPosition = new Vec3d(x + velocityX * 20, y + velocityY * 20, z + velocityZ * 20);
        // this.oscillationAmplitude = 0.1f + world.random.nextFloat() * 0.2f;
        // this.oscillationFrequency = 0.05f + world.random.nextFloat() * 0.1f;
        
        AethelonCore.LOGGER.debug("Created {CLASS_NAME} particle at ({}, {}, {})", x, y, z);
    }
    
    @Override
    public void tick() {
        // Store previous position
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        
        // Age the particle
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        
        // Update physics
        updatePhysics();
        
        // Update animation
        updateAnimation();
        
        // Update color and alpha
        updateAppearance();
        
        // TODO: Add custom tick behavior
        // Examples:
        // updateTargetMovement();
        // updateOscillation();
        // updateColorTransition();
        // checkCollisions();
        
        // Move the particle
        this.move(this.velocityX, this.velocityY, this.velocityZ);
    }
    
    /**
     * Update particle physics
     */
    protected void updatePhysics() {
        // Apply gravity
        this.velocityY -= 0.04 * this.gravityStrength;
        
        // Apply air resistance
        this.velocityX *= 0.98;
        this.velocityY *= 0.98;
        this.velocityZ *= 0.98;
        
        // Update rotation
        this.angle += this.rotationSpeed;
        
        // TODO: Add custom physics
        // Examples:
        // applyWind();
        // applyMagneticForce();
        // applyBuoyancy();
    }
    
    /**
     * Update particle animation
     */
    protected void updateAnimation() {
        float ageRatio = (float) this.age / (float) this.maxAge;
        
        // Scale animation
        if (ageRatio < 0.1f) {
            // Growing phase
            this.scale = MathHelper.lerp(ageRatio / 0.1f, initialScale * 0.1f, initialScale);
        } else if (ageRatio > 0.8f) {
            // Shrinking phase
            float shrinkRatio = (ageRatio - 0.8f) / 0.2f;
            this.scale = MathHelper.lerp(shrinkRatio, initialScale, targetScale);
        }
        
        // TODO: Add custom animations
        // Examples:
        // updatePulseAnimation(ageRatio);
        // updateSpiralAnimation(ageRatio);
        // updateFlickerAnimation(ageRatio);
    }
    
    /**
     * Update particle appearance (color and alpha)
     */
    protected void updateAppearance() {
        float ageRatio = (float) this.age / (float) this.maxAge;
        
        // Alpha animation
        if (fadeIn && ageRatio < 0.2f) {
            // Fade in
            setAlpha(ageRatio / 0.2f);
        } else if (fadeOut && ageRatio > 0.7f) {
            // Fade out
            float fadeRatio = (ageRatio - 0.7f) / 0.3f;
            setAlpha(1.0f - fadeRatio);
        } else {
            setAlpha(1.0f);
        }
        
        // TODO: Add color transitions
        // Examples:
        // updateColorGradient(ageRatio);
        // updateTemperatureColor(ageRatio);
        // updateRainbowColor(ageRatio);
    }
    
    @Override
    public ParticleTextureSheet getType() {
        // TODO: Choose appropriate texture sheet
        // Options:
        // - PARTICLE_SHEET_OPAQUE: For solid particles
        // - PARTICLE_SHEET_TRANSLUCENT: For transparent particles
        // - PARTICLE_SHEET_LIT: For particles that should be affected by lighting
        // - CUSTOM: For custom rendering
        
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    /**
     * Set particle color
     * 
     * @param red Red component (0.0-1.0)
     * @param green Green component (0.0-1.0)
     * @param blue Blue component (0.0-1.0)
     */
    protected void setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    /**
     * Set particle alpha
     * 
     * @param alpha Alpha component (0.0-1.0)
     */
    protected void setAlpha(float alpha) {
        this.alpha = MathHelper.clamp(alpha, 0.0f, 1.0f);
    }
    
    // TODO: Add helper methods as needed
    // Examples:
    /*
    protected void updateTargetMovement() {
        // Move towards target position
        if (targetPosition != null) {
            Vec3d direction = targetPosition.subtract(x, y, z).normalize();
            float speed = 0.02f;
            
            velocityX += direction.x * speed;
            velocityY += direction.y * speed;
            velocityZ += direction.z * speed;
        }
    }
    
    protected void updateOscillation() {
        // Add oscillating movement
        float time = age * oscillationFrequency;
        float offset = (float) Math.sin(time) * oscillationAmplitude;
        
        velocityX += offset * 0.01f;
        velocityZ += offset * 0.01f;
    }
    
    protected void updateColorGradient(float ageRatio) {
        // Transition from one color to another
        float startR = 1.0f, startG = 0.5f, startB = 0.0f; // Orange
        float endR = 1.0f, endG = 0.0f, endB = 0.0f;       // Red
        
        setColor(
            MathHelper.lerp(ageRatio, startR, endR),
            MathHelper.lerp(ageRatio, startG, endG),
            MathHelper.lerp(ageRatio, startB, endB)
        );
    }
    
    protected void updateTemperatureColor(float ageRatio) {
        // Color based on temperature (hot to cold)
        if (ageRatio < 0.5f) {
            // Hot: White to Yellow
            float ratio = ageRatio * 2.0f;
            setColor(1.0f, 1.0f, 1.0f - ratio * 0.5f);
        } else {
            // Cool: Yellow to Red
            float ratio = (ageRatio - 0.5f) * 2.0f;
            setColor(1.0f, 1.0f - ratio * 0.5f, 0.5f - ratio * 0.5f);
        }
    }
    
    protected void applyWind() {
        // Apply wind force
        velocityX += (world.random.nextFloat() - 0.5f) * 0.001f;
        velocityZ += (world.random.nextFloat() - 0.5f) * 0.001f;
    }
    
    protected void checkCollisions() {
        // Check for block collisions
        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
        if (!world.getBlockState(pos).isAir()) {
            // Bounce or stick to surface
            velocityX *= -0.5f;
            velocityY *= -0.5f;
            velocityZ *= -0.5f;
        }
    }
    */
    
    /**
     * Factory class for creating particles
     */
    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;
        
        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        
        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, 
                                     double x, double y, double z, 
                                     double velocityX, double velocityY, double velocityZ) {
            
            // Check if module is enabled
            if (!AethelonCore.getConfigManager().isModuleEnabled("phase{PHASE_NUMBER}", "{MODULE_NAME}")) {
                return null;
            }
            
            {CLASS_NAME} particle = new {CLASS_NAME}(world, x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(this.spriteProvider);
            
            return particle;
        }
    }
}