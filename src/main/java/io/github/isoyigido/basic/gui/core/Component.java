package io.github.isoyigido.basic.gui.core;

import io.github.isoyigido.basic.gui.inputs.MouseButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

/// Represents a base GUI component within the GUI framework.
/// Provides rendering, update, and input event methods to be overridden.
/// Provides an {@link #addWidget(Widget)} method to add child widgets.
/// @apiNote `Component` instances have to be contained in a {@link Widget} object in order to be displayed,
///           as this class does not hold the location of components.
/// @see Widget
/// @see GUI
public abstract class Component {
    /// The width of the component (in pixels)
    int width;

    /// The height of the component (in pixels)
    int height;

    /// The bounding box of the component
    final Rectangle boundingBox;

    /// Whether the rendering context is clipped to the bounding box when rendering this component
    final boolean clipped;

    /// The rendering offset for this component on the x-axis
    int offsetX = 0;

    /// The rendering offset for this component on the y-axis
    int offsetY = 0;

    /// The widget that holds this component
    private Widget widget = null;

    /// The list of child widgets of this component
    final List<Widget> childWidgets = new ArrayList<>(4);

    /// Constructs a component with the given width and height.
    /// @param width the width of the component (in pixels)
    /// @param height the height of the component (in pixels)
    /// @param clipped whether the rendering context is clipped to the bounding box when rendering the component
    public Component(int width, int height, boolean clipped) {
        // Set the width and height
        this.width = width;
        this.height = height;
        this.boundingBox = new Rectangle();

        // Update the bounding box
        this.updateBoundingBox();

        // Set whether to clip the rendering context
        this.clipped = clipped;
    }

    /// Renders this component onto the given graphics context.
    ///
    /// This method is called every frame.
    ///
    /// Override this method to provide the rendering logic.
    ///
    /// @param g the graphics context to render on
    public void render(Graphics2D g) {}

    /// Updates the internal state of this component.
    ///
    /// This method is called every update.
    public void update() {}

    /// Adds the given widget as a child widget of this component.
    /// @param widget the widget to be added as a child to this component
    public void addWidget(Widget widget) {
        // Add the widget to the list of child widgets
        this.childWidgets.add(widget);
    }

    /// Returns whether the given coordinates lie within the boundaries of this component.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return whether the given coordinates lie within the boundaries of this component
    public boolean contains(int x, int y) {
        return     (x >= 0) && (x < this.width)   //     x-coordinate is within bounds
                && (y >= 0) && (y < this.height); // and y-coordinate is within bounds
    }

    // --- GETTERS ---
    /// Returns the width of this component.
    /// @return the width of this component (in pixels)
    public int getWidth() {
        return this.width;
    }

    /// Returns the height of this component.
    /// @return the height of this component (in pixels)
    public int getHeight() {
        return this.height;
    }

    /// Returns whether the rendering context is clipped to the bounding box when rendering this component.
    /// @return whether the rendering context is clipped to the bounding box when rendering this component
    public boolean isClipped() {
        return this.clipped;
    }

    /// Returns the rendering offset for this component on the x-axis.
    /// @return the rendering offset for this component on the x-axis
    public int getOffsetX() {
        return this.offsetX;
    }

    /// Returns the rendering offset for this component on the y-axis.
    /// @return the rendering offset for this component on the y-axis
    public int getOffsetY() {
        return this.offsetY;
    }

    /// Returns the widget that contains this component.
    /// @return the widget that contains this component
    public Widget getWidget() {
        return this.widget;
    }

    /// Returns the list of child widgets of this component.
    /// @return the list of child widgets of this component
    public List<Widget> getChildWidgets() {
        return this.childWidgets;
    }

    // --- SETTERS ---
    /// Sets the width of this component to the given value and updates the bounding box.
    ///
    /// If this component is contained in a widget, the position of the widget is updated as well.
    ///
    /// @param width the new width of this component (in pixels)
    public void setWidth(int width) {
        // Set the width of this component
        this.width = width;

        // Update the bounding box
        this.updateBoundingBox();

        // If this component is contained in a widget, update its position
        if (this.widget != null) this.widget.updateX();
    }


