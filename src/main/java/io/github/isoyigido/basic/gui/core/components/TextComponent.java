package io.github.isoyigido.basic.gui.core.components;

import io.github.isoyigido.basic.gui.core.Component;

import java.awt.*;
import java.util.Objects;

/// Represents a simple text display.
/// Provides methods for setting the displayed text, the color
/// of the displayed text, and the font of the displayed text.
public class TextComponent extends Component {
    /// The displayed text
    private String text;

    /// The color of the displayed text
    private Color color;

    /// The font of the displayed text
    private Font font;

    /// The font metrics for the font of the displayed text
    private FontMetrics fontMetrics;

    /// The y-coordinate of the displayed text
    private int textY;

    /// Constructs a text component.
    /// @param text the displayed text
    /// @param color the color of the displayed text
    /// @param font the font of the displayed text
    /// @throws NullPointerException if the input `text`, `color`, or `font` is null
    public TextComponent(String text, Color color, Font font) {
        // - Check for null parameters -
        Objects.requireNonNull(text, "Text cannot be null.");
        Objects.requireNonNull(text, "Text color cannot be null.");
        Objects.requireNonNull(font, "Text font cannot be null.");

        // Set the text
        this.text = text;

        // Set the color
        this.color = color;

        // Set the font
        this.setFont(font);
    }

    /// Renders the text.
    @Override
    public void render(Graphics2D g) {
        // Set the color
        g.setColor(this.color);

        // Set the font
        g.setFont(this.font);

        // Draw the text
        g.drawString(this.text, 0, this.textY);
    }

    /// Updates the width of this text component.
    private void updateWidth() {
        // Adjust the width based on the width of text
        this.setWidth(this.fontMetrics.stringWidth(this.text));
    }

    // --- SETTERS ---
    /// Sets the displayed text to the given text.
    /// @param text the new displayed text
    /// @return this
    /// @throws NullPointerException if the input `text` is null
    public TextComponent setText(String text) {
        Objects.requireNonNull(text, "Text cannot be null.");

        // Set the text
        this.text = text;

        // Update text width of the text
        this.updateWidth();

        // Return this
        return this;
    }

    /// Sets the color of the displayed text to the given color.
    /// @param color the new color of the displayed text
    /// @return this
    /// @throws NullPointerException if the input `color` is null
    public TextComponent setColor(Color color) {
        Objects.requireNonNull(color, "Text color cannot be null");

        // Set the color
        this.color = color;

        // Return this
        return this;
    }

    /// Sets the font of the displayed text to the given font.
    /// @param font the new font of the displayed text
    /// @return this
    /// @throws NullPointerException if the input `font` is null
    public TextComponent setFont(Font font) {
        Objects.requireNonNull(font, "Text font cannot be null.");

        // Set the font
        this.font = font;

        // Set the font metrics for the font
        this.fontMetrics = new Canvas().getFontMetrics(font);

        // Update the width of the text
        this.updateWidth();

        // Set the height of the text
        this.setHeight(this.fontMetrics.getHeight());

        // Calculate the y coordinate of the text
        this.textY = this.fontMetrics.getAscent();

        // Return this
        return this;
    }

    // --- GETTERS ---
    /// Returns the displayed text.
    /// @return the displayed text
    public String getText() {
        return this.text;
    }

    /// Returns the color of the displayed text.
    /// @return the color of the displayed text
    public Color getColor() {
        return this.color;
    }

    /// Returns the font of the displayed text.
    /// @return the font of the displayed text
    public Font getFont() {
        return this.font;
    }

    /// Returns the font metrics for the font of the displayed text.
    /// @return the font metrics for the font of the displayed text
    public FontMetrics getFontMetrics() {
        return this.fontMetrics;
    }
}