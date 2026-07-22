package io.github.isoyigido.basic.gui.core;

import io.github.isoyigido.basic.gui.window.ScreenConfig;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/// Represents a displayable widget that contains a {@link Component} object.
/// Forwards the rendering, update, and input event methods to the contained {@link Component} object.
/// Holds the location of the contained {@link Component} object on the screen,
/// and handles local coordinate transformations for rendering and input event methods.
/// @see Component
/// @see GUI
public final class Widget {
    /// The component that is contained
    private final Component component;

    /// The x-coordinate of the anchor point on the screen
    private final int anchorX;

    /// The y-coordinate of the anchor point on the screen
    private final int anchorY;

    /// The point on this widget anchored to the anchor point
    private final Anchor anchor;

    /// The x-coordinate of the top-left corner of this widget
    private int x;

    /// The y-coordinate of the top-left corner of this widget
    private int y;

    /// Whether this widget is visible
    private boolean visible = true;

    /// The layer index of this widget. Dictates which widget is rendered on top and receives the input events first.
    private int layerIndex = 0;

    /// The GUI that contains this widget
    private GUI gui = null;

    /// Represents anchor points on a widget.
    public enum Anchor {
        /// The center point
        CENTER,

        /// The middle-left point
        LEFT,

        /// The middle-right point
        RIGHT,

        /// The top-center point
        TOP,

        /// The bottom-center point
        BOTTOM,

        /// The top-left corner
        TOP_LEFT,

        /// The top-right corner
        TOP_RIGHT,

        /// The bottom-left corner
        BOTTOM_LEFT,

        /// The bottom-right corner
        BOTTOM_RIGHT
    }

    /// Constructs a widget that contains the given component and is anchored to the given point on the screen.
    /// @param component the component that is contained in the widget
    /// @param anchorX the x-coordinate of the anchor point on the screen
    /// @param anchorY the y-coordinate of the anchor point on the screen
    /// @param anchor the point on the widget anchored to the anchor point
    /// @throws NullPointerException if the input `component` is null
    public Widget(Component component, int anchorX, int anchorY, Anchor anchor) {
        Objects.requireNonNull(component, "Contained component cannot be null.");

        // Set the widget containing the component to this
        component.setWidget(this);

        // Set the contained component
        this.component = component;

        // Set anchor point coordinates
        this.anchorX = anchorX;
        this.anchorY = anchorY;

        // Set the anchor
        this.anchor = anchor;

        // Update the position of this widget
        this.updateX();
        this.updateY();
    }

    /// Updates the x-coordinate of this widget to stay anchored to the anchor point.
    void updateX() {
        // Set the x-coordinate based on the anchor
        this.x = switch (this.anchor) {
            case TOP_LEFT, LEFT, BOTTOM_LEFT    -> this.anchorX;
            case TOP, CENTER, BOTTOM            -> this.anchorX - (this.component.width / 2);
            case TOP_RIGHT, RIGHT, BOTTOM_RIGHT -> this.anchorX - this.component.width;
        };
    }

    /// Updates the y-coordinate of this widget to stay anchored to the anchor point.
    void updateY() {
        // Set the y-coordinate based on the anchor point
        this.y = switch (this.anchor) {
            case TOP_LEFT, TOP, TOP_RIGHT          -> this.anchorY;
            case LEFT, CENTER, RIGHT               -> this.anchorY - (this.component.height / 2);
            case BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT -> this.anchorY - this.component.height;
        };
    }

    /// Renders the component contained in this widget.
    /// Forwards the render call to each child widget.
    /// @param g the graphics context to render on
    public void render(Graphics2D g) {
        // If this widget is not visible, return
        if (!this.visible) return;

        // Create a local copy of the graphics context
        Graphics2D gLocal = (Graphics2D) g.create();

        // Translate the local graphics context to the position of this widget
        gLocal.translate(this.x, this.y);

        // If the contained component is to be clipped, set the clip to its bounding box
        if (this.component.clipped) gLocal.clip(this.component.boundingBox);

        // Translate the local graphics context by the rendering offset of the contained component
        gLocal.translate(this.component.offsetX, this.component.offsetY);

        // Render the contained component on the local graphics context
        this.component.render(gLocal);

        // Forward the render call to each child widget,
        // render the child widgets on the local, translated graphics context
        this.component.childWidgets.forEach(w -> w.render(gLocal));

        // Dispose of the local graphics
        gLocal.dispose();
    }

