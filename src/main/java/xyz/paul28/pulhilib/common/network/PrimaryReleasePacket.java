package xyz.paul28.pulhilib.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;

import java.util.function.Supplier;

public class PrimaryReleasePacket {
      private final int chargeTicks;

      public PrimaryReleasePacket(int chargeTicks) {
         this.chargeTicks = chargeTicks;
      }

      public static void encode(PrimaryReleasePacket msg, FriendlyByteBuf buf) {
         buf.writeInt(msg.chargeTicks);
      }

      public static PrimaryReleasePacket decode(FriendlyByteBuf buf) {
         return new PrimaryReleasePacket(buf.readInt());
      }

      public static void handle(PrimaryReleasePacket msg, Supplier<Context> ctx) {
         ((Context)ctx.get()).enqueueWork(() -> {
            ServerPlayer player = ((Context)ctx.get()).getSender();
            if (player != null) {
               AbstractWeaponItem.handlePrimaryChargeRelease(player, msg.chargeTicks);
            }

         });
         ((Context)ctx.get()).setPacketHandled(true);
      }
   }