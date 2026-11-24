package xyz.paul28.pulhilib.init;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.paul28.pulhilib.client.particle.HitscanBeamParticle;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class PulhilibParticles {
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpecial(PulhilibParticleTypes.HITSCAN.get(),
                new HitscanBeamParticle.Provider());
    }
}
