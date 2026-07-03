package io.github.isoyigido.basic.gui.core;

import io.github.isoyigido.basic.gui.window.ScreenConfig;

import java.awt.event.*;

/// Forwards the mouse events to {@link GUIManager}.
/// @see MouseListener
/// @see MouseMotionListener
/// @see MouseWheelListener
/// @see GUIManager
public final class MouseInputListener extends MouseAdapter {
    /// The x-coordinate of the mouse cursor on the virtual screen
    private int mouseX;

    /// The y-coordinate of the mouse cursor on the virtual screen
    private int mouseY;

    /// The lastly pressed mouse button that is still currently pressed ({@link MouseButton#NONE} if no mouse button is currently pressed)
    private MouseButton mouseButton = MouseButton.NONE;

    @Override
    public void mouseClicked(MouseEvent e) {
        // Register a mouse click event
        GUIManager.onMouseClicked(this.mouseX, this.mouseY, MouseButton.get(e));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Determine the click type
        this.mouseButton = MouseButton.get(e);

        // Register a mouse press event
        GUIManager.onMousePressed(this.mouseX, this.mouseY, this.mouseButton);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Register a mouse release event
        GUIManager.onMouseReleased(MouseButton.get(e));

        // Set pressed mouse button to none
        this.mouseButton = MouseButton.NONE;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Convert the native mouse coordinates to coordinates on the set screen dimensions
        this.mouseX = Math.round(e.getX() / ScreenConfig.windowToVirtualRatioX);
        this.mouseY = Math.round(e.getY() / ScreenConfig.windowToVirtualRatioY);

        // Register a mouse move event
        GUIManager.onMouseMoved(this.mouseX, this.mouseY);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Register a mouse move event
        this.mouseMoved(e);

        // Register a mouse drag event
        GUIManager.onMouseDragged(this.mouseX, this.mouseY, this.mouseButton);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Register a mouse wheel move event
        GUIManager.onMouseWheelMoved(e);
    }

    /// Returns the x-coordinate of the mouse cursor on the virtual screen.
    /// @return the x-coordinate of the mouse cursor
    public int getMouseX() {
        return this.mouseX;
    }

    /// Returns the y-coordinate of the mouse cursor on the virtual screen.
    /// @return the y-coordinate of the mouse cursor
    public int getMouseY() {
        return this.mouseY;
    }
}