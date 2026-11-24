package xyz.paul28.pulhilib.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;

import java.util.function.Supplier;

public class PrimaryChargeTickPacket {
    private final int chargeTicks;

    public PrimaryChargeTickPacket(int chargeTicks) {
        this.chargeTicks = chargeTicks;
    }

    public static void encode(PrimaryChargeTickPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.chargeTicks);
    }

    public static PrimaryChargeTickPacket decode(FriendlyByteBuf buf) {
        return new PrimaryChargeTickPacket(buf.readInt());
    }

    public static void handle(PrimaryChargeTickPacket msg, Supplier<Context> ctx) {
        ((Context)ctx.get()).enqueueWork(() -> {
            ServerPlayer player = ((Context)ctx.get()).getSender();
            if (player != null) {
                AbstractWeaponItem.handlePrimaryChargeTick(player, msg.chargeTicks);
            }

        });
        ((Context)ctx.get()).setPacketHandled(true);
    }
}