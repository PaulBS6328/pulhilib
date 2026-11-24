package xyz.paul28.pulhilib.common.util;
import net.minecraft.util.Mth;

public final class EasingUtils {

    private EasingUtils() {}

    private static float clamp01(float t) {
        return Mth.clamp(t, 0f, 1f);
    }

    public static float inOutSine(float t) {
        return t < 0.5f ? 8f * t * t * t * t : 1f - 8f * (--t) * t * t * t;
    }

    public static float inOutQuad(float t) {
        t = clamp01(t);
        if (t < 0.5f) {
            return 2f * t * t;
        } else {
            float f = (2f * t) - 1f;
            return 1f - 0.5f * (1f - f * f);
        }
    }

    public static float inOutCubic(float t) {
        t = clamp01(t);
        if (t < 0.5f) {
            return 4f * t * t * t;
        } else {
            float f = (2f * t) - 2f;
            return 0.5f * f * f * f + 1f;
        }
    }

    public static float outElastic(float t) {
        t = clamp01(t);
        if (t == 0f) return 0f;
        if (t == 1f) return 1f;
        float p = 0.3f;
        return (float) (Math.pow(2, -10 * t) * Math.sin((t - p / 4f) * (2 * Math.PI) / p) + 1f);
    }

    public static float outBack(float t) {
        t = clamp01(t);
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1f, 3) + c1 * (float) Math.pow(t - 1f, 2);
    }

    public static float outQuad(float t) {
        t = clamp01(t);
        return 1f - (1f - t) * (1f - t);
    }

    public static float inCirc(float t) {
        t = clamp01(t);
        return 1f - (float) Math.sqrt(1f - t * t);
    }

    public static float inSine(float t) {
        t = clamp01(t);
        return (float) (1.0 - Math.cos(t * Math.PI / 2.0));
    }

    public static float outCirc(float t) {
        t = clamp01(t);
        return (float) Math.sqrt(1.0 - (t - 1.0) * (t - 1.0));
    }

    public static float linear(float t) {
        return clamp01(t);
    }
}