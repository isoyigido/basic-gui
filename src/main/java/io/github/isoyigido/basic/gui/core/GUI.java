package io.github.isoyigido.basic.gui.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// Represents a base GUI within the GUI framework.
/// Provides an {@link #addWidget(Widget)} method to add widgets.
/// Provides methods for enabling and disabling input events.
/// @see Widget
/// @see Component
/// @see GUIManager
public class GUI {
    /// The list of widgets contained in this GUI
    private final List<Widget> widgets = new ArrayList<>(8);

    /// Whether input events are enabled
    private boolean inputEnabled = true;

    /// The widget that receives input events, even if input events are disabled
    private Widget exception = null;

    /// Whether this GUI has been compiled
    private boolean compiled = false;

    /// Adds the given widget to this GUI.
    /// @param widget the widget to be added
    /// @throws UnsupportedOperationException if this GUI has already been compiled
    /// @throws NullPointerException if the input `widget` is null
    public void addWidget(Widget widget) {
        // If this GUI has already been compiled, throw an unsupported operation exception
        if (this.compiled) throw new UnsupportedOperationException("Cannot add widgets after compilation.");

        Objects.requireNonNull(widget, "Widget to add cannot be null.");

        // Set the GUI of the widget to this
        widget.setGUI(this);

        // Add the widget to the list
        this.widgets.add(widget);
    }

    /// Compiles this GUI, running {@link #onCompilation()} and sorting the widgets based on their layer indices.
    void compile() {
        // If already compiled, return
        if (this.compiled) return;

        // Run onCompilation
        this.onCompilation();

        // Sort the widgets based on their layer indices
        Widget.sortBasedOnLayerIndex(this.widgets);

        // Recursively sort the child widgets of the widgets based on their layer indices
        this.widgets.forEach(Widget::sortChildWidgetsBasedOnLayerIndex);

        // Set compiled to true
        this.compiled = true;
    }

    /// Override this method to provide logic that is run on the compilation of this GUI.
    public void onCompilation() {}

    /// Forwards the render call to every widget contained in this GUI.
    /// @param g the graphics context to render on
    public void render(Graphics2D g) {
        // Forward the call to each widget in the list
        this.widgets.forEach(w -> w.render(g));
    }

    /// Forwards the update call to every widget contained in this GUI.
    public void update() {
        // Update each widget
        this.widgets.forEach(Widget::update);
    }

    // --- GETTERS ---
    /// Returns the list of widgets contained in this GUI.
    /// @return the list of widgets contained in this GUI
    public List<Widget> getWidgets() {
        return this.widgets;
    }

    /// Returns whether input events are enabled.
    /// @return whether input events are enabled
    public boolean isInputEnabled() {
        return this.inputEnabled;
    }

    // --- SETTERS ---
    /// Enables input events for every widget in this GUI.
    public void enableInput() {
        // Enable input
        this.inputEnabled = true;

        // Remove the exception
        this.exception = null;
    }

    /// Disables input events for every widget in this GUI.
    public void disableInput() {
        // Disable input with no exception
        this.disableInput(null);
    }

    /// Disables input events for every widget in this GUI, with an exception.
    /// @param exception the widget that receives input events regardless
    public void disableInput(Widget exception) {
        // Disable input
        this.inputEnabled = false;

        // Set the exception
        this.exception = exception;
    }

    // --- INPUT EVENT METHODS ---
    /// Forwards the mouse click event to the widgets contained in this GUI.
    /// @param x the x-coordinate of the click
    /// @param y the y-coordinate of the click
    /// @param mouseButton the mouse button that is clicked
    /// @return whether the mouse click event is consumed
    boolean onMouseClicked(int x, int y, MouseButton mouseButton) {
        // If input is enabled, forward the mouse click event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            if (this.widgets.get(i).onMouseClicked(x, y, mouseButton)) return true;
        }

        // Forward the mouse click event to the exception (if there is one)
        else if (this.exception != null) return this.exception.onMouseClicked(x, y, mouseButton);

