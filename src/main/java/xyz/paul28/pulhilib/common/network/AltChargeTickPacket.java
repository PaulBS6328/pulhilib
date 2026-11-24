package xyz.paul28.pulhilib.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent.Context;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;

import java.util.function.Supplier;

public class AltChargeTickPacket {
      private final int chargeTicks;
      private final InteractionHand hand;

      public AltChargeTickPacket(int chargeTicks, InteractionHand hand) {
         this.chargeTicks = chargeTicks;
         this.hand = hand;
      }

      public static void encode(AltChargeTickPacket msg, FriendlyByteBuf buf) {
         buf.writeInt(msg.chargeTicks);
         buf.writeEnum(msg.hand);
      }

      public static AltChargeTickPacket decode(FriendlyByteBuf buf) {
         return new AltChargeTickPacket(buf.readInt(), buf.readEnum(InteractionHand.class));
      }

      public static void handle(AltChargeTickPacket msg, Supplier<Context> ctx) {
         ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
               AbstractWeaponItem.handleAltChargeTick(player, msg.chargeTicks, msg.hand);
            }
         });
         ctx.get().setPacketHandled(true);
      }
   }