    /// Updates the component contained in this widget.
    /// Forwards the update call to each child widget.
    public void update() {
        // Update the contained component
        this.component.update();

        // Forward the update call to each child widget
        this.component.childWidgets.forEach(Widget::update);
    }

    /// Recursively sorts the child widgets of this widget based on their layer indices.
    /// Forwards this call to the child widgets to ensure their child widgets are also sorted.
    void sortChildWidgetsBasedOnLayerIndex() {
        // Sort the child widgets based on their layer indices
        Widget.sortBasedOnLayerIndex(this.component.childWidgets);

        // Forward the call to each child widget
        this.component.childWidgets.forEach(Widget::sortChildWidgetsBasedOnLayerIndex);
    }

    /// Sorts the given list of widgets based on their layer indices,
    /// where the widget with the smallest layer index appears first on the list.
    /// @param widgets the list of widgets to sort
    /// @throws NullPointerException if the input `widgets` is null
    static void sortBasedOnLayerIndex(List<Widget> widgets) {
        Objects.requireNonNull(widgets, "List of widgets to sort cannot be null.");

        // Sort the widgets based on their layer indices
        widgets.sort(Comparator.comparingInt(Widget::getLayerIndex));
    }

    // --- GETTERS ---
    /// Returns the component contained in this widget.
    /// @return the component contained in this widget
    public Component getComponent() {
        return this.component;
    }

    /// Returns the x-coordinate of the top-left corner of this widget.
    /// @return the x-coordinate of the top-left corner of this widget
    public int getX() {
        return this.x;
    }

    /// Returns the y-coordinate of the top-left corner of this widget.
    /// @return the y-coordinate of the top-left corner of this widget
    public int getY() {
        return this.y;
    }

    /// Returns whether this widget is visible.
    /// @return whether this widget is visible
    public boolean isVisible() {
        return this.visible;
    }

    /// Returns the layer index of this widget, which dictates which widget is rendered on top and receives the input events first.
    /// @return the layer index of this widget, which dictates which widget is rendered on top and receives the input events first
    public int getLayerIndex() {
        return this.layerIndex;
    }

    /// Returns the GUI that contains this widget.
    /// @return the GUI that contains this widget
    public GUI getGUI() {
        return this.gui;
    }

    // --- SETTERS ---
    /// Sets the visibility of this widget.
    /// @param visible whether this widget should be visible
    /// @return this
    public Widget setVisible(boolean visible) {
        // Set visibility
        this.visible = visible;

        // Return this
        return this;
    }

    /// Shows this widget.
    /// @return this
    public Widget show() {
        // Set visibility to true
        this.setVisible(true);

        // Return this
        return this;
    }

    /// Hides this widget.
    /// @return this
    public Widget hide() {
        // Set visibility to false
        this.setVisible(false);

        // Return this
        return this;
    }

    /// Sets the GUI that contains this widget and its child widgets.
    /// @param gui the GUI that contains this widget and its child widgets
    void setGUI(GUI gui) {
        // Set the GUI
        this.gui = gui;

        // Set the GUI of each child widget
        this.component.childWidgets.forEach(w -> w.setGUI(gui));
    }

    /// Sets the layer index of this widget, which dictates which widget is rendered on top and receives the input events first.
    /// @param layerIndex the new layer index of this widget
    /// @return this
    public Widget setLayerIndex(int layerIndex) {
        // Set the layer index
        this.layerIndex = layerIndex;

        // Return this
        return this;
    }

    // --- INPUT EVENT METHODS ---
    /// Forwards the mouse click event to the contained component and its child widgets. Localizes the input coordinates.
    /// @param x the x-coordinate of the click
    /// @param y the y-coordinate of the click
    /// @param mouseButton the mouse button that is clicked
    void onMouseClicked(int x, int y, MouseButton mouseButton) {
        // Convert the input coordinates to local coordinates
        int xLocal = x - this.x - this.component.offsetX;
        int yLocal = y - this.y - this.component.offsetY;

        // Forward the mouse click event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onMouseClicked(xLocal, yLocal, mouseButton);
        }

