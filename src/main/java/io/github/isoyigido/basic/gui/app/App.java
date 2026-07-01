package io.github.isoyigido.basic.gui.app;

import java.awt.*;

/// Stores application appearance.
///
/// Includes:
/// - {@link #getColorProfile()}
/// - {@link #getFont(float)}
///
/// @see ColorProfile
/// @see Font
public final class App {
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
    /// @see ColorProfile
    public static void setColorProfile(ColorProfile colorProfile) {
        App.colorProfile = colorProfile;
    }

    /// Current text font of the application
    private static Font font = new Font("Calibri", Font.PLAIN, 24);

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
    /// @see Font
    public static void setFont(Font font) {
        App.font = font;
    }
}