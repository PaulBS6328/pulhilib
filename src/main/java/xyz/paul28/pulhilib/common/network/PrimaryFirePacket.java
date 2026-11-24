package xyz.paul28.pulhilib.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;

import java.util.function.Supplier;

public class PrimaryFirePacket {
      public static void encode(PrimaryFirePacket pkt, FriendlyByteBuf buf) {
      }

      public static PrimaryFirePacket decode(FriendlyByteBuf buf) {
         return new PrimaryFirePacket();
      }

      public static void handle(PrimaryFirePacket pkt, Supplier<Context> ctx) {
         ((Context)ctx.get()).enqueueWork(() -> {
            ServerPlayer player = ((Context)ctx.get()).getSender();
            if (player != null) {
               AbstractWeaponItem.handlePrimaryFire(player);
            }

         });
         ((Context)ctx.get()).setPacketHandled(true);
      }
   }