        // Forward the mouse click event to the contained component
        this.component.mouseClickEvent(xLocal, yLocal, mouseButton);
    }

    /// Forwards the mouse press event to the contained component and its child widgets. Localizes the input coordinates.
    /// @param x the x-coordinate of the press
    /// @param y the y-coordinate of the press
    /// @param mouseButton the mouse button that is pressed
    void onMousePressed(int x, int y, MouseButton mouseButton) {
        // Convert the input coordinates to local coordinates
        int xLocal = x - this.x - this.component.offsetX;
        int yLocal = y - this.y - this.component.offsetY;

        // Forward the mouse press event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onMousePressed(xLocal, yLocal, mouseButton);
        }

        // Forward the mouse press event to the contained component
        this.component.mousePressEvent(xLocal, yLocal, mouseButton);
    }

    /// Forwards the mouse release event to the contained component and its child widgets.
    /// @param mouseButton the mouse button that is released
    void onMouseReleased(MouseButton mouseButton) {
        // Forward the mouse release event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onMouseReleased(mouseButton);
        }

        // Forward the mouse release event to the contained component
        this.component.mouseReleaseEvent(mouseButton);
    }

    /// Forwards the mouse move event to the contained component and its child widgets. Localizes the input coordinates.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    void onMouseMoved(int x, int y) {
        // Convert the input coordinates to local coordinates
        int xLocal = x - this.x - this.component.offsetX;
        int yLocal = y - this.y - this.component.offsetY;

        // Forward the mouse move event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onMouseMoved(xLocal, yLocal);
        }

        // Forward the mouse move event to the contained component
        this.component.mouseMoveEvent(xLocal, yLocal);
    }

    /// Forwards the mouse drag event to the contained component and its child widgets. Localizes the input coordinates.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    /// @param mouseButton the mouse button that is dragged
    void onMouseDragged(int x, int y, MouseButton mouseButton) {
        // Convert the input coordinates to local coordinates
        int xLocal = x - this.x - this.component.offsetX;
        int yLocal = y - this.y - this.component.offsetY;

        // Forward the mouse drag event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onMouseDragged(xLocal, yLocal, mouseButton);
        }

        // Forward the mouse drag event to the contained component
        this.component.mouseDragEvent(xLocal, yLocal, mouseButton);
    }

    /// Forwards the mouse wheel move event to the contained component and its child widgets.
    /// @param e the mouse wheel event
    void onMouseWheelMoved(MouseWheelEvent e) {
        // Forward the mouse wheel move event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onMouseWheelMoved(e);
        }

        // Forward the mouse wheel move event to the contained component
        this.component.mouseWheelEvent(e);
    }

    /// Forwards the key typing event to the contained component and its child widgets.
    /// @param e the key event
    void onKeyTyped(KeyEvent e) {
        // Forward the key typing event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onKeyTyped(e);
        }

        // Forward the key typing event to the contained component
        this.component.keyTypingEvent(e);
    }

    /// Forwards the key pressing event to the contained component and its child widgets.
    /// @param e the key event
    void onKeyPressed(KeyEvent e) {
        // Forward the key press event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onKeyPressed(e);
        }

        // Forward the key press event to the contained component
        this.component.keyPressEvent(e);
    }

    /// Forwards the key releasing event to the contained component and its child widgets.
    /// @param e the key event
    void onKeyReleased(KeyEvent e) {
        // Forward the key release event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onKeyReleased(e);
        }

        // Forward the key release event to the contained component
        this.component.keyReleaseEvent(e);
    }

    // --- FACTORY METHODS ---
    /// Returns a widget containing the given component centered at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget center(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.CENTER);
    }

    /// Returns a widget containing the given component whose middle-left point lies at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget left(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.LEFT);
    }

    /// Returns a widget containing the given component whose middle-right point lies at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget right(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.RIGHT);
    }

    /// Returns a widget containing the given component whose top-center point lies at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget top(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.TOP);
    }

    /// Returns a widget containing the given component whose bottom-center point lies at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget bottom(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.BOTTOM);
    }

    /// Returns a widget containing the given component whose top-left corner lies at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget topLeft(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.TOP_LEFT);
    }

    /// Returns a widget containing the given component whose top-right corner lies at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget topRight(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.TOP_RIGHT);
    }

    /// Returns a widget containing the given component whose bottom-left corner lies at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget bottomLeft(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.BOTTOM_LEFT);
    }

    /// Returns a widget containing the given component whose bottom-right corner lies at the given coordinates.
    /// @param component the contained component
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new `Widget` object containing the given component
    static Widget bottomRight(Component component, int x, int y) {
        return new Widget(component, x, y, Anchor.BOTTOM_RIGHT);
    }

    /// Returns a widget containing the given component centered at the center of the screen.
    /// @param component the contained component
    /// @return a new `Widget` object containing the given component
    static Widget center(Component component) {
        return Widget.center(component, ScreenConfig.xCenter, ScreenConfig.yCenter);
    }
}