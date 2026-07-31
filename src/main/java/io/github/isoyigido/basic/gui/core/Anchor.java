package io.github.isoyigido.basic.gui.core;

/// Represents anchor points on a widget.
public class Anchor {
    /// The center point
    public static final Anchor CENTER = new Anchor(0.5f, 0.5f);

    /// The middle-left point
    public static final Anchor LEFT = new Anchor(0.0f, 0.5f);

    /// The middle-right point
    public static final Anchor RIGHT = new Anchor(1.0f, 0.5f);

    /// The top-center point
    public static final Anchor TOP = new Anchor(0.5f, 0.0f);

    /// The bottom-center point
    public static final Anchor BOTTOM = new Anchor(0.5f, 1.0f);

    /// The top-left corner
    public static final Anchor TOP_LEFT = new Anchor(0.0f, 0.0f);

    /// The top-right corner
    public static final Anchor TOP_RIGHT = new Anchor(1.0f, 0.0f);

    /// The bottom-left corner
    public static final Anchor BOTTOM_LEFT = new Anchor(0.0f, 1.0f);

    /// The bottom-right corner
    public static final Anchor BOTTOM_RIGHT = new Anchor(1.0f, 1.0f);


    /// The offset factor on the x-axis
    private final float xOffset;

    /// The offset factor on the y-axis
    private final float yOffset;

    /// Constructs an anchor point that is offset by the given factors relative to the top-left corner.
    /// @param xOffset the offset factor on the x-axis
    /// @param yOffset the offset factor on the y-axis
    public Anchor(float xOffset, float yOffset) {
        // Set the offset factors
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    /// Calculates and returns the x-coordinate of this anchor point.
    /// @param x the leftmost x-coordinate of the widget
    /// @param width the width of the component
    /// @return the x-coordinate of this anchor point
    public int getAnchorX(int x, int width) {
        return x + Math.round(width * this.xOffset);
    }

    /// Calculates and returns the y-coordinate of this anchor point.
    /// @param y the topmost y-coordinate of the widget
    /// @param height the height of the component
    /// @return the y-coordinate of this anchor point
    public int getAnchorY(int y, int height) {
        return y + Math.round(height * this.yOffset);
    }

    /// Calculates and returns the leftmost x-coordinate of the widget.
    /// @param anchorX the x-coordinate of this anchor point
    /// @param width the width of the component
    /// @return the leftmost x-coordinate of the widget
    public int getX(int anchorX, int width) {
        return anchorX - Math.round(width * this.xOffset);
    }

    /// Calculates and returns the topmost y-coordinate of the widget.
    /// @param anchorY the y-coordinate of this anchor point
    /// @param height the height of the component
    /// @return the topmost y-coordinate of the widget
    public int getY(int anchorY, int height) {
        return anchorY - Math.round(height * this.yOffset);
    }
}