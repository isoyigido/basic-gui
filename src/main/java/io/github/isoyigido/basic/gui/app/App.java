package io.github.isoyigido.basic.gui.app;

import java.awt.*;
import java.util.Objects;

/// Stores application appearance.
///
/// Includes:
/// - {@link #getColorProfile()}
/// - {@link #getFont(float)}
///
/// @see ColorProfile
/// @see Font
public final class App {
    /// Private constructor to prevent instantiation
    private App() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    /// Current color profile of the application
    private static ColorProfile colorProfile = new ColorProfile(
            ColorSet.of(Color.BLACK, false),
            ColorSet.of(Color.WHITE, true),
            ColorSet.of(Color.WHITE, true),
            ColorSet.of(Color.RED, true),
            ColorSet.of(Color.GREEN, true),
            ColorSet.of(Color.WHITE, true),
            ColorSet.of(Color.WHITE, true)
    );

    /// Returns the current color profile of the application.
    /// @return the current color profile
    /// @see ColorProfile
    public static ColorProfile getColorProfile() {
        return colorProfile;
    }

    /// Sets the color profile of the application to the given color profile.
    /// @param colorProfile the new color profile to be set
    /// @throws NullPointerException if the input `colorProfile` is null
    /// @see ColorProfile
    public static void setColorProfile(ColorProfile colorProfile) {
        Objects.requireNonNull(colorProfile, "Color profile cannot be null.");

        App.colorProfile = colorProfile;
    }

    /// The default font size of the application
    private static final int DEFAULT_FONT_SIZE = 24;

    /// Current text font of the application
    private static Font font = new Font("Calibri", Font.PLAIN, App.DEFAULT_FONT_SIZE);

    /// Returns the current text font of the application with the given font size.
    /// @param size the font size (in points)
    /// @return the current text font with the given font size
    /// @implNote This method uses {@link Font#deriveFont(int, float)} to derive a plain font with the given size from the current font.
    /// @see Font
    public static Font getFont(float size) {
        return font.deriveFont(Font.PLAIN, size);
    }

    /// Sets the text font of the application to the given text font.
    /// @param font the new text font to be set
    /// @throws NullPointerException if the input `font` is null
    /// @see Font
    public static void setFont(Font font) {
        Objects.requireNonNull(font, "Application font cannot be null.");

        App.font = font;
    }
}