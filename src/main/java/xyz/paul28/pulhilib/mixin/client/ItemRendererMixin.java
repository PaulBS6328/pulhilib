package xyz.paul28.pulhilib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.paul28.pulhilib.client.CustomModelHolder;
import xyz.paul28.pulhilib.common.item.ICustomModelItem;
import xyz.paul28.pulhilib.mixin.accessor.ItemRendererAccessor;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {


    @ModifyVariable(
            method = "render",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private BakedModel pauls_extras$customModelLoader(
            BakedModel model,
            ItemStack stack,
            ItemDisplayContext context,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        if (!(stack.getItem() instanceof ICustomModelItem custom)) {
            return model;
        }

        ResourceLocation location = custom.getCustomModel(
                stack,
                pulhilib$toCustomContext(context, stack),
                CustomModelHolder.get()
        );

        if (location == null) {
            return model;
        }

        ItemRendererAccessor renderer = (ItemRendererAccessor) (Object) this;

        return renderer
                .pulhilib$getItemModelShaper()
                .getModelManager()
                .getModel(location);
    }

    private static final String RENDER_STATIC =
            "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;"
                    + "Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V";

    @Inject(method = RENDER_STATIC, at = @At("HEAD"))
    private void pauls_extras$captureHolder(LivingEntity entity, ItemStack stack, ItemDisplayContext ctx,
                                            boolean left, PoseStack poseStack, MultiBufferSource buffer,
                                            Level level, int light, int overlay, int seed, CallbackInfo ci) {
        CustomModelHolder.set(entity);
    }

    @Inject(method = RENDER_STATIC, at = @At("RETURN"))
    private void pauls_extras$releaseHolder(LivingEntity entity, ItemStack stack, ItemDisplayContext ctx,
                                            boolean left, PoseStack poseStack, MultiBufferSource buffer,
                                            Level level, int light, int overlay, int seed, CallbackInfo ci) {
        CustomModelHolder.clear();
    }

    @Unique
    private static ICustomModelItem.ModelContext pulhilib$toCustomContext(
            ItemDisplayContext context, ItemStack stack) {
        return switch (context) {
            case GUI -> {
                var player = net.minecraft.client.Minecraft.getInstance().player;
                yield (player != null && stack == player.getOffhandItem())
                        ? ICustomModelItem.ModelContext.GUI_OFFHAND
                        : ICustomModelItem.ModelContext.GUI;
            }
            case GROUND -> ICustomModelItem.ModelContext.GROUND;
            case FIXED -> ICustomModelItem.ModelContext.FIXED;

            case THIRD_PERSON_LEFT_HAND ->
                    ICustomModelItem.ModelContext.THIRD_PERSON_LEFT;

            case THIRD_PERSON_RIGHT_HAND ->
                    ICustomModelItem.ModelContext.THIRD_PERSON_RIGHT;

            case FIRST_PERSON_LEFT_HAND ->
                    ICustomModelItem.ModelContext.FIRST_PERSON_LEFT;

            case FIRST_PERSON_RIGHT_HAND ->
                    ICustomModelItem.ModelContext.FIRST_PERSON_RIGHT;

            case HEAD ->
                    ICustomModelItem.ModelContext.HEAD;

            default ->
                    ICustomModelItem.ModelContext.NONE;
        };
    }
}