package xyz.paul28.pulhilib.common.util;

public final class ColorUtil {

    private ColorUtil() {}

    /**
     * Parses a hex color string into a packed 0xRRGGBB int.
     * Accepts "#rrggbb", "rrggbb", "#rgb", or "rgb" (shorthand expands each
     * digit, e.g. "#d4f" -> "#dd44ff"). The alpha byte is always 0 here -
     * ChargeBarRenderer OR's in 0xFF000000 itself, so don't pack alpha in.
     */
    public static int fromHex(String hex) {
        String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;

        if (cleaned.length() == 3) {
            StringBuilder expanded = new StringBuilder(6);
            for (char c : cleaned.toCharArray()) {
                expanded.append(c).append(c);
            }
            cleaned = expanded.toString();
        }

        if (cleaned.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }

        return Integer.parseInt(cleaned, 16);
    }

    public static float[] hexToRGB(String hex) {
        hex = hex.replace("#", "");
        if (hex.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new float[]{r / 255.0F, g / 255.0F, b / 255.0F};
    }
}