    /// Sets the height of this component to the given value and updates the bounding box.
    ///
    /// If this component is contained in a widget, the position of the widget is updated as well.
    ///
    /// @param height the new height of this component (in pixels)
    public void setHeight(int height) {
        // Set the height of this component
        this.height = height;

        // Update the bounding box
        this.updateBoundingBox();

        // If this component is contained in a widget, update its position
        if (this.widget != null) this.widget.updateY();
    }

    /// Updates the bounding box of this component.
    private void updateBoundingBox() {
        // Set the width and height of the bounding box
        this.boundingBox.setSize(this.width, this.height);
    }

    /// Sets the rendering offset for this component on the x-axis.
    /// @param offsetX the new rendering offset on the x-axis
    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    /// Sets the rendering offset for this component on the y-axis.
    /// @param offsetY the new rendering offset on the y-axis
    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    /// Sets the widget that contains this component.
    /// @param widget the widget that contains this component
    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    // --- INPUT EVENT METHODS ---
    /// Override this method to handle a mouse click event.
    /// @param x the x-coordinate of the click
    /// @param y the y-coordinate of the click
    /// @param mouseButton the mouse button that is clicked
    public void mouseClickEvent(int x, int y, MouseButton mouseButton) {}

    /// Override this method to handle a mouse press event.
    /// @param x the x-coordinate of the press
    /// @param y the y-coordinate of the press
    /// @param mouseButton the mouse button that is pressed
    public void mousePressEvent(int x, int y, MouseButton mouseButton) {}

    /// Override this method to handle a mouse release event.
    /// @param mouseButton the mouse button that is released
    public void mouseReleaseEvent(MouseButton mouseButton) {}

    /// Override this method to handle a mouse move event.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    public void mouseMoveEvent(int x, int y) {}

    /// Override this method to handle a mouse drag event.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    /// @param mouseButton the mouse button that is dragged
    public void mouseDragEvent(int x, int y, MouseButton mouseButton) {}

    /// Override this method to handle a mouse wheel move event.
    /// @param e the mouse wheel event
    public void mouseWheelEvent(MouseWheelEvent e) {}

    /// Override this method to handle a key typing event.
    /// @param e the key event
    public void keyTypingEvent(KeyEvent e) {}

    /// Override this method to handle a key pressing event.
    /// @param e the key event
    public void keyPressEvent(KeyEvent e) {}

    /// Override this method to handle a key releasing event.
    /// @param e the key event
    public void keyReleaseEvent(KeyEvent e) {}

    // --- WIDGET FACTORY METHODS ---
    /// Returns a widget containing this component centered at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget center(int x, int y) {
        return Widget.center(this, x, y);
    }

    /// Returns a widget containing this component whose middle-left point lies at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget left(int x, int y) {
        return Widget.left(this, x, y);
    }

    /// Returns a widget containing this component whose middle-right point lies at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget right(int x, int y) {
        return Widget.right(this, x, y);
    }

    /// Returns a widget containing this component whose top-center point lies at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget top(int x, int y) {
        return Widget.top(this, x, y);
    }

    /// Returns a widget containing this component whose bottom-center point lies at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget bottom(int x, int y) {
        return Widget.bottom(this, x, y);
    }

    /// Returns a widget containing this component whose top-left corner lies at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget topLeft(int x, int y) {
        return Widget.topLeft(this, x, y);
    }

    /// Returns a widget containing this component whose top-right corner lies at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget topRight(int x, int y) {
        return Widget.topRight(this, x, y);
    }

    /// Returns a widget containing this component whose bottom-left corner lies at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget bottomLeft(int x, int y) {
        return Widget.bottomLeft(this, x, y);
    }

    /// Returns a widget containing this component whose bottom-right corner lies at the given coordinates.
    /// @param x the x-coordinate
    /// @param y the y-coordinate
    /// @return a new {@link Widget} object containing this component
    public Widget bottomRight(int x, int y) {
        return Widget.bottomRight(this, x, y);
    }

    /// Returns a widget containing this component centered at the center of the screen.
    /// @return a new {@link Widget} object containing this component
    public Widget center() {
        return Widget.center(this);
    }
}