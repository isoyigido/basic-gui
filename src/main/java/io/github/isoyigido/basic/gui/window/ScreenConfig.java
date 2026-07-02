package io.github.isoyigido.basic.gui.window;

import java.awt.*;

/// Stores configuration for the screen and displayed window.
/// @see GraphicsEnvironment
/// @see GraphicsConfiguration
public final class ScreenConfig {
    /// Private constructor to prevent instantiation
    private ScreenConfig() {
        throw new UnsupportedOperationException("Configuration class cannot be instantiated.");
    }

    /// The default graphics configuration for the local graphics environment
    public static final GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();

    /// The dimensions of the physical screen displaying the window (in pixels)
    public static final Rectangle actualScreenBounds = graphicsConfiguration.getBounds();

    /// The width of the physical screen displaying the window (in pixels)
    public static final int actualScreenWidth = (int) Math.round(actualScreenBounds.getWidth());

    /// The height of the physical screen displaying the window (in pixels)
    public static final int actualScreenHeight = (int) Math.round(actualScreenBounds.getHeight());

    /// The aspect ratio of the physical screen displaying the window
    public static final float actualScreenAspectRatio = (float) actualScreenWidth / actualScreenHeight;

    /// The width of the displayed window (in pixels)
    public static int windowWidth = 0;

    /// The height of the displayed window (in pixels)
    public static int windowHeight = 0;

    /// The width of the virtual screen (in pixels)
    public static int screenWidth = 0;

    /// The height of the virtual screen (in pixels)
    public static int screenHeight = 0;

    /// The x-coordinate of the virtual screen center (helper value)
    public static int xCenter = 0;

    /// The y-coordinate of the virtual screen center (helper value)
    public static int yCenter = 0;

    /// The ratio of the window width to the virtual screen width
    public static float windowToVirtualRatioX = 1.0f;

    /// The ratio of the window height to the virtual screen height
    public static float windowToVirtualRatioY = 1.0f;
}