package xyz.paul28.pulhilib.common.util;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import xyz.paul28.pulhilib.common.item.IHitscanConvertible;
import xyz.paul28.pulhilib.common.particle.HitscanBeamOptions;
import xyz.paul28.pulhilib.init.PulhilibParticleTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class HitscanUtils {
    public static void explode(Level level, Vec3 center,
                               float damageRadius, float damage,
                               float knockbackRadius, float knockbackStrength,
                               Entity source) {
        explode(level, center, damageRadius, damage, knockbackRadius, knockbackStrength, source, null);
    }

    public static void explode(Level level, Vec3 center,
                               float damageRadius, float damage,
                               float knockbackRadius, float knockbackStrength,
                               Entity source, @Nullable Entity excludeFromEffects) {
        if (damageRadius <= 0 && knockbackRadius <= 0) return;

        float maxRadius = Math.max(damageRadius, knockbackRadius);
        AABB area = new AABB(
                center.x - maxRadius, center.y - maxRadius, center.z - maxRadius,
                center.x + maxRadius, center.y + maxRadius, center.z + maxRadius
        );

        List<Entity> nearby = level.getEntities((Entity) null, area,
                e -> e instanceof LivingEntity && e.isAlive() && e != excludeFromEffects);

        for (Entity entity : nearby) {
            LivingEntity living = (LivingEntity) entity;
            double dist = living.position().distanceTo(center);

            if (damageRadius > 0 && dist <= damageRadius) {
                if ((damage > 0) && !level.isClientSide()) {
                    living.hurt(source != null
                            ? living.damageSources().explosion(null, source)
                            : living.damageSources().generic(), damage);
                }
            }

            if (knockbackRadius > 0 && dist <= knockbackRadius) {
                if (knockbackStrength > 0) {
                    Vec3 push = living.position().subtract(center);
                    double pushLen = push.length();
                    Vec3 pushNorm = pushLen > 1.0E-4 ? push.scale(1.0 / pushLen) : new Vec3(0, 1, 0);
                    living.hurtMarked = true;
                    living.hasImpulse = true;
                    living.push(pushNorm.x * knockbackStrength, pushNorm.y * knockbackStrength + 0.1, pushNorm.z * knockbackStrength);
                    living.hurtMarked = true;
                    living.hasImpulse = true;
                }
            }

        }
        if (level.isClientSide()) return;
        level.playSound(null, center.x, center.y, center.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1, 1);
        level.addParticle(ParticleTypes.EXPLOSION, center.x, center.y, center.z, 0, 0, 0);
    }

    public static void convertToHitscan(ServerLevel level, Player player, Entity target, Vec3 impactPos) {
        float damage;
        float explosionDamageRadius;
        float explosionDamage;
        float explosionKnockbackRadius;
        float explosionKnockbackStrength;
        int beamColor;

        if (target instanceof IHitscanConvertible convertible) {
            damage = convertible.getHitscanDamage();
            explosionDamageRadius = convertible.getHitscanExplosionDamageRadius();
            explosionDamage = convertible.getHitscanExplosionDamage();
            explosionKnockbackRadius = convertible.getHitscanExplosionKnockbackRadius();
            explosionKnockbackStrength = convertible.getHitscanExplosionKnockbackStrength();
            beamColor = convertible.getHitscanColor();
        } else if (target instanceof EndCrystal) {
            damage = 0F;
            explosionDamageRadius = 0F;
            explosionDamage = 0F;
            explosionKnockbackRadius = 6.0F;
            explosionKnockbackStrength = 3.5F;
            beamColor = 0xFFAA33;
        } else {
            return;
        }

        level.sendParticles(
                new HitscanBeamOptions(impactPos.x, impactPos.y, impactPos.z, 0.05F, beamColor, 6),
                target.getX(), target.getY(), target.getZ(),
                1, 0, 0, 0, 0);
        level.sendParticles(PulhilibParticleTypes.SHOCKWAVE.get(), impactPos.x, impactPos.y, impactPos.z, 1, 0, 0, 0, 0);

        if (damage > 0 && target instanceof LivingEntity living) {
            living.hurt(player.damageSources().playerAttack(player), damage);
        }

        if (explosionDamageRadius > 0 || explosionKnockbackRadius > 0) {
            explode(level, impactPos, explosionDamageRadius, explosionDamage,
                    explosionKnockbackRadius, explosionKnockbackStrength, player);
        }

        target.discard();
    }

    public static Vec3 traceLookEndpoint(Player player, double maxRange, Predicate<Entity> entityFilter) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 maxReach = eyePos.add(look.scale(maxRange));

        BlockHitResult blockHit = player.level().clip(new ClipContext(
                eyePos, maxReach, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        double blockDistSq = blockHit.getType() != HitResult.Type.MISS
                ? eyePos.distanceToSqr(blockHit.getLocation())
                : maxRange * maxRange;

        Vec3 entitySearchReach = blockHit.getType() != HitResult.Type.MISS ? blockHit.getLocation() : maxReach;
        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(maxRange)).inflate(1.0);

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                player, eyePos, entitySearchReach, searchBox, entityFilter, (float) blockDistSq);

        if (entityHit != null) return entityHit.getLocation();
        if (blockHit.getType() != HitResult.Type.MISS) return blockHit.getLocation();
        return maxReach;
    }

    public static HitResult traceHit(
            Player player,
            Vec3 direction,
            double maxRange,
            Predicate<Entity> entityFilter
    ) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 look = direction.normalize();
        Vec3 maxReach = eyePos.add(look.scale(maxRange));

        BlockHitResult blockHit = player.level().clip(
                new ClipContext(
                        eyePos,
                        maxReach,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        player
                )
        );

        double blockDistSq = blockHit.getType() != HitResult.Type.MISS
                ? eyePos.distanceToSqr(blockHit.getLocation())
                : maxRange * maxRange;

        Vec3 entitySearchReach = blockHit.getType() != HitResult.Type.MISS
                ? blockHit.getLocation()
                : maxReach;

        AABB searchBox = player.getBoundingBox()
                .expandTowards(look.scale(maxRange))
                .inflate(1.0D);

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                player,
                eyePos,
                entitySearchReach,
                searchBox,
                entityFilter,
                blockDistSq
        );

        return entityHit != null ? entityHit : blockHit;
    }
}