package xyz.paul28.pulhilib.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.paul28.pulhilib.Pulhilib;
import xyz.paul28.pulhilib.common.network.*;

import java.util.Optional;

public class PulhilibPackets {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(Pulhilib.MODID, "main"), () -> {
        return "1";
    }, "1"::equals, "1"::equals);
    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.registerMessage(
                nextId(),
                PrimaryFirePacket.class,
                PrimaryFirePacket::encode,
                PrimaryFirePacket::decode,
                PrimaryFirePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextId(),
                PrimaryReleasePacket.class,
                PrimaryReleasePacket::encode,
                PrimaryReleasePacket::decode,
                PrimaryReleasePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(
                nextId(),
                AltFirePacket.class,
                AltFirePacket::encode,
                AltFirePacket::decode,
                AltFirePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
