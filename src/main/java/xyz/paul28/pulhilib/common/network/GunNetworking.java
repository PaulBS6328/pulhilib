package xyz.paul28.pulhilib.common.network;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.paul28.pulhilib.Pulhilib;

public final class GunNetworking {
   private static final String PROTOCOL = "1";
   private static SimpleChannel CHANNEL;
   private static int id = 0;

   public static void init() {
      CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(Pulhilib.MODID, "guns"), () -> {
         return "1";
      }, "1"::equals, "1"::equals);
      CHANNEL.registerMessage(id++, PrimaryFirePacket.class, PrimaryFirePacket::encode, PrimaryFirePacket::decode, PrimaryFirePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(id++, AltFirePacket.class, AltFirePacket::encode, AltFirePacket::decode, AltFirePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(id++, PrimaryChargeTickPacket.class, PrimaryChargeTickPacket::encode, PrimaryChargeTickPacket::decode, PrimaryChargeTickPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(id++, AltChargeTickPacket.class, AltChargeTickPacket::encode, AltChargeTickPacket::decode, AltChargeTickPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(id++, PrimaryReleasePacket.class, PrimaryReleasePacket::encode, PrimaryReleasePacket::decode, PrimaryReleasePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
      CHANNEL.registerMessage(id++, AltReleasePacket.class, AltReleasePacket::encode, AltReleasePacket::decode, AltReleasePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
   }

   public static void sendPrimaryFire() {
      CHANNEL.sendToServer(new PrimaryFirePacket());
   }

   public static void sendAltFire(InteractionHand mainHand) {
      CHANNEL.sendToServer(new AltFirePacket(mainHand));
   }

   public static void sendPrimaryChargeTick(int ticks) {
      CHANNEL.sendToServer(new PrimaryChargeTickPacket(ticks));
   }

   public static void sendAltChargeTick(int ticks, InteractionHand hand) {
      CHANNEL.sendToServer(new AltChargeTickPacket(ticks, hand));
   }

   public static void sendPrimaryRelease(int ticks) {
      CHANNEL.sendToServer(new PrimaryReleasePacket(ticks));
   }

   public static void sendAltRelease(int ticks, InteractionHand hand) {
      CHANNEL.sendToServer(new AltReleasePacket(ticks, hand));
   }
}
