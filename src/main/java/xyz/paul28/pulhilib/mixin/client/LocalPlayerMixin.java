package xyz.paul28.pulhilib.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;

@Mixin({LocalPlayer.class})
public class LocalPlayerMixin {

   @Inject(
           method = {"swing(Lnet/minecraft/world/InteractionHand;)V"},
           at = {@At("HEAD")},
           cancellable = true
   )
   private void cancelGunSwing(InteractionHand pHand, CallbackInfo ci) {
      LocalPlayer player = (LocalPlayer)(Object)this;
      ItemStack stack = player.getItemInHand(pHand);
      Item item = stack.getItem();
      if (item instanceof AbstractWeaponItem) {
         AbstractWeaponItem weapon = (AbstractWeaponItem)item;
         if (weapon.shouldCancelSwing(stack, player)) {
            ci.cancel();
         }
      }

   }
}
