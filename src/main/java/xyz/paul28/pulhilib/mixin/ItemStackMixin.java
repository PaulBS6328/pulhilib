package xyz.paul28.pulhilib.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.paul28.pulhilib.Pulhilib;
import xyz.paul28.pulhilib.common.item.ICustomRarityItem;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
   @Inject(
           method = "getHoverName",
           at = @At("RETURN"),
           cancellable = true
   )
   private void pulhilib$customRarity(CallbackInfoReturnable<Component> cir) {
      ItemStack stack = (ItemStack) (Object) this;

      if (stack.getItem() instanceof ICustomRarityItem customItem) {
         Pulhilib.Rarity rarity = customItem.getModRarity(stack);

         Component name = cir.getReturnValue();

         cir.setReturnValue(
                 name.copy().withStyle(
                         Style.EMPTY.withColor(rarity.getColor())
                 )
         );
      }
   }
}
