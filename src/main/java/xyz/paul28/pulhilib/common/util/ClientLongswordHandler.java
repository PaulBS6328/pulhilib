package xyz.paul28.pulhilib.common.util;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import xyz.paul28.pulhilib.common.item.AbstractLongswordItem;

import javax.annotation.Nullable;

@EventBusSubscriber(
        value = {Dist.CLIENT},
        bus = Bus.FORGE
)
public class ClientLongswordHandler {
   @SubscribeEvent
   public static void onRenderPlayer(Pre event) {
      Player entity = event.getEntity();
      HumanoidModel<?> model = event.getRenderer().getModel();
      execute(entity, model);
   }

   public static final ArmPose LONGSWORD_BLOCK = ArmPose.create("LONGSWORD_BLOCK", false, (model, entity, arm) -> {
      if (arm == HumanoidArm.RIGHT) {
         model.rightArm.yRot = -0.1F + model.head.yRot;
         model.rightArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
      } else {
         model.leftArm.yRot = 0.1F + model.head.yRot;
         model.leftArm.xRot = (-(float) Math.PI / 2F) + model.head.xRot;
      }
   });

   private static void execute(@Nullable Player player, @Nullable HumanoidModel<?> model) {
      if (player != null && model != null) {
          ItemStack held = player.getMainHandItem();
          if (held.getItem() instanceof AbstractLongswordItem longswordItem) {
             boolean using = player.isUsingItem()
                     && player.getUsedItemHand() == InteractionHand.MAIN_HAND
                     && player.getUseItem() == held;
             if (player.getMainArm() == HumanoidArm.RIGHT) {
                if (using) {
                   model.rightArmPose = longswordItem.isOneHanded(player) ? LONGSWORD_BLOCK : ArmPose.BOW_AND_ARROW;
                   if (longswordItem.isOneHanded(player)) return;
                   model.leftArmPose = ArmPose.CROSSBOW_HOLD;
                   player.yBodyRot = player.yHeadRot;
                   player.yBodyRotO = player.yHeadRot;
                } else {
                   if (longswordItem.isOneHanded(player)) return;
                   model.rightArmPose = ArmPose.CROSSBOW_CHARGE;
                   model.leftArmPose = ArmPose.CROSSBOW_CHARGE;
                }
             }
             else if (using) {
                model.leftArmPose = longswordItem.isOneHanded(player) ? LONGSWORD_BLOCK : ArmPose.BOW_AND_ARROW;
                if (longswordItem.isOneHanded(player)) return;
                model.rightArmPose = ArmPose.CROSSBOW_HOLD;
                player.yBodyRot = player.yHeadRot;
                player.yBodyRotO = player.yHeadRot;
             } else {
                if (longswordItem.isOneHanded(player)) return;
                model.leftArmPose = ArmPose.CROSSBOW_CHARGE;
                model.rightArmPose = ArmPose.CROSSBOW_CHARGE;
             }
          }
      }
   }
}
