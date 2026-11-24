package xyz.paul28.pulhilib.common.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IChargeBarItem {

    int getChargeBarSegments(ItemStack stack);

    float getChargeBarFill(Player player, ItemStack stack);

    default int getSegmentWidth(ItemStack stack) {
        int segments = getChargeBarSegments(stack);
        if (segments <= 1) return 13;
        if (segments == 3) return 4;
        return Math.max(1, Math.round((float) (13 - (segments - 1)) / segments));
    }

    default int getChargeBarColor(ItemStack stack) { return 0x55FF55; }
    default int getChargeBarPartialColor(ItemStack stack) { return 0x555555; }
    default int getChargeBarOverchargeColor(ItemStack stack) { return 0xf0cc62; }
}
