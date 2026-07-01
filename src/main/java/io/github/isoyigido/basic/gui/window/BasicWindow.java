package io.github.isoyigido.basic.gui.window;

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
    protected final String title;

    /// The icon image of the window
    protected final Image iconImage;

    /// Whether the window is undecorated
    protected final boolean undecorated;

    /// Whether the window is resizable
    protected final boolean resizable;

    /// The width of the window (in pixels)
    protected final int windowWidth;

    /// The height of the window (in pixels)
    protected final int windowHeight;

    /// The desired width for the virtual screen (in pixels)
    protected final int desiredScreenWidth;

    /// The desired height for the virtual screen (in pixels)
    protected final int desiredScreenHeight;

    /// Whether the aspect ratio of the physical screen displaying the window should be preserved on the virtual screen
    protected final boolean preserveNativeAspectRatio;

    /// Constructs a basic window to be displayed.
    /// @param title the title of the window
    /// @param iconImage the icon image of the window
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
        ScreenConfig.setVirtualScreenDimensions(
                this.windowWidth, this.windowHeight,
                this.desiredScreenWidth, this.desiredScreenHeight,
                this.preserveNativeAspectRatio
        );

        // Invoke later to prevent multithreading bugs
        SwingUtilities.invokeLater(() -> {
            // --- Set up the window ---
            // Initialize the JFrame window
            JFrame jFrame = new JFrame();

            // Set the title
            jFrame.setTitle(this.title);

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
}