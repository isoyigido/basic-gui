package io.github.isoyigido.basic.gui.window;

import java.awt.*;

/// Stores configuration for the screen and displayed window.
/// @see GraphicsEnvironment
/// @see GraphicsConfiguration
public class ScreenConfig {
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
    public static int windowWidth;

    /// The height of the displayed window (in pixels)
    public static int windowHeight;

    /// The width of the virtual screen (in pixels)
    public volatile static int screenWidth;

    /// The height of the virtual screen (in pixels)
    public static int screenHeight;

    /// The x-coordinate of the virtual screen center (helper value)
    public static int xCenter;

    /// The y-coordinate of the virtual screen center (helper value)
    public static int yCenter;

    /// The ratio of the window width to the virtual screen width
    public static float windowToVirtualRatioX;

    /// The ratio of the window height to the virtual screen height
    public static float windowToVirtualRatioY;

    /// Sets the dimensions of the virtual screen based on the given desired dimensions.
    /// If the native aspect ratio is to be preserved, the virtual screen dimensions set
    /// may not match the desired dimensions. In that case, the total pixel count is tried to be preserved.
    /// @param windowWidth the width of the displayed window (in pixels)
    /// @param windowHeight the height of the displayed window (in pixels)
    /// @param desiredScreenWidth the desired width of the virtual screen (in pixels)
    /// @param desiredScreenHeight the desired height of the virtual screen (in pixels)
    /// @param preserveNativeAspectRatio whether the aspect ratio of the physical screen displaying the window should be preserved on the virtual screen
    public static void setVirtualScreenDimensions(int windowWidth, int windowHeight, int desiredScreenWidth, int desiredScreenHeight, boolean preserveNativeAspectRatio) {
        // Set the window width and height
        ScreenConfig.windowWidth = windowWidth;
        ScreenConfig.windowHeight = windowHeight;

        // If the native aspect ratio is to be preserved
        if (preserveNativeAspectRatio) {
            // Calculate the area that should be preserved
            final int targetArea = desiredScreenWidth * desiredScreenHeight;

            // Set the width and height based on the aspect, preserving the target area
            screenWidth = (int) Math.round(Math.sqrt(targetArea * actualScreenAspectRatio));
            screenHeight = Math.round(screenWidth / actualScreenAspectRatio);
        } else {
            // Set the virtual screen width and height to the desired values
            screenWidth = desiredScreenWidth;
            screenHeight = desiredScreenHeight;
        }

        // Set the coordinates of the virtual screen center
        xCenter = screenWidth / 2;
        yCenter = screenHeight / 2;

        // Set the ratio of the window dimensions to the virtual screen dimensions
        windowToVirtualRatioX = (float) windowWidth / screenWidth;
        windowToVirtualRatioY = (float) windowHeight / screenHeight;
    }
}