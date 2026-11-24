package xyz.paul28.pulhilib.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Remembers which entity an item is currently being rendered for.
 *
 * ItemRenderer.render(...) - where the custom model swap happens - doesn't know the entity, but
 * renderStatic(LivingEntity, ...) does, and always calls render(...) before returning. So a mixin
 * sets this at the start of renderStatic and clears it at the end. Rendering is single threaded
 * (render thread), so a plain static is fine.
 */
@OnlyIn(Dist.CLIENT)
public final class CustomModelHolder {

    @Nullable
    private static LivingEntity current;

    private CustomModelHolder() {
    }

    public static void set(@Nullable LivingEntity entity) {
        current = entity;
    }

    @Nullable
    public static LivingEntity get() {
        return current;
    }

    public static void clear() {
        current = null;
    }
}