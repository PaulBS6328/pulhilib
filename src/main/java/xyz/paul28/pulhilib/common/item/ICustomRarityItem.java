package xyz.paul28.pulhilib.common.item;

import net.minecraft.world.item.ItemStack;
import xyz.paul28.pulhilib.Pulhilib;

public interface ICustomRarityItem {
    Pulhilib.Rarity getModRarity(ItemStack stack);
}
