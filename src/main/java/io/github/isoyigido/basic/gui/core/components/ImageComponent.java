package io.github.isoyigido.basic.gui.core.components;

import io.github.isoyigido.basic.gui.core.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/// Represents a simple image display.
/// Provides methods for setting the displayed image and its dimensions.
/// Provides factory methods for reading images from the resources folder.
/// @see Component
/// @see BufferedImage
/// @apiNote Use {@link #setDimensions(int, int)} to resize the displayed image.
///          The methods {@link Component#setWidth(int)} and {@link Component#setHeight(int)}
///          do not change the dimensions of the displayed image, but only that of the component.
public final class ImageComponent extends Component {
    private static final Logger logger = LoggerFactory.getLogger(ImageComponent.class);

    /// The displayed image
    private BufferedImage image;

    /// Constructs an image component which displays the given image.
    /// @param image the displayed image
    /// @throws NullPointerException if the input `image` is null
    public ImageComponent(BufferedImage image) {
        Objects.requireNonNull(image, "The displayed image cannot be null.");

        // Set the image
        this.setImage(image);
    }

    /// Constructs an image component which displays the given image, resized to the given dimensions.
    /// @param image the displayed image
    /// @param width the width of the displayed image
    /// @param height the height of the displayed image
    /// @throws NullPointerException if the input `image` is null
    public ImageComponent(BufferedImage image, int width, int height) {
        // Resize the image and construct the image component
        this(
                ImageComponent.resizeImage(
                        Objects.requireNonNull(image, "The displayed image cannot be null."),
                        width,
                        height
                )
        );
    }

    /// Renders the image.
    @Override
    public void render(Graphics2D g) {
        // Draw the image
        g.drawImage(this.image, 0, 0, null);
    }

    /// @apiNote Do not use this method to resize the displayed image.
    ///          Use {@link #setDimensions(int, int)} instead.
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    /// @apiNote Do not use this method to resize the displayed image.
    ///          Use {@link #setDimensions(int, int)} instead.
    @Override
    public void setHeight(int height) {
        super.setHeight(height);
    }

    // --- FACTORY METHODS ---
    /// Reads the image file at the given path in the resources folder and returns an image component displaying it.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if there is no image file at the given path, or if an {@link IOException} is caught while reading the image file
    ///
    /// @param path the path to the image file in the resources folder (e.g., `/folder/file.png`)
    /// @return an {@link Optional} containing the image component,
    ///         or an empty {@link Optional} if there is no image file at the given path, or if an {@link IOException} is caught while reading the image file
    /// @throws NullPointerException if the input `path` is null
    public static Optional<ImageComponent> fromResources(String path) {
        Objects.requireNonNull(path, "Path to image file cannot be null.");

        // Read the image file at the given path and map it to a new image component
        return readImageFromResources(path).map(ImageComponent::new);
    }

    /// Reads the image file at the given path in the resources folder and returns an image component displaying it, resized to the given dimensions.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if there is no image file at the given path, or if an {@link IOException} is caught while reading the image file
    ///
    /// @param path the path to the image file in the resources folder (e.g., `/folder/file.png`)
    /// @param width the width of the displayed image
    /// @param height the height of the displayed image
    /// @return an {@link Optional} containing the resized image component,
    ///         or an empty {@link Optional} if there is no image file at the given path, or if an {@link IOException} is caught while reading the image file
    /// @throws NullPointerException if the input `path` is null
    public static Optional<ImageComponent> fromResources(String path, int width, int height) {
        Objects.requireNonNull(path, "Path to image file cannot be null.");

        // Read the image file at the given path and map it to a resized image component
        return readImageFromResources(path)
                .map(img -> new ImageComponent(img, width, height));
    }

    // --- GETTERS ---
    /// Returns the displayed image.
    /// @return the displayed image
    public BufferedImage getImage() {
        // Return the displayed image
        return this.image;
    }

    // --- SETTERS ---
    /// Sets the displayed image.
    /// @param image the displayed image
    /// @return this
    /// @throws NullPointerException if the input `image` is null
    public ImageComponent setImage(BufferedImage image) {
        Objects.requireNonNull(image, "The displayed image cannot be null.");

        // Set the image
        this.image = image;

        // Update dimensions
        this.setWidth(image.getWidth());
        this.setHeight(image.getHeight());

        // Return this
        return this;
    }

    /// Sets the dimensions of the displayed image.
    /// @param width the new width of the displayed image
    /// @param height the new height of the displayed image
    /// @return this
    /// @apiNote This method updates the dimensions of this component as well.
    public ImageComponent setDimensions(int width, int height) {
        // Resize and set the image
        this.setImage(ImageComponent.resizeImage(this.image, width, height));

        // Return this
        return this;
    }

    // --- STATIC HELPERS ---
    /// Reads the image file at the given path in the resources folder and returns the image.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if there is no image file at the given path, or if an {@link IOException} is caught while reading the image file
    ///
    /// @param path the path to the image file in the resources folder (e.g., `/folder/file.png`)
    /// @return an {@link Optional} containing the {@link BufferedImage} object,
    ///         or an empty {@link Optional} if there is no image file at the given path, or if an {@link IOException} is caught while reading the image file
    /// @throws NullPointerException if the input `path` is null
    private static Optional<BufferedImage> readImageFromResources(String path) {
        Objects.requireNonNull(path, "Path to image file cannot be null.");

        // If the path is empty, throw an illegal argument exception
        if (path.isEmpty()) throw new IllegalArgumentException("Path to image file cannot be empty.");

        // If the path does not have a leading slash, add it
        if (path.charAt(0) != '/') path = '/' + path;

        // Try to read the image file from the given path
        try (InputStream is = ImageComponent.class.getResourceAsStream(path)) {
            // If the input stream is null
            if (is == null) {
                // Log warning
                logger.warn("Unable to find image. path={}", path);

                // Return empty optional
                return Optional.empty();
            }

            // Read the image from the input stream
            return Optional.of(ImageIO.read(is));

        } catch (IOException e) {
            // Log error
            logger.error("Unable to read image. path={}", path, e);

            // Return empty optional
            return Optional.empty();
        }
    }

    /// Resizes the given image to the given dimensions.
    /// @param image the image to resize
    /// @param width the new width of the image
    /// @param height the new height of the image
    /// @return the resized image
    /// @throws NullPointerException if the input `image` is null
    /// @implNote This method uses bicubic interpolation, quality rendering, and antialiasing when resizing the given image.
    private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        Objects.requireNonNull(image, "Image to resize cannot be null.");

        // Fallback to TYPE_INT_ARGB to avoid issues with specialized source types
        int originalImageType = image.getType();
        int imageType = (originalImageType == BufferedImage.TYPE_CUSTOM)
                ? BufferedImage.TYPE_INT_ARGB
                : originalImageType;

        // Create the new image and its graphics
        BufferedImage newImage = new BufferedImage(width, height, imageType);
        Graphics2D g = newImage.createGraphics();

        // - Set rendering configuration -
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the image with the given dimensions
        g.drawImage(image, 0, 0, width, height, null);

        // Dispose of the graphics
        g.dispose();

        // Return the resized image
        return newImage;
    }
}