package io.github.isoyigido.basic.gui.core;

import io.github.isoyigido.basic.gui.window.ScreenConfig;

import java.awt.*;
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
    private int anchorX;

    /// The y-coordinate of the anchor point on the screen
    private int anchorY;

    /// The point on this widget anchored to the anchor coordinates
    private Anchor anchor;

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

    /// Constructs a widget that contains the given component and is anchored to the given point on the screen.
    /// @param component the component that is contained in the widget
    /// @param anchorX the x-coordinate of the anchor point on the screen
    /// @param anchorY the y-coordinate of the anchor point on the screen
    /// @param anchor the point on the widget anchored to the coordinates
    /// @throws NullPointerException if the input `component` is null
    public Widget(Component component, int anchorX, int anchorY, Anchor anchor) {
        Objects.requireNonNull(component, "Contained component cannot be null.");

        // Set the widget containing the component to this
        component.setWidget(this);

        // Set the contained component
        this.component = component;

        // Set the position and anchor
        this.setPosition(anchorX, anchorY, anchor);
    }

    /// Updates the x-coordinate of this widget to stay anchored to the anchor point.
    void updateX() {
        // Set the x-coordinate based on the anchor point
        this.x = this.anchor.getX(this.anchorX, this.component.width);
    }

    /// Updates the y-coordinate of this widget to stay anchored to the anchor point.
    void updateY() {
        // Set the y-coordinate based on the anchor point
        this.y = this.anchor.getY(this.anchorY, this.component.height);
    }

    /// Renders the component contained in this widget and forwards the render call to each child widget
    /// if this widget is visible.
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

    /// Updates the component contained in this widget and forwards the update call to each child widget
    /// if this widget is visible.
    public void update() {
        // If this widget is not visible, return
        if (!this.visible) return;

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

    /// Returns the x-coordinate of the given anchor point on this widget.
    /// @param anchor the anchor point
    /// @return the x-coordinate of the given anchor point on this widget
    public int getX(Anchor anchor) {
        return anchor.getAnchorX(this.x, this.component.width);
    }

    /// Returns the y-coordinate of the given anchor point on this widget.
    /// @param anchor the anchor point
    /// @return the y-coordinate of the given anchor point on this widget
    public int getY(Anchor anchor) {
        return anchor.getAnchorY(this.y, this.component.height);
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
    /// Sets the x-coordinate of the anchor point of this widget.
    /// @param anchorX the x-coordinate of the anchor point
    /// @return this
    public Widget setX(int anchorX) {
        // Set the x-coordinate of the anchor point
        this.anchorX = anchorX;

        // Update the x-coordinate of this widget
        this.updateX();

        // Return this
        return this;
    }

    /// Sets the y-coordinate of the anchor point of this widget.
    /// @param anchorY the y-coordinate of the anchor point
    /// @return this
    public Widget setY(int anchorY) {
        // Set the y-coordinate of the anchor point
        this.anchorY = anchorY;

        // Update the y-coordinate of this widget
        this.updateY();

        // Return this
        return this;
    }

    /// Sets the position of the anchor point of this widget.
    /// @param anchorX the x-coordinate of the anchor point
    /// @param anchorY the y-coordinate of the anchor point
    /// @return this
    public Widget setPosition(int anchorX, int anchorY) {
        // Set the x and y coordinates
        this.setX(anchorX);
        this.setY(anchorY);

        // Return this
        return this;
    }

    /// Sets the position of this widget.
    /// @param anchorX the x-coordinate of the anchor point
    /// @param anchorY the y-coordinate of the anchor point
    /// @param anchor the point on the widget anchored to the coordinates
    /// @return this
    public Widget setPosition(int anchorX, int anchorY, Anchor anchor) {
        // Set the anchor point
        this.anchor = anchor;

        // Set the position of the anchor point
        return this.setPosition(anchorX, anchorY);
    }

    /// Sets the visibility of this widget.
    /// @param visible whether this widget should be visible
    /// @return this
    public Widget setVisible(boolean visible) {
        // Set visibility
        this.visible = visible;

        // Return this
        return this;
    }

    /// Toggles the visibility of this widget.
    /// @return this
    public Widget toggleVisibility() {
        // Toggle visibility
        return this.setVisible(!this.visible);
    }

    /// Shows this widget.
    /// @return this
    public Widget show() {
        // Set visibility to true
        return this.setVisible(true);
    }

    /// Hides this widget.
    /// @return this
    public Widget hide() {
        // Set visibility to false
        return this.setVisible(false);
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
    /// Forwards the mouse click event to the contained component and its child widgets if this widget is visible.
    /// Localizes the input coordinates.
    /// @param x the x-coordinate of the click
    /// @param y the y-coordinate of the click
    /// @param mouseButton the mouse button that is clicked
    /// @return whether the mouse click event is consumed
    boolean onMouseClicked(int x, int y, MouseButton mouseButton) {
        // If this widget is not visible, return
        if (!this.visible) return false;

        // Convert the input coordinates to local coordinates
        int xLocal = x - this.x - this.component.offsetX;
        int yLocal = y - this.y - this.component.offsetY;

        // Forward the mouse click event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            if (this.component.childWidgets.get(i).onMouseClicked(xLocal, yLocal, mouseButton)) return true;
        }

        // Forward the mouse click event to the contained component
        return this.component.mouseClickEvent(xLocal, yLocal, mouseButton);
    }

    /// Forwards the mouse press event to the contained component and its child widgets if this widget is visible.
    /// Localizes the input coordinates.
    /// @param x the x-coordinate of the press
    /// @param y the y-coordinate of the press
    /// @param mouseButton the mouse button that is pressed
    void onMousePressed(int x, int y, MouseButton mouseButton) {
        // If this widget is not visible, return
        if (!this.visible) return;

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

    /// Forwards the mouse release event to the contained component and its child widgets if this widget is visible.
    /// @param mouseButton the mouse button that is released
    void onMouseReleased(MouseButton mouseButton) {
        // If this widget is not visible, return
        if (!this.visible) return;

        // Forward the mouse release event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onMouseReleased(mouseButton);
        }

        // Forward the mouse release event to the contained component
        this.component.mouseReleaseEvent(mouseButton);
    }

    /// Forwards the mouse move event to the contained component and its child widgets if this widget is visible.
    /// Localizes the input coordinates.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    void onMouseMoved(int x, int y) {
        // If this widget is not visible, return
        if (!this.visible) return;

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

    /// Forwards the mouse drag event to the contained component and its child widgets if this widget is visible.
    /// Localizes the input coordinates.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    /// @param mouseButton the mouse button that is dragged
    void onMouseDragged(int x, int y, MouseButton mouseButton) {
        // If this widget is not visible, return
        if (!this.visible) return;

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

    /// Forwards the mouse wheel move event to the contained component and its child widgets if this widget is visible.
    /// @param wheelRotation the mouse wheel rotation (1 for down, -1 for up, 0 for partial rotation)
    /// @param preciseWheelRotation the precise mouse wheel rotation (positive values for down, negative values for up)
    void onMouseWheelMoved(int wheelRotation, double preciseWheelRotation) {
        // If this widget is not visible, return
        if (!this.visible) return;

        // Forward the mouse wheel move event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onMouseWheelMoved(wheelRotation, preciseWheelRotation);
        }

        // Forward the mouse wheel move event to the contained component
        this.component.mouseWheelEvent(wheelRotation, preciseWheelRotation);
    }

    /// Forwards the key typing event to the contained component and its child widgets if this widget is visible.
    /// @param keyChar the typed character
    /// @return whether the key typing event is consumed
    boolean onKeyTyped(char keyChar) {
        // If this widget is not visible, return
        if (!this.visible) return false;

        // Forward the key typing event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            if (this.component.childWidgets.get(i).onKeyTyped(keyChar)) return true;
        }

        // Forward the key typing event to the contained component
        return this.component.keyTypingEvent(keyChar);
    }

    /// Forwards the key pressing event to the contained component and its child widgets if this widget is visible.
    /// @param keyCode the key code of the pressed key
    void onKeyPressed(int keyCode) {
        // If this widget is not visible, return
        if (!this.visible) return;

        // Forward the key press event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onKeyPressed(keyCode);
        }

        // Forward the key press event to the contained component
        this.component.keyPressEvent(keyCode);
    }

    /// Forwards the key releasing event to the contained component and its child widgets if this widget is visible.
    /// @param keyCode the key code of the released key
    void onKeyReleased(int keyCode) {
        // If this widget is not visible, return
        if (!this.visible) return;

        // Forward the key release event to each child widget
        for (int i = this.component.childWidgets.size() - 1; i >= 0; i--) {
            this.component.childWidgets.get(i).onKeyReleased(keyCode);
        }

        // Forward the key release event to the contained component
        this.component.keyReleaseEvent(keyCode);
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