package xyz.paul28.pulhilib.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.paul28.pulhilib.common.item.ICustomHitParticleItem;
import xyz.paul28.pulhilib.common.item.ICustomHitSoundItem;

@Mixin({Player.class})
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Shadow
    public abstract void playSound(SoundEvent var1, float var2, float var3);

    @Shadow
    public abstract float getAttackStrengthScale(float var1);

    @Inject(
            method = {"attack"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;getAttackStrengthScale(F)F"
            )}
    )
    private void pauls_extras$spawnCustomHitParticlesAndPlayCustomHitSound(Entity target, CallbackInfo ci) {
        if (this.getAttackStrengthScale(0.5F) > 0.9F) {
            ItemStack stack = this.getMainHandItem();
            Item item = stack.getItem();
            if (item instanceof ICustomHitParticleItem) {
                ICustomHitParticleItem hit = (ICustomHitParticleItem)item;
                hit.spawnHitParticles((Player) (Object) this, stack);
            }

            if (item instanceof ICustomHitSoundItem) {
                ICustomHitSoundItem sound = (ICustomHitSoundItem)item;
                sound.playHitSound((Player) (Object) this);
            }
        }
    }
}