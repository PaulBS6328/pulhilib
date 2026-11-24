package xyz.paul28.pulhilib.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import xyz.paul28.pulhilib.common.particle.HitscanBeamOptions;

/**
 * Renders an instantaneous beam from the particle's spawn position to a target
 * position, fading out over its lifetime. Geometry is the same "tube of quads"
 * approach BeaconRenderer uses for the vanilla beacon beam, just rotated to an
 * arbitrary direction instead of being locked to world-up.
 */
public class HitscanBeamParticle extends Particle {
    private final Vec3 start;
    private final Vec3 end;
    private final float radius;
    private final float r, g, b;
    private final int lifetimeTicks;

    protected HitscanBeamParticle(ClientLevel level, double startX, double startY, double startZ,
                                  HitscanBeamOptions opts) {
        super(level, startX, startY, startZ);
        this.start = new Vec3(startX, startY, startZ);
        this.end = new Vec3(opts.targetX, opts.targetY, opts.targetZ);
        this.radius = opts.radius;
        this.lifetimeTicks = opts.lifetimeTicks;
        this.lifetime = opts.lifetimeTicks;
        this.gravity = 0;
        this.hasPhysics = false;

        int argb = opts.color;
        this.r = ((argb >> 16) & 0xFF) / 255.0F;
        this.g = ((argb >> 8) & 0xFF) / 255.0F;
        this.b = (argb & 0xFF) / 255.0F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer ignored, Camera camera, float partialTicks) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());

        Vec3 camPos = camera.getPosition();
        PoseStack pose = new PoseStack();
        pose.pushPose();
        pose.translate(start.x - camPos.x, start.y - camPos.y, start.z - camPos.z);

        Vec3 dir = end.subtract(start);
        double length = dir.length();
        if (length < 1.0E-4) {
            pose.popPose();
            return;
        }
        Vec3 norm = dir.scale(1.0 / length);

        // Rotate local +Y onto the beam direction (same trick as aligning any
        // "up-facing" tube geometry to an arbitrary vector).
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 axis = up.cross(norm);
        double axisLen = axis.length();
        double dot = Mth.clamp(up.dot(norm), -1.0, 1.0);
        float angle = (float) Math.acos(dot);
        if (axisLen > 1.0E-6) {
            Vec3 axisNorm = axis.scale(1.0 / axisLen);
            pose.mulPose(new Quaternionf().setAngleAxis(angle, (float) axisNorm.x, (float) axisNorm.y, (float) axisNorm.z));
        } else if (dot < 0) {
            // Direction is exactly opposite world-up; rotate 180 around any perpendicular axis.
            pose.mulPose(Axis.XP.rotationDegrees(180));
        }

        float alpha = 1.0F - (age + partialTicks) / (float) lifetimeTicks;
        alpha = Mth.clamp(alpha, 0.0F, 1.0F);

        Matrix4f mat = pose.last().pose();
        drawBeamTube(mat, buffer, (float) length, radius, r, g, b, alpha);

        pose.popPose();
        bufferSource.endBatch();
    }

    private static void drawBeamTube(Matrix4f mat, VertexConsumer buf, float height, float radius,
                                     float r, float g, float b, float alpha) {
        // +X face
        drawQuad(buf, mat,
                radius, 0, radius, radius, 0, -radius,
                radius, height, -radius, radius, height, radius,
                r, g, b, alpha);
        // -X face
        drawQuad(buf, mat,
                -radius, 0, -radius, -radius, 0, radius,
                -radius, height, radius, -radius, height, -radius,
                r, g, b, alpha);
        // +Z face
        drawQuad(buf, mat,
                -radius, 0, radius, radius, 0, radius,
                radius, height, radius, -radius, height, radius,
                r, g, b, alpha);
        // -Z face
        drawQuad(buf, mat,
                radius, 0, -radius, -radius, 0, -radius,
                -radius, height, -radius, radius, height, -radius,
                r, g, b, alpha);
    }

    private static void drawQuad(VertexConsumer buf, Matrix4f mat,
                                 float x1, float y1, float z1,
                                 float x2, float y2, float z2,
                                 float x3, float y3, float z3,
                                 float x4, float y4, float z4,
                                 float r, float g, float b, float alpha) {
        buf.vertex(mat, x1, y1, z1).color(r, g, b, alpha).endVertex();
        buf.vertex(mat, x2, y2, z2).color(r, g, b, alpha).endVertex();
        buf.vertex(mat, x3, y3, z3).color(r, g, b, alpha).endVertex();
        buf.vertex(mat, x4, y4, z4).color(r, g, b, alpha).endVertex();
    }

    public static class Provider implements ParticleProvider<HitscanBeamOptions> {
        @Override
        public Particle createParticle(HitscanBeamOptions opts, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            HitscanBeamParticle particle = new HitscanBeamParticle(level, x, y, z, opts);
            particle.setParticleSpeed(0, 0, 0);
            return particle;
        }
    }
}