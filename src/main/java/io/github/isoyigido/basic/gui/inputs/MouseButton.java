package io.github.isoyigido.basic.gui.inputs;

import java.awt.event.MouseEvent;

/// Represents mouse buttons.
/// @see MouseEvent
/// @see MouseInputListener
public enum MouseButton {
    /// Left mouse button
    LEFT,

    /// Right mouse button
    RIGHT,

    /// Middle mouse button
    MIDDLE,

    /// Other mouse buttons
    OTHER,

    /// No mouse button
    NONE

    ;

    /// Returns the interacted mouse button based on the given mouse event.
    /// @param e the mouse event to evaluate
    /// @return the corresponding `MouseButton` enum constant,
    ///         or {@link MouseButton#OTHER} if unknown
    public static MouseButton get(MouseEvent e) {
        // Map the mouse event buttons to the enum constants
        return switch (e.getButton()) {
            case MouseEvent.BUTTON1 -> MouseButton.LEFT;
            case MouseEvent.BUTTON2 -> MouseButton.MIDDLE;
            case MouseEvent.BUTTON3 -> MouseButton.RIGHT;
            case MouseEvent.NOBUTTON -> MouseButton.NONE;
            default -> MouseButton.OTHER;
        };
    }
}