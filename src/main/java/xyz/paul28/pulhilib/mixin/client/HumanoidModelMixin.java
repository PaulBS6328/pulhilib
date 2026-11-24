package xyz.paul28.pulhilib.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.paul28.pulhilib.common.item.AbstractLongswordItem;

@Mixin({HumanoidModel.class})
public abstract class HumanoidModelMixin<T extends LivingEntity> {

   @Unique
   private static final TagKey<Item> pulhilib$LONGSWORDS =
           ItemTags.create(ResourceLocation.fromNamespaceAndPath("pulhilib", "longswords"));

   @Shadow
   @Final
   public ModelPart head;

   @Shadow
   protected abstract HumanoidArm getAttackArm(T entity);

   @Shadow
   protected abstract ModelPart getArm(HumanoidArm arm);

   @Shadow
   @Final
   public ModelPart body;

   @Inject(
           method = "setupAttackAnimation",
           at = @At("TAIL")
   )
   private void pauls_extras$twoHandSwing(T pLivingEntity, float pAgeInTicks, CallbackInfo ci) {
      ItemStack stack = pLivingEntity.getMainHandItem();
      if (!(pLivingEntity instanceof Player player)) return;
      boolean isLongsword = stack.is(pulhilib$LONGSWORDS);
      boolean isTwoHanded = !(stack.getItem() instanceof AbstractLongswordItem longsword)
              || !longsword.isOneHanded(player);
      if (isLongsword && isTwoHanded && pLivingEntity.attackAnim > 0.0F) {
         HumanoidArm offArm = this.getAttackArm(pLivingEntity) == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
         ModelPart offArmPart = this.getArm(offArm);
         float f = 1.0F - pLivingEntity.attackAnim;
         f *= f;
         f *= f;
         f = 1.0F - f;
         float g = Mth.sin(f * (float) Math.PI);
         float h = Mth.sin(pLivingEntity.attackAnim * (float) Math.PI)
                 * -(this.head.xRot - 0.7F) * 0.75F;
         offArmPart.xRot -= g * 1.2F + h;
         offArmPart.yRot += this.body.yRot * 2.0F;
         offArmPart.zRot += Mth.sin(pLivingEntity.attackAnim * (float) Math.PI) * -0.4F;
      }
   }
}
