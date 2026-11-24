package xyz.paul28.pulhilib.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.paul28.pulhilib.Pulhilib;
import xyz.paul28.pulhilib.common.particle.HitscanBeamOptions;

public class PulhilibParticleTypes {
   public static final DeferredRegister<ParticleType<?>> REGISTRY =
           DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Pulhilib.MODID);

    public static final RegistryObject<ParticleType<HitscanBeamOptions>> HITSCAN =
            REGISTRY.register("hitscan", () ->
                    new ParticleType<>(false, HitscanBeamOptions.DESERIALIZER) {
                        @Override
                        public Codec<HitscanBeamOptions> codec() {
                            return HitscanBeamOptions.CODEC;
                        }
                    });

    public static final RegistryObject<SimpleParticleType> SHOCKWAVE =
            REGISTRY.register("shockwave", () -> new SimpleParticleType(true));
}
