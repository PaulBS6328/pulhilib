package xyz.paul28.pulhilib.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.paul28.pulhilib.client.ChargeBarRenderer;
import xyz.paul28.pulhilib.common.item.IChargeBarItem;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("HEAD")
    )
    private void pulhilib$renderChargeBar(Font font, ItemStack stack, int x, int y, String countString, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!(stack.getItem() instanceof IChargeBarItem chargeBarItem)) return;

        ChargeBarRenderer.render((GuiGraphics) (Object) this, chargeBarItem, mc.player, stack, x, y);
    }
}