package xyz.paul28.pulhilib.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import xyz.paul28.pulhilib.common.item.AbstractLongswordItem;

@EventBusSubscriber
public class LongswordHandler {

   private static final String PARRIED_BY_TAG = "PaulsExtrasParriedBy";
   private static final String PARRIED_TIME_TAG = "PaulsExtrasParriedTime";

   /*@SubscribeEvent
   public static void onProjectileImpact(ProjectileImpactEvent event) {
      Projectile projectile = event.getProjectile();
      if (projectile.level().isClientSide) return;
      if (!(event.getRayTraceResult() instanceof EntityHitResult hit)) return;
      if (!(hit.getEntity() instanceof Player player)) return;

      // a projectile we just parried is still overlapping us, so let it pass through
      CompoundTag data = projectile.getPersistentData();
      if (data.hasUUID(PARRIED_BY_TAG)
              && data.getUUID(PARRIED_BY_TAG).equals(player.getUUID())
              && projectile.level().getGameTime() - data.getLong(PARRIED_TIME_TAG) <= 5) {
         event.setCanceled(true);
         return;
      }

      if (!player.isUsingItem() || !(player.getUseItem().getItem() instanceof ScarletItem)) return;

      int heldTicks = player.getUseItem().getUseDuration() - player.getUseItemRemainingTicks();
      if (heldTicks >= 10) return;
      if (player.getAttackStrengthScale(0.5F) < 0.5F) return;

      // direction the projectile is travelling, not its (meaningless) offset from the player
      Vec3 velocity = projectile.getDeltaMovement();
      double speed = velocity.length();
      if (speed < 1.0E-4D) return;

      Vec3 look = player.getViewVector(1.0F).normalize();
      Vec3 incoming = velocity.scale(-1.0D / speed); // points from the player back toward the shooter
      if (look.dot(incoming) <= 0.0D) return;

      // ---- parry succeeded ----
      event.setCanceled(true); // no hit, no damage

      Vec3 redirected = look.scale(speed * 1.5D);
      projectile.setOwner(player);
      projectile.setDeltaMovement(redirected);
      projectile.hasImpulse = true;
      projectile.hurtMarked = true; // forces a motion packet to clients

      data.putUUID(PARRIED_BY_TAG, player.getUUID());
      data.putLong(PARRIED_TIME_TAG, projectile.level().getGameTime());

      // fireballs accelerate along their power vector, so redirect that too
      if (projectile instanceof AbstractHurtingProjectile hurting) {
         double power = Math.sqrt(hurting.xPower * hurting.xPower
                 + hurting.yPower * hurting.yPower
                 + hurting.zPower * hurting.zPower);
         hurting.xPower = look.x * power;
         hurting.yPower = look.y * power;
         hurting.zPower = look.z * power;
      }

      double horizontal = Math.sqrt(redirected.x * redirected.x + redirected.z * redirected.z);
      projectile.setYRot((float) (Mth.atan2(redirected.x, redirected.z) * (180D / Math.PI)));
      projectile.setXRot((float) (Mth.atan2(redirected.y, horizontal) * (180D / Math.PI)));
      projectile.yRotO = projectile.getYRot();
      projectile.xRotO = projectile.getXRot();

      Level level = player.level();
      float pitch = 0.8F + level.random.nextFloat() * 0.2F;
      level.playSound(null, player.getX(), player.getY(), player.getZ(),
              PaulsExtrasModSounds.LONGSWORD_BLOCK.get(), SoundSource.PLAYERS, 2.0F, pitch);
      level.playSound(null, player.getX(), player.getY(), player.getZ(),
              SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
   }*/

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (!(event.getEntity() instanceof Player player) || !player.isUsingItem()) return;

      ItemStack stack = player.getUseItem();
      if (!(stack.getItem() instanceof AbstractLongswordItem longsword)) return;

      Vec3 lookDirection = player.getViewVector(1.0F).normalize();
      Vec3 damageDirection;
      if (event.getSource().is(DamageTypes.FALL)) {
         damageDirection = new Vec3(0.0D, -1.0D, 0.0D);
      } else {
         Entity attacker = event.getSource().getEntity();
         if (attacker == null) return;
         damageDirection = attacker.position().subtract(player.position()).normalize();
      }

      if (lookDirection.dot(damageDirection) <= 0.0D) return;

      int heldTicks = stack.getUseDuration() - player.getUseItemRemainingTicks();
      longsword.onBlock(player, player.level(), player.getAttackStrengthScale(0.5F), heldTicks, event);
   }
}