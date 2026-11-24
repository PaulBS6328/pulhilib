package xyz.paul28.pulhilib;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import xyz.paul28.pulhilib.common.network.GunNetworking;
import xyz.paul28.pulhilib.init.PulhilibPackets;
import xyz.paul28.pulhilib.init.PulhilibParticleTypes;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Pulhilib.MODID)
public class Pulhilib {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "pulhilib";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "pulhilib" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "pulhilib" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(MODID, MODID), () -> {
        return "1";
    }, "1"::equals, "1"::equals);
    private static int messageID = 0;

    public Pulhilib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        PulhilibParticleTypes.REGISTRY.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("Pulhilib Initialized");
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        ++messageID;
    }

    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(PulhilibPackets::register);
        event.enqueueWork(GunNetworking::init);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public enum Rarity {
        GILDED(0xFFFF55),
        SCRAP(0xcC5300),
        BLOODBOUND(0xFF5555);

        private final int color;

        Rarity(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }
}
