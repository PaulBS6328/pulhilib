package xyz.paul28.pulhilib.common.item;

/**
 * Implemented by projectiles that the Jackhammer (or other weapons) can convert
 * into a HITSCAN on impact — e.g. shooting an arrow mid-flight turns it into an
 * instant beam-damage-explosion instead of letting it continue as a projectile.
 */
public interface IHitscanConvertible {

    float getHitscanDamage();

    /** 0 = no AoE damage from the resulting hitscan explosion. */
    default float getHitscanExplosionDamageRadius() { return 0; }

    default float getHitscanExplosionDamage() { return 0; }

    /** 0 = no knockback from the resulting hitscan explosion. */
    default float getHitscanExplosionKnockbackRadius() { return 0; }

    default float getHitscanExplosionKnockbackStrength() { return 0; }

    default int getHitscanColor() { return 0xFFFFFF; }
}