package io.github.isoyigido.basic.gui.constants;

import io.github.isoyigido.basic.gui.main.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/// Stores constant cursor types.
/// @see Cursor
public final class Cursors {
    /// Private constructor to prevent instantiation
    private Cursors() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated.");
    }

    /// Default cursor
    public static final Cursor DEFAULT = Cursor.getDefaultCursor();

    /// Invisible cursor
    public static final Cursor INVISIBLE = Toolkit.getDefaultToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new Point(0, 0),
            "invisibleCursor"
    );

    /// Hand cursor
    public static final Cursor HAND = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    /// Text cursor
    public static final Cursor TEXT = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);

    /// Crosshair cursor
    public static final Cursor CROSSHAIR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    /// Horizontal resize cursor
    public static final Cursor HORIZONTAL_RESIZE = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);

    /// Returns a new custom cursor with the given parameters.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if an {@link IOException} is caught
    ///
    /// @param name the name of the cursor
    /// @param path the path to the cursor image relative to the resources folder
    /// @param hotspotX the x position of the hotspot on the cursor image (`0f` is leftmost, `1f` is rightmost)
    /// @param hotspotY the y position of the hotspot on the cursor image (`0f` is topmost, `1f` is bottommost)
    /// @return an {@link Optional} containing the custom cursor,
    ///         or an empty {@link Optional} if an {@link IOException} is caught
    /// @throws NullPointerException if the input `name` or `path` is null
    public static Optional<Cursor> getCustomCursor(String name, String path, float hotspotX, float hotspotY) {
        Objects.requireNonNull(name, "Name of the custom cursor cannot be null.");
        Objects.requireNonNull(path, "Path to the cursor image file cannot be null.");

        try {
            // Get the default toolkit
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            // Load the cursor image from the input path
            URL cursorImageURL = Main.class.getResource(path);

            // If the cursor image cannot be found
            if (cursorImageURL == null) {
                // Return empty optional
                return Optional.empty();
            }

            // Read the cursor image
            BufferedImage cursorImage = ImageIO.read(cursorImageURL);

            // Get the cursor size
            Dimension cursorSize = toolkit.getBestCursorSize(cursorImage.getWidth(), cursorImage.getHeight());

            // Create and return the custom cursor
            return Optional.of(toolkit.createCustomCursor(
                    cursorImage,
                    new Point(
                            Math.round(hotspotX * cursorSize.width),
                            Math.round(hotspotY * cursorSize.height)
                    ),
                    name
            ));

        } catch (IOException e) {
            // Return empty optional
            return Optional.empty();
        }
    }
}