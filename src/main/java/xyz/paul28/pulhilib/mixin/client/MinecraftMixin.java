package xyz.paul28.pulhilib.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;


@Mixin({Minecraft.class})
public abstract class MinecraftMixin {
   @Shadow
   public LocalPlayer player;

   @Inject(
           method = {"startAttack"},
           at = {@At("HEAD")},
           cancellable = true
   )
   private void pulhilib$cancelSwingAnimation(CallbackInfoReturnable<Boolean> cir) {
      if (this.player != null && !this.player.isSpectator()) {
         ItemStack stack = this.player.getMainHandItem();
         Item item = stack.getItem();
         if (item instanceof AbstractWeaponItem) {
            AbstractWeaponItem weapon = (AbstractWeaponItem)item;
            if (weapon.shouldCancelSwing(stack, this.player)) {
               cir.cancel();
            }
         }
      }
   }
}
