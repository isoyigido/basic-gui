package io.github.isoyigido.basic.gui.core;

import java.awt.*;

/// Defines the contract for an overlay that can be rendered and updated within a GUI context.
/// @see GUIManager
public interface Overlay {
    /// Renders the overlay onto the given graphics context.
    /// @param g the graphics context to render on
    void render(Graphics2D g);

    /// Updates the internal state of the overlay.
    void update();
}