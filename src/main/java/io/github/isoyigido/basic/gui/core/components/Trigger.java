package io.github.isoyigido.basic.gui.core.components;

import io.github.isoyigido.basic.gui.constants.Cursors;
import io.github.isoyigido.basic.gui.core.Component;
import io.github.isoyigido.basic.gui.core.GUIManager;
import io.github.isoyigido.basic.gui.core.MouseButton;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/// Represents an invisible rectangular area that performs an action when clicked on.
/// Provides methods for setting actions for different mouse buttons,
/// setting a special cursor that is shown when hovering over the area,
/// and externally registering a click.
/// Can be deactivated to stop performing the actions and showing the special cursor.
public class Trigger extends Component {
    /// Whether this trigger is active
    private boolean active;

    /// Whether the mouse cursor is currently hovering over this trigger
    private boolean hovered = false;

    /// Maps a mouse button to the action that is run when it is clicked.
    private final Map<MouseButton, Runnable> buttonActionMap = new EnumMap<>(MouseButton.class);

    /// The cursor that is shown when hovering over this trigger
    private Cursor specialCursor = null;

    /// Whether the special cursor for this trigger is currently shown
    private boolean specialCursorShown = false;

    /// Constructs an active trigger with the given dimensions.
    /// @param width the width of the trigger area
    /// @param height the height of the trigger area
    public Trigger(int width, int height) {
        super(width, height);

        this.setActive(true);
    }

    /// Registers a click on this trigger.
    /// If this trigger is active and the given mouse button has a set action, runs the action.
    /// @param mouseButton the mouse button that is clicked
    /// @throws NullPointerException if the input `mouseButton` is null
    public void click(MouseButton mouseButton) {
        Objects.requireNonNull(mouseButton, "Clicked mouse button cannot be null");

        // If the trigger is active and the clicked mouse button has a set action, run it
        if (this.active) this.getMouseButtonAction(mouseButton).ifPresent(Runnable::run);
    }

    /// Updates whether the mouse cursor is hovering over this trigger.
    /// If this trigger has a special cursor, is active, and is hovered,
    /// sets the mouse cursor to the special cursor.
    @Override
    public void mouseMoveEvent(int x, int y) {
        // Update hovered status
        this.hovered = this.contains(x, y);

        // If there is no special cursor, return
        if (this.specialCursor == null) return;

        // Check the desired state: should the special cursor be shown?
        boolean shouldShowCustomCursor = this.active && this.hovered;

        // If the cursor should be shown, but it is not shown
        if (shouldShowCustomCursor && !this.specialCursorShown) {
            // Set the cursor to the special cursor
            GUIManager.setCursor(this.specialCursor);
        }
        // Else if the cursor should not be shown, but it is shown
        else if (!shouldShowCustomCursor && this.specialCursorShown) {
            // Set the cursor to the default cursor
            GUIManager.setCursor(Cursors.DEFAULT);
        }

        // Update special cursor shown flag
        this.specialCursorShown = shouldShowCustomCursor;
    }

    /// If the clicked coordinates lie on this trigger, calls {@link #click} with the clicked mouse button.
    @Override
    public void mouseClickEvent(int x, int y, MouseButton mouseButton) {
        // If clicked on the trigger, register a click
        if (this.contains(x, y)) this.click(mouseButton);
    }

    // --- GETTERS ---
    /// Returns whether this trigger is active.
    /// @return whether this trigger is active
    public boolean isActive() {
        return this.active;
    }

    /// Whether the mouse cursor is currently hovering over this trigger.
    /// @return whether the mouse cursor is currently hovering over this trigger
    public boolean isHovered() {
        return this.hovered;
    }

    /// Returns an {@link Optional} containing the action that is run when the given mouse button is clicked,
    ///         or an empty {@link Optional} if no action is run.
    /// @param mouseButton the mouse button that is clicked
    /// @return an {@link Optional} containing the action that is run when the given mouse button is clicked,
    ///         or an empty {@link Optional} if no action is run
    public Optional<Runnable> getMouseButtonAction(MouseButton mouseButton) {
        // Get the action from the map and return an optional containing it
        return Optional.ofNullable(this.buttonActionMap.get(mouseButton));
    }

    /// Returns the cursor that is shown when hovering over this trigger.
    /// @return the cursor that is shown when hovering over this trigger
    public Cursor getSpecialCursor() {
        return this.specialCursor;
    }

    // --- SETTERS ---
    /// Sets whether this trigger is active.
    /// @param active whether this trigger is active
    /// @return this
    public Trigger setActive(boolean active) {
        // Set active status
        this.active = active;

        // Return this
        return this;
    }

    /// Activates this trigger.
    /// @return this
    public Trigger activate() {
        // Set active to true
        this.setActive(true);

        // Return this
        return this;
    }

    /// Deactivates this trigger.
    /// @return this
    public Trigger deactivate() {
        // Set active to false
        this.setActive(false);

        // Return this
        return this;
    }

    /// Sets the action that is run when the given mouse button is clicked.
    /// @param mouseButton the mouse button that is clicked
    /// @param action the action that is run
    /// @return this
    public Trigger setMouseButtonAction(MouseButton mouseButton, Runnable action) {
        Objects.requireNonNull(mouseButton, "Mouse button to set action for cannot be null.");
        Objects.requireNonNull(action, "Mouse button action cannot be null.");

        // Put the mouse button-action pair in the map
        this.buttonActionMap.put(mouseButton, action);

        // Return this
        return this;
    }

    /// Sets the mouse cursor that is shown when hovering over this trigger.
    /// @param specialCursor the mouse cursor that is shown
    /// @return this
    public Trigger setSpecialCursor(Cursor specialCursor) {
        // Set the special cursor
        this.specialCursor = specialCursor;

        // Return this
        return this;
    }

    /// Stops showing the special mouse cursor when hovering over this trigger.
    /// @return this
    /// @apiNote This method is functionally the same as `setSpecialCursor(null)`.
    ///          Use this method to improve readability.
    public Trigger removeSpecialCursor() {
        // Set the special cursor to null
        this.setSpecialCursor(null);

        // Return this
        return this;
    }
}