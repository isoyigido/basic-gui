package io.github.isoyigido.basic.gui.window;

import io.github.isoyigido.basic.gui.app.App;
import io.github.isoyigido.basic.gui.core.GUIManager;
import io.github.isoyigido.basic.gui.core.KeyboardInputListener;
import io.github.isoyigido.basic.gui.core.MouseInputListener;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/// Represents a basic {@link JPanel} instance linked to a {@link JFrame} object.
/// Implements a virtual screen buffer and draws the buffer on the displayed window.
/// Overrides the {@link JPanel#paintComponent(Graphics)} method to render frames
/// on the virtual screen buffer using {@link GUIManager#render(Graphics2D)}.
/// Adds input event listeners which forward the input events to {@link GUIManager}.
/// @see JPanel
/// @see GUIManager
/// @see MouseInputListener
/// @see KeyboardInputListener
public class BasicPanel extends JPanel {
    /// The buffer for the virtual screen
    private final BufferedImage virtualScreen;

    /// The graphics for the virtual screen buffer
    private final Graphics2D virtualScreenGraphics;

    /// Constructs a panel with the given window dimensions. Initializes the buffers and event listeners.
    /// @param windowWidth the width of the displayed window (in pixels)
    /// @param windowHeight the height of the displayed window (in pixels)
    BasicPanel(int windowWidth, int windowHeight) {
        // Initialize the Dimension object with the window dimensions
        Dimension size = new Dimension(windowWidth, windowHeight);

        // Set the minimum, preferred, and maximum size
        super.setMinimumSize(size);
        super.setPreferredSize(size);
        super.setMaximumSize(size);

        // Disable double buffering for performance,
        // buffering is already implemented with the virtual screen buffer
        super.setDoubleBuffered(false);

        // Set opaque to true for performance,
        // this prevents Swing from painting the background of the underlying container
        super.setOpaque(true);

        // Initialize the buffer for the virtual screen with dimensions matching the configured virtual screen dimensions
        this.virtualScreen = new BufferedImage(ScreenConfig.screenWidth, ScreenConfig.screenHeight, BufferedImage.TYPE_INT_ARGB);

        // Create the graphics for the virtual screen buffer
        this.virtualScreenGraphics = this.virtualScreen.createGraphics();

        // Enable Anti-Aliasing on the virtual screen buffer graphics
        this.virtualScreenGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Add event listeners for mouse inputs
        MouseInputListener mouseInputListener = new MouseInputListener();
        super.addMouseListener(mouseInputListener);
        super.addMouseMotionListener(mouseInputListener);
        super.addMouseWheelListener(mouseInputListener);

        // Add event listener for keyboard inputs
        super.addKeyListener(new KeyboardInputListener());
    }

    /// Uses {@link GUIManager#render(Graphics2D)} to render a frame on the virtual screen buffer,
    /// and draws the virtual screen buffer onto the displayed window.
    /// @param g the graphics context to render on
    @Override
    public void paintComponent(Graphics g) {
        // Set the color to the background color of the current color profile
        this.virtualScreenGraphics.setColor(App.getColorProfile().background().base());

        // Fill the background
        this.virtualScreenGraphics.fillRect(0, 0, ScreenConfig.screenWidth, ScreenConfig.screenHeight);

        // Render the GUI on the virtual screen buffer
        GUIManager.render(this.virtualScreenGraphics);

        // Draw the virtual screen buffer onto the displayed window
        g.drawImage(this.virtualScreen, 0, 0, ScreenConfig.windowWidth, ScreenConfig.windowHeight, null);
    }
}