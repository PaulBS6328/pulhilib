package xyz.paul28.pulhilib.common.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import xyz.paul28.pulhilib.init.PulhilibParticleTypes;

public class HitscanBeamOptions implements ParticleOptions {
    public static final Codec<HitscanBeamOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("targetX").forGetter(o -> o.targetX),
                    Codec.DOUBLE.fieldOf("targetY").forGetter(o -> o.targetY),
                    Codec.DOUBLE.fieldOf("targetZ").forGetter(o -> o.targetZ),
                    Codec.FLOAT.fieldOf("radius").forGetter(o -> o.radius),
                    Codec.INT.fieldOf("color").forGetter(o -> o.color),
                    Codec.INT.fieldOf("lifetimeTicks").forGetter(o -> o.lifetimeTicks)
            ).apply(instance, HitscanBeamOptions::new)
    );

    public static final Deserializer<HitscanBeamOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public HitscanBeamOptions fromCommand(ParticleType<HitscanBeamOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            reader.expect(' ');
            float radius = reader.readFloat();
            reader.expect(' ');
            int color = reader.readInt();
            reader.expect(' ');
            int lifetime = reader.readInt();
            return new HitscanBeamOptions(x, y, z, radius, color, lifetime);
        }

        @Override
        public HitscanBeamOptions fromNetwork(ParticleType<HitscanBeamOptions> type, FriendlyByteBuf buf) {
            return new HitscanBeamOptions(buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readFloat(), buf.readInt(), buf.readInt());
        }
    };

    public final double targetX, targetY, targetZ;
    public final float radius;
    public final int color;
    public final int lifetimeTicks;

    public HitscanBeamOptions(double targetX, double targetY, double targetZ, float radius, int color, int lifetimeTicks) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.radius = radius;
        this.color = color;
        this.lifetimeTicks = lifetimeTicks;
    }

    @Override
    public ParticleType<?> getType() {
        return PulhilibParticleTypes.HITSCAN.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeDouble(targetX);
        buf.writeDouble(targetY);
        buf.writeDouble(targetZ);
        buf.writeFloat(radius);
        buf.writeInt(color);
        buf.writeInt(lifetimeTicks);
    }

    @Override
    public String writeToString() {
        return String.format("%f %f %f %f %d %d", targetX, targetY, targetZ, radius, color, lifetimeTicks);
    }
}