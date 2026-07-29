package io.github.isoyigido.basic.gui.window;

import io.github.isoyigido.basic.gui.core.GUIManager;
import io.github.isoyigido.basic.gui.main.Main;

import javax.swing.*;
import java.awt.*;

/// Stores basic properties for a window to be displayed. Provides setters for the properties.
/// Provides the {@link #show(int, int)} method to automatically set up and show the window
/// with the stored properties.
/// @see JFrame
/// @see BasicPanel
/// @see ScreenConfig
public class BasicWindow {
    /// The width of the window (in pixels)
    private final int windowWidth;

    /// The height of the window (in pixels)
    private final int windowHeight;

    /// The desired width for the virtual screen (in pixels)
    private int desiredScreenWidth;

    /// The desired height for the virtual screen (in pixels)
    private int desiredScreenHeight;

    /// Whether the aspect ratio of the physical screen displaying the window should be preserved on the virtual screen
    private boolean preserveNativeAspectRatio = false;

    /// The title of the window (null indicates that no title is set)
    private String title = null;

    /// The icon image of the window (null indicates that no icon image is set)
    private Image iconImage = null;

    /// Whether the window is resizable
    private boolean resizable = false;

    /// Whether the window is undecorated
    private boolean undecorated = false;

    /// Whether the window is always shown on top of other windows
    private boolean alwaysOnTop = false;

    /// Constructs a basic window to be displayed.
    ///
    /// The virtual screen dimensions match the given window dimensions by default.
    /// Use {@link #setVirtualScreenDimensions(int, int)} to set the virtual screen dimensions.
    /// The native aspect ratio of the physical screen displaying the window can be preserved
    /// on the virtual screen using {@link #preserveNativeAspectRatio()}.
    ///
    /// Use {@link #setTitle(String)} to set the title of the window,
    /// and {@link #setIconImage(Image)} to set the icon image of the window.
    ///
    /// The constructed window has the following default properties:
    /// - NOT resizable (use {@link #makeResizable()} to make resizable)
    /// - NOT undecorated (use {@link #makeUndecorated()} to make undecorated)
    /// - NOT always on top (use {@link #makeAlwaysOnTop()} to make always on top)
    ///
    /// @param windowWidth the width of the window (in pixels)
    /// @param windowHeight the height of the window (in pixels)
    public BasicWindow(int windowWidth, int windowHeight) {
        // Set window dimensions
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        // Set virtual screen dimensions to window dimensions by default
        this.desiredScreenWidth = windowWidth;
        this.desiredScreenHeight = windowHeight;
    }

    /// Sets the virtual screen dimensions.
    /// @param desiredScreenWidth the desired width for the virtual screen (in pixels)
    /// @param desiredScreenHeight the desired height for the virtual screen (in pixels)
    /// @return this
    /// @apiNote The virtual screen dimensions set exactly match the desired dimensions, unless
    ///          {@link #preserveNativeAspectRatio()} is used. See {@link #preserveNativeAspectRatio()}
    ///          for more information.
    public BasicWindow setVirtualScreenDimensions(int desiredScreenWidth, int desiredScreenHeight) {
        // Set desired virtual screen dimensions
        this.desiredScreenWidth = desiredScreenWidth;
        this.desiredScreenHeight = desiredScreenHeight;

        // Return this
        return this;
    }

    /// Preserves the aspect ratio of the physical screen displaying the window on the virtual screen.
    /// When used, the virtual screen dimensions set may not match the desired dimensions, while the
    /// total pixel count is tried to be preserved.
    /// @return this
    public BasicWindow preserveNativeAspectRatio() {
        // Enable preservation of native aspect ratio
        this.preserveNativeAspectRatio = true;

        // Return this
        return this;
    }

    /// Sets the title of this window.
    /// @param title the title of this window
    /// @return this
    public BasicWindow setTitle(String title) {
        // Set the title
        this.title = title;

        // Return this
        return this;
    }

    /// Sets the icon image of this window.
    /// @param iconImage the icon image of this window
    /// @return this
    public BasicWindow setIconImage(Image iconImage) {
        // Set the icon image
        this.iconImage = iconImage;

        // Return this
        return this;
    }

    /// Makes this window resizable.
    /// @return this
    public BasicWindow makeResizable() {
        // Set resizable to true
        this.resizable = true;

        // Return this
        return this;
    }

    /// Makes this window undecorated.
    /// @return this
    public BasicWindow makeUndecorated() {
        // Set undecorated to true
        this.undecorated = true;

        // Return this
        return this;
    }

    /// Makes this window always shown on top of other windows.
    /// @return this
    public BasicWindow makeAlwaysOnTop() {
        // Set alwaysOnTop to true
        this.alwaysOnTop = true;

        // Return this
        return this;
    }

    /// Sets up and shows this window. Updates static fields in {@link ScreenConfig}.
    /// @param fps number of frames (render calls) per second
    /// @param ups number of updates (update calls) per second
    public void show(int fps, int ups) {
        // Set the virtual screen dimensions
        BasicWindow.updateScreenConfig(
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

            // Set whether the window is always on top
            jFrame.setAlwaysOnTop(this.alwaysOnTop);

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
    private static void updateScreenConfig(int windowWidth, int windowHeight, int desiredScreenWidth, int desiredScreenHeight, boolean preserveNativeAspectRatio) {
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