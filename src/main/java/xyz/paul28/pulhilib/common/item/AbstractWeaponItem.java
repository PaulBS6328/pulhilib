package xyz.paul28.pulhilib.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractWeaponItem extends Item {
   public boolean allowAltFromOffhand = false;
   public boolean primaryFromOffhandWhenSevered = false;

   protected AbstractWeaponItem(Properties properties) {
      super(properties);
   }

   public static ItemStack getAltHandStack(Player player) {
      ItemStack main = player.getMainHandItem();
      if (main.getItem() instanceof AbstractWeaponItem) {
         return main;
      } else {
         ItemStack off = player.getOffhandItem();
         Item item = off.getItem();
         if (item instanceof AbstractWeaponItem) {
            AbstractWeaponItem weapon = (AbstractWeaponItem)item;
            if (weapon.allowAltFromOffhand) {
               return off;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   public static boolean redirectsAltToPrimary(Player player, InteractionHand hand, AbstractWeaponItem weapon) {
      return hand == InteractionHand.OFF_HAND
              && weapon.primaryFromOffhandWhenSevered;
   }

   public static FiringType getEffectiveAltFiringType(Player player, InteractionHand hand, ItemStack stack) {
      if (stack.getItem() instanceof AbstractWeaponItem weapon) {
         return redirectsAltToPrimary(player, hand, weapon)
                 ? weapon.getPrimaryFiringType(stack)
                 : weapon.getAltFiringType(stack);
      }
      return FiringType.NONE;
   }

   public boolean shouldCancelSwing(ItemStack stack, Player player) {
      return true;
   }

   public abstract @NotNull AbstractWeaponItem.FiringType getPrimaryFiringType(ItemStack stack);

   public abstract @NotNull AbstractWeaponItem.FiringType getAltFiringType(ItemStack stack);

   public abstract int getPrimaryCooldown(ItemStack stack);

   public abstract int getAltCooldown(ItemStack stack);

   public void onPrimaryFire(Level level, Player player, ItemStack stack) {
   }

   public void onAltFire(Level level, Player player, ItemStack stack) {
   }

   public void onPrimaryChargeTick(ServerPlayer player, int chargeTicks) {
   }

   public void onPrimaryChargeRelease(Level level, ServerPlayer player, ItemStack stack, int chargeTicks) {
   }

   public void onAltChargeTick(ServerPlayer player, int chargeTicks, InteractionHand hand) {
   }

   public void onAltChargeRelease(Level level, ServerPlayer player, ItemStack stack, int chargeTicks) {
   }

   public static void handlePrimaryFire(ServerPlayer player) {
      ItemStack stack = player.getMainHandItem();
      Item item = stack.getItem();
      if (item instanceof AbstractWeaponItem weapon &&
              (!player.isUsingItem())) {
         if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
            FiringType type = weapon.getPrimaryFiringType(stack);
            if (type != FiringType.CHARGE && type != FiringType.NONE) {
               weapon.onPrimaryFire(player.level(), player, stack);
               player.getCooldowns().addCooldown(
                       stack.getItem(),
                       weapon.getPrimaryCooldown(stack)
               );
            }
         }
      }
   }

   public static void handlePrimaryChargeTick(ServerPlayer player, int chargeTicks) {
      ItemStack stack = player.getMainHandItem();
      Item item = stack.getItem();
      if (item instanceof AbstractWeaponItem && !player.isUsingItem()) {
         AbstractWeaponItem weapon = (AbstractWeaponItem)item;
         weapon.onPrimaryChargeTick(player, chargeTicks);
      }
   }

   public static void handlePrimaryChargeRelease(ServerPlayer player, int chargeTicks) {
      ItemStack stack = player.getMainHandItem();
      Item item = stack.getItem();
      if (item instanceof AbstractWeaponItem && !player.isUsingItem()) {
         AbstractWeaponItem weapon = (AbstractWeaponItem)item;
         if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
            weapon.onPrimaryChargeRelease(player.level(), player, stack, chargeTicks);
            player.getCooldowns().addCooldown(stack.getItem(), weapon.getPrimaryCooldown(stack));
         }
      }
   }

   public static void handleAltFire(ServerPlayer player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      Item item = stack.getItem();

      if (item instanceof AbstractWeaponItem weapon && !player.isUsingItem()) {
         boolean asPrimary = redirectsAltToPrimary(player, hand, weapon);

         if (hand == InteractionHand.OFF_HAND && !weapon.allowAltFromOffhand && !asPrimary) {
            return;
         }

         if (!player.getCooldowns().isOnCooldown(item)) {
            if (asPrimary) {
               // severed offhand: this input is the weapon's main fire
               FiringType type = weapon.getPrimaryFiringType(stack);
               if (type != FiringType.CHARGE && type != FiringType.NONE) {
                  weapon.onPrimaryFire(player.level(), player, stack);
                  player.getCooldowns().addCooldown(item, weapon.getPrimaryCooldown(stack));
               }
               return;
            }

            FiringType type = weapon.getAltFiringType(stack);

            if (type != FiringType.CHARGE && type != FiringType.NONE) {
               weapon.onAltFire(player.level(), player, stack);
               player.getCooldowns().addCooldown(
                       item,
                       weapon.getAltCooldown(stack)
               );
            }
         }
      }
   }

   public static void handleAltChargeTick(ServerPlayer player, int chargeTicks, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      Item item = stack.getItem();

      if (item instanceof AbstractWeaponItem weapon && !player.isUsingItem()) {
         boolean asPrimary = redirectsAltToPrimary(player, hand, weapon);

         if (hand == InteractionHand.OFF_HAND && !weapon.allowAltFromOffhand && !asPrimary) {
            return;
         }

         if (asPrimary) {
            weapon.onPrimaryChargeTick(player, chargeTicks);
            return;
         }

         weapon.onAltChargeTick(player, chargeTicks, hand);
      }
   }

   public static void handleAltChargeRelease(ServerPlayer player, int chargeTicks, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      Item item = stack.getItem();

      if (item instanceof AbstractWeaponItem weapon && !player.isUsingItem()) {
         boolean asPrimary = redirectsAltToPrimary(player, hand, weapon);

         if (hand == InteractionHand.OFF_HAND && !weapon.allowAltFromOffhand && !asPrimary) {
            return;
         }

         if (asPrimary) {
            if (!player.getCooldowns().isOnCooldown(item)) {
               weapon.onPrimaryChargeRelease(player.level(), player, stack, chargeTicks);
               player.getCooldowns().addCooldown(item, weapon.getPrimaryCooldown(stack));
            }
            return;
         }

         weapon.onAltChargeRelease(player.level(), player, stack, chargeTicks);
      }
   }

   public boolean hasSway() {
      return false;
   }

   public static enum FiringType {
      NONE,
      SEMI_AUTO,
      AUTO,
      CHARGE;
   }
}