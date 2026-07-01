package io.github.isoyigido.basic.gui.app;

import java.awt.*;

/// Stores a set of colors derived from a base color.
/// @param base the base color
/// @param brighter brighter variant
/// @param darker darker variant
/// @param hovered variant displayed when hovered over
/// @param grayscale grayscale variant
/// @see Color
public record ColorSet(Color base, Color brighter, Color darker, Color hovered, Color grayscale) {
    /// Constructs a color set derived from the given base color.
    /// @param base the base color
    /// @param light whether the color is light or dark (used for determining whether to brighten or darken the color when hovered over)
    /// @return a new `ColorSet` record
    public static ColorSet of(Color base, boolean light) {
        // - Get the variants -
        Color brighter = brighten(base);
        Color darker = darken(base);
        Color hovered = light ? darker : brighter;
        Color grayscale = grayscale(base);

        // Return a new color set
        return new ColorSet(base, brighter, darker, hovered, grayscale);
    }

    /// The arbitrary change of the `lightness` value in HSL color space (used for brightening and darkening colors)
    private static final float LIGHTNESS_CHANGE = 0.05f;

    /// Helper method: Offsets the `lightness` value of the given base color by the given offset value in HSL color space.
    /// @param base the base color
    /// @param offset the offset value
    /// @return the color with the changed `lightness` value
    private static Color offsetLightness(Color base, float offset) {
        // If there is no offset, return the base color
        if (offset == 0f) return base;

        // Get the HSL (Hue, Saturation, Lightness) values of the color
        float[] hsl = RGBtoHSL(base);

        // Offset the Lightness value safely between 0.0 and 1.0
        hsl[2] = Math.clamp(hsl[2] + offset, 0f, 1f);

        // Return a new color with the new HSL values, preserving the alpha channel
        return HSLtoRGB(hsl[0], hsl[1], hsl[2], base.getAlpha());
    }

    /// Helper method: Converts the given RGB color to HSL (Hue, Saturation, Lightness).
    /// @param color the RGB color
    /// @return a `float` array containing HSL values: `[0]=Hue`, `[1]=Saturation`, `[2]=Lightness`.
    private static float[] RGBtoHSL(Color color) {
        // Normalize RGB values to the range [0, 1]
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        // Get the maximum and minimum values
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));

        // Initialize HSL values
        float h = 0f, s = 0f, l = (max + min) / 2f;

        // If the color is not achromatic (grayscale)
        if (max != min) {
            // Calculate Saturation based on Lightness
            float d = max - min;
            s = l > 0.5f ? d / (2f - max - min) : d / (max + min);

            // Calculate Hue based on which RGB component is dominant
            if (max == r) {
                h = (g - b) / d + (g < b ? 6f : 0f);
            } else if (max == g) {
                h = (b - r) / d + 2f;
            } else if (max == b) {
                h = (r - g) / d + 4f;
            }
            h /= 6f; // Normalize hue to [0, 1]
        }

        // Return the HSL values
        return new float[] { h, s, l };
    }

    /// Helper method: Converts HSL (Hue, Saturation, Lightness) values to an RGB Color.
    /// @param h hue value within `[0.0, 1.0]`
    /// @param s saturation value within `[0.0, 1.0]`
    /// @param l lightness value within `[0.0, 1.0]`
    /// @param alpha alpha value within `[0, 255]`
    /// @return the RGB color
    private static Color HSLtoRGB(float h, float s, float l, int alpha) {
        // RGB values
        float r, g, b;

        // Saturation is 0 -> achromatic color (grayscale)
        if (s == 0f) r = g = b = l;
        else {
            // Intermediate values for conversion logic
            float q = l < 0.5f ? l * (1f + s) : l + s - l * s;
            float p = 2f * l - q;

            // Calculate individual RGB channels
            r = hueToRGB(p, q, h + 1f / 3f);
            g = hueToRGB(p, q, h);
            b = hueToRGB(p, q, h - 1f / 3f);
        }

        // Return a new color with the RGB values and the alpha value
        return new Color(Math.round(r * 255f), Math.round(g * 255f), Math.round(b * 255f), alpha);
    }

    /// Helper method: Converts a specific hue segment to an RGB channel value.
    /// @param p intermediate value p
    /// @param q intermediate value q
    /// @param t normalized hue value for the specific channel
    /// @return the normalized channel value within `[0.0, 1.0]`.
    private static float hueToRGB(float p, float q, float t) {
        // Ensure t stays within the [0, 1] circle
        if      (t < 0f) t += 1f;
        else if (t > 1f) t -= 1f;

        // Linear interpolation based on hue segments
        if (t < 1f / 6f) return p + (q - p) * 6f * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    /// Helper method: Brightens the given base color.
    /// @param base the base color
    /// @return the brightened color
    /// @implNote This method simply calls {@link #offsetLightness(Color, float)} with the given base color and returns the result. The lightness offset amount is {@value #LIGHTNESS_CHANGE}.
    private static Color brighten(Color base) {
        // Offset the lightness in the positive direction
        return offsetLightness(base, LIGHTNESS_CHANGE);
    }

    /// Helper method: Darkens the given base color.
    /// @param base the base color
    /// @return the darkened color
    /// @implNote This method simply calls {@link #offsetLightness(Color, float)} with the given base color and returns the result. The lightness offset amount is negative {@value #LIGHTNESS_CHANGE}.
    private static Color darken(Color base) {
        // Offset the lightness in the negative direction
        return offsetLightness(base, -LIGHTNESS_CHANGE);
    }

    /// Helper method: Returns the grayscale representation of the given base color.
    /// @param base the base color
    /// @return the grayscale color
    private static Color grayscale(Color base) {
        // Calculate the luminance value with precomputed RGB factors
        int luminance = (int) Math.round(base.getRed() * 0.299 + base.getGreen() * 0.587 + base.getBlue() * 0.114);

        // Return a new grayscale color with the luminance value, preserving the alpha channel
        return new Color(luminance, luminance, luminance, base.getAlpha());
    }
}