        // Return false
        return false;
    }

    /// Forwards the mouse press event to the widgets contained in this GUI.
    /// @param x the x-coordinate of the press
    /// @param y the y-coordinate of the press
    /// @param mouseButton the mouse button that is pressed
    void onMousePressed(int x, int y, MouseButton mouseButton) {
        // If input is enabled, forward the mouse press event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            this.widgets.get(i).onMousePressed(x, y, mouseButton);
        }

        // Forward the mouse press event to the exception (if there is one)
        else if (this.exception != null) this.exception.onMousePressed(x, y, mouseButton);
    }

    /// Forwards the mouse release event to the widgets contained in this GUI.
    /// @param mouseButton the mouse button that is released
    void onMouseReleased(MouseButton mouseButton) {
        // If input is enabled, forward the mouse release event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            this.widgets.get(i).onMouseReleased(mouseButton);
        }

        // Forward the mouse release event to the exception (if there is one)
        else if (this.exception != null) this.exception.onMouseReleased(mouseButton);
    }

    /// Forwards the mouse move event to the widgets contained in this GUI.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    void onMouseMoved(int x, int y) {
        // If input is enabled, forward the mouse move event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            this.widgets.get(i).onMouseMoved(x, y);
        }

        // Forward the mouse move event to the exception (if there is one)
        else if (this.exception != null) this.exception.onMouseMoved(x, y);
    }

    /// Forwards the mouse drag event to the widgets contained in this GUI.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    /// @param mouseButton the mouse button that is dragged
    void onMouseDragged(int x, int y, MouseButton mouseButton) {
        // If input is enabled, forward the mouse drag event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            this.widgets.get(i).onMouseDragged(x, y, mouseButton);
        }

        // Forward the mouse drag event to the exception (if there is one)
        else if (this.exception != null) this.exception.onMouseDragged(x, y, mouseButton);
    }

    /// Forwards the mouse wheel move event to the widgets contained in this GUI.
    /// @param wheelRotation the mouse wheel rotation (1 for down, -1 for up, 0 for partial rotation)
    /// @param preciseWheelRotation the precise mouse wheel rotation (positive values for down, negative values for up)
    void onMouseWheelMoved(int wheelRotation, double preciseWheelRotation) {
        // If input is enabled, forward the mouse wheel event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            this.widgets.get(i).onMouseWheelMoved(wheelRotation, preciseWheelRotation);
        }

        // Forward the mouse wheel event to the exception (if there is one)
        else if (this.exception != null) this.exception.onMouseWheelMoved(wheelRotation, preciseWheelRotation);
    }

    /// Forwards the key typing event to the widgets contained in this GUI.
    /// @param keyChar the typed character
    /// @return whether the key typing event is consumed
    boolean onKeyTyped(char keyChar) {
        // If input is enabled, forward the key typing event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            if (this.widgets.get(i).onKeyTyped(keyChar)) return true;
        }

        // Forward the key typing event to the exception (if there is one)
        else if (this.exception != null) return this.exception.onKeyTyped(keyChar);

        // Return false
        return false;
    }

    /// Forwards the key pressing event to the widgets contained in this GUI.
    /// @param keyCode the key code of the pressed key
    void onKeyPressed(int keyCode) {
        // If input is enabled, forward the key press event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            this.widgets.get(i).onKeyPressed(keyCode);
        }

        // Forward the key press event to the exception (if there is one)
        else if (this.exception != null) this.exception.onKeyPressed(keyCode);
    }

    /// Forwards the key releasing event to the widgets contained in this GUI.
    /// @param keyCode the key code of the released key
    void onKeyReleased(int keyCode) {
        // If input is enabled, forward the key release event to each element
        if (this.inputEnabled) for (int i = this.widgets.size() - 1; i >= 0; i--) {
            this.widgets.get(i).onKeyReleased(keyCode);
        }

        // Forward the key release event to the exception (if there is one)
        else if (this.exception != null) this.exception.onKeyReleased(keyCode);
    }
}