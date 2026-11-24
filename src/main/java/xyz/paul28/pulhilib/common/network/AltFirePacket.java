package xyz.paul28.pulhilib.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent.Context;
import xyz.paul28.pulhilib.common.item.AbstractWeaponItem;

import java.util.function.Supplier;

public class AltFirePacket {
      public final InteractionHand hand;

      public AltFirePacket(InteractionHand hand) {
         this.hand = hand;
      }

      public static void encode(AltFirePacket msg, FriendlyByteBuf buf) {
         buf.writeEnum(msg.hand);
      }

      public static AltFirePacket decode(FriendlyByteBuf buf) {
         return new AltFirePacket(buf.readEnum(InteractionHand.class));
      }

      public static void handle(AltFirePacket msg, Supplier<Context> ctx) {
         ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
               AbstractWeaponItem.handleAltFire(player, msg.hand);
            }
         });
         ctx.get().setPacketHandled(true);
      }
   }