package xyz.paul28.pulhilib.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent.Context;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;

import java.util.function.Supplier;

public class AltReleasePacket {
      private final int chargeTicks;
      private final InteractionHand hand;

      public AltReleasePacket(int chargeTicks, InteractionHand hand) {
         this.chargeTicks = chargeTicks;
         this.hand = hand;
      }

      public static void encode(AltReleasePacket msg, FriendlyByteBuf buf) {
         buf.writeInt(msg.chargeTicks);
         buf.writeEnum(msg.hand);
      }

      public static AltReleasePacket decode(FriendlyByteBuf buf) {
         return new AltReleasePacket(buf.readInt(), buf.readEnum(InteractionHand.class));
      }

      public static void handle(AltReleasePacket msg, Supplier<Context> ctx) {
         ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
               AbstractWeaponItem.handleAltChargeRelease(player, msg.chargeTicks, msg.hand);
            }
         });
         ctx.get().setPacketHandled(true);
      }
   }