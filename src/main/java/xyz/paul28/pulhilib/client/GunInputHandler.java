package xyz.paul28.pulhilib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import xyz.paul28.pulhilib.Pulhilib;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;
import xyz.paul28.pulhilib.common.network.GunNetworking;

@EventBusSubscriber(
   modid = Pulhilib.MODID,
   value = {Dist.CLIENT}
)
public final class GunInputHandler {
   private static boolean lastPrimary = false;
   private static boolean lastAlt = false;
   private static boolean lastOffhandAlt = false;
   private static int primaryHoldTicks = 0;
   private static int altHoldTicks = 0;
   private static int offhandAltHoldTicks = 0;

   @SubscribeEvent
   public static void onClientTick(TickEvent.ClientTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null && mc.screen == null) {
            Player player = mc.player;
            ItemStack mainStack = player.getMainHandItem();
            ItemStack offStack = player.getOffhandItem();

            boolean primaryDown = mc.options.keyAttack.isDown();
            boolean altDown = mc.options.keyUse.isDown();
//          boolean offhandAltDown = KeyBinding.OFFHAND_ALT_FIRE_KEY.isDown();
            boolean offhandAltDown = mc.options.keyUse.isDown();

            Item item = mainStack.getItem();
            if (item instanceof AbstractWeaponItem) {
               handlePrimary(mainStack, (AbstractWeaponItem) item, primaryDown);
            } else {
               primaryHoldTicks = 0;
               lastPrimary = false;
            }

            Item mainItem = mainStack.getItem();
            if (mainItem instanceof AbstractWeaponItem) {
               handleAlt(mainStack, (AbstractWeaponItem) mainItem, altDown);
            } else {
               altHoldTicks = 0;
               lastAlt = false;
            }

            Item offItem = offStack.getItem();
            if (offItem instanceof AbstractWeaponItem offWeapon && offWeapon.allowAltFromOffhand) {
               handleOffhandAlt(offStack, offWeapon, offhandAltDown);
            } else {
               offhandAltHoldTicks = 0;
               lastOffhandAlt = false;
            }

            lastPrimary = primaryDown;
            lastAlt = altDown;
            lastOffhandAlt = offhandAltDown;
         } else {
            reset();
         }
      }
   }

   private static void handlePrimary(ItemStack stack, AbstractWeaponItem weapon, boolean down) {
      switch(weapon.getPrimaryFiringType(stack)) {
      case SEMI_AUTO:
         if (down && !lastPrimary) {
            GunNetworking.sendPrimaryFire();
         }
         break;
      case AUTO:
         if (down) {
            GunNetworking.sendPrimaryFire();
         }
         break;
      case CHARGE:
         if (down) {
            ++primaryHoldTicks;
            GunNetworking.sendPrimaryChargeTick(primaryHoldTicks);
         } else if (lastPrimary) {
            GunNetworking.sendPrimaryRelease(primaryHoldTicks);
            primaryHoldTicks = 0;
         }
      }

   }

   private static void handleAlt(ItemStack stack, AbstractWeaponItem weapon, boolean down) {
      switch (weapon.getAltFiringType(stack)) {
         case SEMI_AUTO:
            if (down && !lastAlt) {
               GunNetworking.sendAltFire(InteractionHand.MAIN_HAND);
            }
            break;
         case AUTO:
            if (down) {
               GunNetworking.sendAltFire(InteractionHand.MAIN_HAND);
            }
            break;
         case CHARGE:
            if (down) {
               ++altHoldTicks;
               GunNetworking.sendAltChargeTick(altHoldTicks, InteractionHand.MAIN_HAND);
            } else if (lastAlt) {
               GunNetworking.sendAltRelease(altHoldTicks, InteractionHand.MAIN_HAND);
               altHoldTicks = 0;
            }
            break;
      }
   }

   private static void handleOffhandAlt(ItemStack stack, AbstractWeaponItem weapon, boolean down) {
      switch (weapon.getAltFiringType(stack)) {
         case SEMI_AUTO:
            if (down && !lastOffhandAlt) {
               GunNetworking.sendAltFire(InteractionHand.OFF_HAND);
            }
            break;
         case AUTO:
            if (down) {
               GunNetworking.sendAltFire(InteractionHand.OFF_HAND);
            }
            break;
         case CHARGE:
            if (down) {
               ++offhandAltHoldTicks;
               GunNetworking.sendAltChargeTick(offhandAltHoldTicks, InteractionHand.OFF_HAND);
            } else if (lastOffhandAlt) {
               GunNetworking.sendAltRelease(offhandAltHoldTicks, InteractionHand.OFF_HAND);
               offhandAltHoldTicks = 0;
            }
            break;
      }
   }

   private static void reset() {
      primaryHoldTicks = 0;
      altHoldTicks = 0;
      offhandAltHoldTicks = 0;
      lastPrimary = false;
      lastAlt = false;
      lastOffhandAlt = false;
   }
}
