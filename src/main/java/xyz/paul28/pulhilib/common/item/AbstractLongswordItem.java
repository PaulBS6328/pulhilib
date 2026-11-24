package xyz.paul28.pulhilib.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;

import java.util.UUID;

public abstract class AbstractLongswordItem extends AbstractWeaponItem {

    private final Tier toolMaterial;
    private final float attackDamage;
    private final float attackSpeed;
    private final float attackReach;

    public float attackDamageAddition = 0;
    public float attackSpeedAddition = 0;
    public float attackReachAddition = 0;

    private Multimap<Attribute, AttributeModifier> attributeModifiers;

    public AbstractLongswordItem(Tier toolMaterial, float attackDamage, float attackSpeed, float attackReach, Properties settings) {
        super(settings);
        this.toolMaterial = toolMaterial;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.attackReach = attackReach;

        setAttributeModifiersForMode();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public AbstractWeaponItem.@NotNull FiringType getPrimaryFiringType(ItemStack stack) {
        return FiringType.SEMI_AUTO;
    }

    @Override
    public AbstractWeaponItem.@NotNull FiringType getAltFiringType(ItemStack stack) {
        return FiringType.NONE;
    }

    @Override
    public int getPrimaryCooldown(ItemStack stack) {
        return 0;
    }

    @Override
    public int getAltCooldown(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean shouldCancelSwing(ItemStack stack, Player player) {
        return false;
    }

    @Override
    public void onPrimaryFire(Level level, Player player, ItemStack stack) {
        if (!isBlocking(player) || !canUseBlockHitAbility(player, stack)) return;
        handleLongswordAbility(player, stack);
    }

    private void setAttributeModifiersForMode() {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        builder.put(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        this.attackDamage + this.toolMaterial.getAttackDamageBonus() + attackDamageAddition,
                        AttributeModifier.Operation.ADDITION)
        );
        builder.put(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                        this.attackSpeed + attackSpeedAddition,
                        AttributeModifier.Operation.ADDITION)
        );
        builder.put(
                ForgeMod.ENTITY_REACH.get(),
                new AttributeModifier(UUID.fromString("911af262-067d-4da2-854c-20f03cc2dd8b"),
                        "Weapon modifier", this.attackReach + attackReachAddition, AttributeModifier.Operation.ADDITION)
        );

        this.attributeModifiers = builder.build();
    }

    public float getBlockMultiplier() {
        return 0.75F;
    }

    abstract SoundEvent getLongswordParrySound();

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
        itemstack.hurtAndBreak(1, entity, (i) -> i.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (pState.is(Blocks.COBWEB)) {
            return 15.0F;
        } else {
            return pState.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack itemstack) {
        return 99999;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND && !canBeOneHanded()) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (canBlock(player)) {
            InteractionResultHolder<ItemStack> result = super.use(world, player, hand);
            player.startUsingItem(hand);
            return result;
        }
        return super.use(world, player, hand);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return this.toolMaterial.getEnchantmentValue();
    }

    public void handleOnBlock(Player player, ItemStack stack) {

    }

    public abstract boolean canUseBlockHitAbility(Player player, ItemStack stack);

    public abstract void handleLongswordAbility(Player player, ItemStack stack);

    public abstract void onBlock(Player player, Level level, float attackStrength, int heldTicks, LivingHurtEvent event);

    public boolean canBeOneHanded() {
        return false;
    }

    public boolean isOneHanded(Player player) {
        return canBeOneHanded();
    }

    public boolean canBlock(Player player) {
        return (!isOneHanded(player));
    }

    public boolean isBlocking(Player player) {
        return (player.getUseItem().getItem() instanceof AbstractLongswordItem);
    }

    public boolean acceptsEnchantment(Enchantment enchantment) {
        return enchantment == Enchantments.SHARPNESS
                || enchantment == Enchantments.BANE_OF_ARTHROPODS
                || enchantment == Enchantments.SMITE
                || enchantment == Enchantments.UNBREAKING
                || enchantment == Enchantments.MENDING
                || enchantment == Enchantments.VANISHING_CURSE
                || enchantment == Enchantments.FIRE_ASPECT;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return acceptsEnchantment(enchantment);
    }

    public static class LongswordTier implements Tier {
        public int getUses() { return 0; }
        public float getSpeed() { return 0.0F; }
        public float getAttackDamageBonus() { return 0.5F; }
        public int getLevel() { return 4; }
        public int getEnchantmentValue() { return 25; }
        public @NotNull Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    }
}