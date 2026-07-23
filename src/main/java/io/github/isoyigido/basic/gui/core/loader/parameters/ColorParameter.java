package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Optional;

/// Overrides the {@link #parse(String)} method to parse a hexadecimal color value or individual RGB values.
///
/// @see #parse(String)
/// @see #parseHex(String)
/// @see #parseRGB(String)
/// @see Parameter
/// @see Color
public class ColorParameter extends Parameter<Color> {
    private static final Logger logger = LoggerFactory.getLogger(ColorParameter.class);

    /// Parses the given {@link String} representation of a color.
    /// If the {@link String} representation consists of individual RGB(A) values, uses {@link #parseRGB(String)}.
    /// If the {@link String} representation represents a hexadecimal value, uses {@link #parseHex(String)}.
    ///
    /// **Valid RGB(A) formats:**
    /// - `(R, G, B, A)`
    /// - `(R G B A)`
    /// - `R, G, B, A`
    /// - `R G B A`
    ///
    /// *Note: The alpha channel (A) is optional, and should be included after the RGB values if present.
    ///        All RGB(A) values must be integers in the range 0-255.*
    ///
    /// **Valid hexadecimal formats:**
    /// - `#RRGGBB(AA)`
    /// - `RRGGBB(AA)`
    /// - `0xRRGGBB(AA)`
    /// - `0XRRGGBB(AA)`
    ///
    /// *Note: The alpha channel (AA) is optional, and should be included after the RGB bytes if present.
    ///        Both uppercase and lowercase hexadecimal letters (A-F and a-f) are accepted.*
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the {@link String} representation is invalid
    ///   or has out-of-bounds RGB(A) values
    ///
    /// @param valueString the {@link String} representation of the color
    /// @return an {@link Optional} containing the parsed {@link Color},
    ///         or an empty {@link Optional} if the {@link String} representation is invalid
    ///         or has out-of-bounds RGB(A) values
    /// @see #parseRGB(String)
    /// @see #parseHex(String)
    @Override
    public Optional<Color> parse(String valueString) {
        // Remove surrounding whitespace
        valueString = valueString.strip();

        // If the value string contains a comma or whitespace character, parse individual RGB(A) values
        if (valueString.matches(".*[\\s,].*")) return ColorParameter.parseRGB(valueString);

        // Parse hexadecimal color value
        return ColorParameter.parseHex(valueString);
    }

    /// Parses the given {@link String} representation of individual RGB(A) values.
    ///
    /// **Valid formats:**
    /// - `(R, G, B, A)`
    /// - `(R G B A)`
    /// - `R, G, B, A`
    /// - `R G B A`
    ///
    /// *Note: The alpha channel (A) is optional, and should be included after the RGB values if present.
    ///        All RGB(A) values must be integers in the range 0-255.*
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the {@link String} representation of individual RGB(A) values is invalid,
    ///   or if the RGB(A) values are outside the range 0-255
    ///
    /// @param valueString the {@link String} representation of the individual RGB(A) values
    /// @return an {@link Optional} containing the parsed {@link Color},
    ///         or an empty {@link Optional} if the {@link String} representation of individual RGB(A) values is invalid,
    ///         or if the RGB(A) values are outside the range 0-255
    private static Optional<Color> parseRGB(String valueString) {
        // Remove surrounding whitespace
        valueString = valueString.strip();

        // If there is a pair of surrounding parentheses, remove them
        if (valueString.startsWith("(") && valueString.endsWith(")")) {
            valueString = valueString.substring(1, valueString.length() - 1).strip();
        }

        // Split the value string by commas or whitespace
        String[] args = valueString.contains(",")
                ? valueString.split(",")
                : valueString.split("\\s+");

        // If there are 4 arguments, there is an alpha channel alongside the RGB channels
        boolean hasAlpha = args.length == 4;

        // If the number of arguments is not 3 or 4
        if (!hasAlpha && (args.length != 3)) {
            // Log warning
            ColorParameter.logger.warn("Color parameter has invalid number of color channels. value={} channels={}", valueString, args.length);

            // Return empty optional
            return Optional.empty();
        }

        try {
            // Initialize the array of integer RGB(A) values
            int[] values = new int[args.length];

            // Parse each argument to get the integer RGB(A) values
            for (int i = 0; i < args.length; i++) {
                values[i] = Integer.parseInt(args[i].strip());
            }

            // Return a new color with the parsed RGB(A) values
            return Optional.of(
                    hasAlpha
                            ? new Color(values[0], values[1], values[2], values[3])
                            : new Color(values[0], values[1], values[2])
            );

        } catch (NumberFormatException _) {
            // Log warning
            ColorParameter.logger.warn("Color parameter has invalid RGB(A) values. value={}", valueString);

            // Return empty optional
            return Optional.empty();

        } catch (IllegalArgumentException _) {
            // Log warning
            ColorParameter.logger.warn("Color parameter has out-of-bounds RGB(A) values. value={}", valueString);

            // Return empty optional
            return Optional.empty();
        }
    }

    /// Parses the given {@link String} representation of a hexadecimal color value.
    ///
    /// **Valid formats:**
    /// - `#RRGGBB(AA)`
    /// - `RRGGBB(AA)`
    /// - `0xRRGGBB(AA)`
    /// - `0XRRGGBB(AA)`
    ///
    /// *Note: The alpha channel (AA) is optional, and should be included after the RGB bytes if present.
    ///        Both uppercase and lowercase hexadecimal letters (A-F and a-f) are accepted.*
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the {@link String} representation of the hexadecimal value is invalid
    ///
    /// @param valueString the {@link String} representation of the hexadecimal color value
    /// @return an {@link Optional} containing the parsed {@link Color},
    ///         or an empty {@link Optional} if the {@link String} representation of the hexadecimal value is invalid
    public static Optional<Color> parseHex(String valueString) {
        try {
            // Remove surrounding whitespace and convert to lowercase
            valueString = valueString.strip().toLowerCase();

            // If the hexadecimal string has the prefix # or 0x, remove them
            if (valueString.startsWith("#")) valueString = valueString.substring(1);
            if (valueString.startsWith("0x")) valueString = valueString.substring(2);

            // Parse the hexadecimal string to get the unsigned integer color value
            int value = Integer.parseUnsignedInt(valueString, 16);

            // #RRGGBB -> return a fully opaque color with the parsed RGB value
            if (valueString.length() == 6) return Optional.of(new Color(value, false));

            // #RRGGBBAA -> move AA from the end to the front
            if (valueString.length() == 8) {
                // RRGGBBAA -> (00RRGGBB | AA000000) -> AARRGGBB
                int argb = (value >>> 8) | (value << 24);

                // Return a new color with an alpha channel
                return Optional.of(new Color(argb, true));
            }

            // - Invalid length -
            // Log warning
            ColorParameter.logger.warn("Color parameter has hexadecimal value of invalid length. value={}", valueString);

            // Return empty optional
            return Optional.empty();

        } catch (NumberFormatException _) {
            // Log warning
            ColorParameter.logger.warn("Color parameter has invalid hexadecimal value. value={}", valueString);

            // Return empty optional
            return Optional.empty();
        }
    }
}