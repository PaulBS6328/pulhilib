package xyz.paul28.pulhilib.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ICustomModelItem {

    /**
     * Since ItemDisplayContext is a client only class, we mimic the enum
     * and sync it on the mixin, so it's essentially the same thing
     */
    enum ModelContext {
        GUI,
        GUI_OFFHAND,
        GROUND,
        FIXED,
        THIRD_PERSON_LEFT,
        THIRD_PERSON_RIGHT,
        FIRST_PERSON_LEFT,
        FIRST_PERSON_RIGHT,
        HEAD,
        NONE
    }

    /**
     * Returns the model to use for this stack in the given display context.
     * Return null to fall back to vanilla model resolution.
     */
    @Nullable
    ResourceLocation getCustomModel(ItemStack stack, ModelContext context);

    /**
     * Same as above, but also tells you WHO is holding / wearing the stack when that is known
     * (first person, third person and head contexts: the entity being rendered). It is null for
     * GUI, ground and item frame rendering.
     *
     * Override this one when the model depends on the holder. By default it just forwards to the
     * two-argument version, so existing items don't need to change.
     */
    @Nullable
    default ResourceLocation getCustomModel(ItemStack stack, ModelContext context, @Nullable LivingEntity holder) {
        return getCustomModel(stack, context);
    }
}