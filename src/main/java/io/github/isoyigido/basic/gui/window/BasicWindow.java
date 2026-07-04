package io.github.isoyigido.basic.gui.window;

import io.github.isoyigido.basic.gui.core.GUIManager;
import io.github.isoyigido.basic.gui.main.Main;

import javax.swing.*;
import java.awt.*;

/// Stores basic properties for a window to be displayed.
/// Provides a {@link #show(int, int)} method to set up and show the window.
/// @see JFrame
/// @see BasicPanel
/// @see ScreenConfig
public class BasicWindow {
    /// The title of the window
    private final String title;

    /// The icon image of the window
    private final Image iconImage;

    /// Whether the window is undecorated
    private final boolean undecorated;

    /// Whether the window is resizable
    private final boolean resizable;

    /// The width of the window (in pixels)
    private final int windowWidth;

    /// The height of the window (in pixels)
    private final int windowHeight;

    /// The desired width for the virtual screen (in pixels)
    private final int desiredScreenWidth;

    /// The desired height for the virtual screen (in pixels)
    private final int desiredScreenHeight;

    /// Whether the aspect ratio of the physical screen displaying the window should be preserved on the virtual screen
    private final boolean preserveNativeAspectRatio;

    /// Constructs a basic window to be displayed.
    /// @param title the title of the window (null for default title)
    /// @param iconImage the icon image of the window (null for default icon)
    /// @param undecorated whether the window is undecorated
    /// @param resizable whether the window is resizable
    /// @param windowWidth the width of the window (in pixels)
    /// @param windowHeight the height of the window (in pixels)
    /// @param desiredScreenWidth the desired width for the virtual screen (in pixels)
    /// @param desiredScreenHeight the desired height for the virtual screen (in pixels)
    /// @param preserveNativeAspectRatio whether the aspect ratio of the physical screen displaying the window should be preserved on the virtual screen
    public BasicWindow(
            String title,
            Image iconImage,
            boolean undecorated,
            boolean resizable,
            int windowWidth, int windowHeight,
            int desiredScreenWidth, int desiredScreenHeight,
            boolean preserveNativeAspectRatio
    ) {
        this.title = title;
        this.iconImage = iconImage;
        this.undecorated = undecorated;
        this.resizable = resizable;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.desiredScreenWidth = desiredScreenWidth;
        this.desiredScreenHeight = desiredScreenHeight;
        this.preserveNativeAspectRatio = preserveNativeAspectRatio;
    }

    /// Shows this window and updates static fields in {@link ScreenConfig}.
    /// @param fps number of frames (render calls) per second
    /// @param ups number of updates (update calls) per second
    public void show(int fps, int ups) {
        // Set the virtual screen dimensions
        BasicWindow.setVirtualScreenDimensions(
                this.windowWidth, this.windowHeight,
                this.desiredScreenWidth, this.desiredScreenHeight,
                this.preserveNativeAspectRatio
        );

        // Invoke later to prevent multithreading bugs
        SwingUtilities.invokeLater(() -> {
            // --- Set up the window ---
            // Initialize the JFrame window
            JFrame jFrame = new JFrame();

            // Set the title if there is one
            if (this.title != null) jFrame.setTitle(this.title);

            // Set the application icon if there is one
            if (this.iconImage != null) jFrame.setIconImage(this.iconImage);

            // Set whether the window is undecorated
            jFrame.setUndecorated(this.undecorated);

            // Set whether the window is resizable
            jFrame.setResizable(this.resizable);

            // Set default close operation to exit the program when the window is closed
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            // Initialize the panel with the window dimensions
            BasicPanel panel = new BasicPanel(this.windowWidth, this.windowHeight);

            // Set the panel of the GUI manager
            GUIManager.setPanel(panel);

            // Link the panel
            jFrame.add(panel);

            // Pack the window
            jFrame.pack();

            // Center the window
            jFrame.setLocationRelativeTo(null);

            // Show the window
            jFrame.setVisible(true);

            // Focus on the panel
            panel.setFocusable(true);
            panel.requestFocusInWindow();

            // Start the main loop
            Main.startMainLoop(panel, fps, ups);
        });
    }

    /// Sets the dimensions of the virtual screen in {@link ScreenConfig} based on the given desired dimensions.
    /// If the native aspect ratio is to be preserved, the virtual screen dimensions set
    /// may not match the desired dimensions. In that case, the total pixel count is tried to be preserved.
    /// @param windowWidth the width of the displayed window (in pixels)
    /// @param windowHeight the height of the displayed window (in pixels)
    /// @param desiredScreenWidth the desired width of the virtual screen (in pixels)
    /// @param desiredScreenHeight the desired height of the virtual screen (in pixels)
    /// @param preserveNativeAspectRatio whether the aspect ratio of the physical screen displaying the window should be preserved on the virtual screen
    /// @see ScreenConfig
    private static void setVirtualScreenDimensions(int windowWidth, int windowHeight, int desiredScreenWidth, int desiredScreenHeight, boolean preserveNativeAspectRatio) {
        // Set the window width and height
        ScreenConfig.windowWidth = windowWidth;
        ScreenConfig.windowHeight = windowHeight;

        // If the native aspect ratio is to be preserved
        if (preserveNativeAspectRatio) {
            // Calculate the area that should be preserved
            final int targetArea = desiredScreenWidth * desiredScreenHeight;

            // Set the width and height based on the aspect, preserving the target area
            ScreenConfig.screenWidth = (int) Math.round(Math.sqrt(targetArea * ScreenConfig.actualScreenAspectRatio));
            ScreenConfig.screenHeight = Math.round(ScreenConfig.screenWidth / ScreenConfig.actualScreenAspectRatio);
        } else {
            // Set the virtual screen width and height to the desired values
            ScreenConfig.screenWidth = desiredScreenWidth;
            ScreenConfig.screenHeight = desiredScreenHeight;
        }

        // Set the coordinates of the virtual screen center
        ScreenConfig.xCenter = ScreenConfig.screenWidth / 2;
        ScreenConfig.yCenter = ScreenConfig.screenHeight / 2;

        // Set the ratio of the window dimensions to the virtual screen dimensions
        ScreenConfig.windowToVirtualRatioX = (float) windowWidth / ScreenConfig.screenWidth;
        ScreenConfig.windowToVirtualRatioY = (float) windowHeight / ScreenConfig.screenHeight;
    }
}