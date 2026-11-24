package xyz.paul28.pulhilib.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xyz.paul28.pulhilib.common.item.IChargeBarItem;

public final class ChargeBarRenderer {

    private static final int BAR_X_OFFSET = 2;
    private static final int BAR_Y_OFFSET = 13;
    private static final int SEGMENT_SPACING = 1;
    private static final int BACKGROUND_COLOR = 0xFF000000;

    private ChargeBarRenderer() {}

    public static void render(GuiGraphics graphics, IChargeBarItem chargeBarItem,
                              Player player, ItemStack stack, int x, int y) {
        int segments = chargeBarItem.getChargeBarSegments(stack);
        if (segments <= 0) return;

        float fill = chargeBarItem.getChargeBarFill(player, stack);
        int filledColor = chargeBarItem.getChargeBarColor(stack) | 0xFF000000;
        int partialColor = chargeBarItem.getChargeBarPartialColor(stack) | 0xFF000000;
        int overchargeColor = chargeBarItem.getChargeBarOverchargeColor(stack) | 0xFF000000;

        int baseX = x + BAR_X_OFFSET;
        int baseY = y + BAR_Y_OFFSET;

        // Pull the exact segment width from the interface rule safely
        int segmentWidth = chargeBarItem.getSegmentWidth(stack);

        // CRITICAL FIX: Calculate the background box size explicitly based on segment metrics
        int dynamicTotalWidth = (segments * segmentWidth) + ((segments - 1) * SEGMENT_SPACING);
        graphics.fill(RenderType.guiOverlay(), baseX, baseY, baseX + dynamicTotalWidth, baseY + 2, BACKGROUND_COLOR);

        // Pass 1: Render Base Layer (0.0 to 1.0)
        float baseFill = clamp(fill, 0.0F, 1.0F);
        renderLayer(graphics, baseX, baseY, segments, segmentWidth, baseFill, filledColor, partialColor);

        // Pass 2: Render Overcharge Layer (1.0 to 2.0+)
        if (fill > 1.0F) {
            float overchargeFill = fill - 1.0F;
            renderLayer(graphics, baseX, baseY, segments, segmentWidth, overchargeFill, overchargeColor, overchargeColor);
        }
    }


    private static void renderLayer(GuiGraphics graphics, int baseX, int baseY, int segments,
                                    int segmentWidth, float layerFill, int fullColor, int partialColor) {
        float progressPerSegment = 1.0F / segments;

        for (int i = 0; i < segments; i++) {
            int segX = baseX + i * (segmentWidth + SEGMENT_SPACING);

            float minProgressRequired = i * progressPerSegment;
            float segmentProgress = clamp(layerFill - minProgressRequired, 0.0F, progressPerSegment);

            float normalizedProgress = segmentProgress * segments;

            // floor instead of round: a segment can only show as visually "full"
            // once normalizedProgress has actually reached 1.0, not merely rounded up to it.
            int fillWidth = (int) Math.floor(segmentWidth * normalizedProgress);

            if (fillWidth <= 0) continue;

            int finalColor = (fillWidth >= segmentWidth) ? fullColor : partialColor;
            graphics.fill(RenderType.guiOverlay(), segX, baseY, segX + fillWidth, baseY + 1, finalColor);
        }
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
