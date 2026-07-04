package io.github.isoyigido.basic.gui.core;

import java.awt.event.MouseEvent;
import java.util.Objects;

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
    /// @param mouseEvent the mouse event to evaluate
    /// @return the corresponding `MouseButton` enum constant,
    ///         or {@link MouseButton#OTHER} if unknown
    /// @throws NullPointerException if the input `mouseEvent` is null
    public static MouseButton get(MouseEvent mouseEvent) {
        Objects.requireNonNull(mouseEvent, "Mouse event to evaluate cannot be null.");

        // Map the mouse event buttons to the enum constants
        return switch (mouseEvent.getButton()) {
            case MouseEvent.BUTTON1 -> MouseButton.LEFT;
            case MouseEvent.BUTTON2 -> MouseButton.MIDDLE;
            case MouseEvent.BUTTON3 -> MouseButton.RIGHT;
            case MouseEvent.NOBUTTON -> MouseButton.NONE;
            default -> MouseButton.OTHER;
        };
    }
}