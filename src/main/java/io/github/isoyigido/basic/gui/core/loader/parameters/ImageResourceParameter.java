package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/// Overrides the {@link #parse(String)} method to read the image at the given path
/// relative to the resources folder.
///
/// @see #parse(String)
/// @see Parameter
/// @see BufferedImage
public class ImageResourceParameter extends Parameter<BufferedImage> {
    private static final Logger logger = LoggerFactory.getLogger(ImageResourceParameter.class);

    /// Reads the image at the given path relative to the resources folder.
    ///
    /// **Parameter format:** `/folder/file.png` or `"/folder/file.png"`
    ///
    /// **Recognized image formats:** <code>JPG, JPEG, PNG, BMP, WBMP, TIF, TIFF, <i>GIF*</i></code>
    ///
    /// <i>* GIF files can technically be read but only the first frame of the GIF is actually stored.</i>
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if there is no file at the given path in resources,
    /// or if an {@link IOException} is caught
    ///
    /// @param valueString the path to the image file relative to the resources folder
    /// @return an {@link Optional} containing the read image as a {@link BufferedImage} object,
    ///         or an empty {@link Optional} if there is no file at the given path in resources,
    ///         or if an {@link IOException} is caught
    /// @see ImageIO#read(InputStream)
    @Override
    public Optional<BufferedImage> parse(String valueString) {
        // Strip the pair of surrounding quotation marks (if present)
        String path = StringParameter.stripQuotationMarks(valueString);

        // If the path does not have a leading slash, add it
        if (path.charAt(0) != '/') path = '/' + path;

        // Get the image resource as an input stream
        try (InputStream is = ImageResourceParameter.class.getResourceAsStream(path)) {
            // If there is no file at the given path in resources
            if (is == null) {
                // Log warning
                ImageResourceParameter.logger.warn("Image resource cannot be found. path={}", path);

                // Return empty optional
                return Optional.empty();
            }

            // Read and return the image from the input stream
            return Optional.ofNullable(ImageIO.read(is));

        } catch (IOException e) {
            // Log error
            ImageResourceParameter.logger.error("Encountered an error while reading image resource. path={}", path, e);

            // Return empty optional
            return Optional.empty();
        }
    }
}