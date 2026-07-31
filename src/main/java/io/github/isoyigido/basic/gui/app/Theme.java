package io.github.isoyigido.basic.gui.app;

import io.github.isoyigido.basic.gui.core.GUIManager;
import io.github.isoyigido.basic.gui.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/// Stores application appearance. Provides the method {@link #registerColorTheme(String, String...)} to register
/// color theme JSON files, and the method {@link #setColorTheme(String)} to set the color theme from the registered
/// color themes. The colors of set color themes can be accessed via {@link #getColor(String)}, which returns the
/// corresponding color for the given color key.
/// @see #registerColorTheme(String, String...)
/// @see #setColorTheme(String)
/// @see #getColor(String)
public final class Theme {
    /// Private constructor to prevent instantiation
    private Theme() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(Theme.class);

    // --- COLOR THEMES ---
    /// Maps each registered color theme name to the corresponding color map.
    private static final Map<String, Map<String, Color>> colorThemeRegistry = new HashMap<>(4);

    /// The currently set color theme (null indicates that no color theme is set)
    private static Map<String, Color> colorTheme = null;

    /// The placeholder color for missing color keys
    public static final Color PLACEHOLDER_COLOR = Color.BLACK;

    /// The color key for the background color
    public static final String BACKGROUND_COLOR_KEY = "background";

    /// Loads and flattens the color theme JSON files at the given resource directory. Registers the flattened
    /// color themes under the given color theme names.
    ///
    /// The names of the JSON files should match the given color theme names (e.g., `light.json`, `dark.json`).
    ///
    /// **Special cases:**
    /// - Logs a warning and skips the color theme if no corresponding color theme file is found, or if the color theme
    ///   file cannot be read
    ///
    /// @param directory the path to the directory that contains the color theme files relative to the resources folder (e.g., `/app/themes/`)
    /// @param colorThemeNames the names of the color themes to be registered (e.g., `light`, `dark`)
    /// @throws NullPointerException if the input `directory` or `colorThemeNames` is null
    public static void registerColorTheme(String directory, String... colorThemeNames) {
        Objects.requireNonNull(directory, "Path to color theme directory cannot be null.");
        Objects.requireNonNull(colorThemeNames, "Array of color theme names cannot be null.");

        // If there is no color theme to register, return
        if (colorThemeNames.length == 0) return;

        // If the directory path does not have a leading slash, add it
        // Use startsWith instead of charAt to handle empty path (root)
        if (!directory.startsWith("/")) directory = '/' + directory;

        // If the directory path does not have a trailing slash, add it
        if (directory.charAt(directory.length() - 1) != '/') directory += '/';

        // For each color theme to register
        for (String colorThemeName : colorThemeNames) {
            // Get the resource path for the color theme JSON file
            String resourcePath = directory + colorThemeName + ".json";

            // Read and flatten the color theme JSON file
            JsonUtils.readFromResources(resourcePath).map(json -> JsonUtils.flatten(json, Theme::parseColor)).ifPresentOrElse(
                    // Register the color theme under the given color theme name
                    colorTheme -> colorThemeRegistry.put(colorThemeName, colorTheme),
                    // Log warning
                    () -> logger.warn("Unable to find or read color theme JSON file. path={}", resourcePath)
            );
        }
    }

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
    private static Optional<Color> parseColor(String valueString) {
        // Remove surrounding whitespace
        valueString = valueString.strip();

        // If the value string contains a comma or whitespace character, parse individual RGB(A) values
        if (valueString.matches(".*[\\s,].*")) return parseRGB(valueString);

        // Parse hexadecimal color value
        return parseHex(valueString);
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
            Theme.logger.warn("Color has invalid number of color channels. value={} channels={}", valueString, args.length);

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
            Theme.logger.warn("Color has invalid RGB(A) values. value={}", valueString);

            // Return empty optional
            return Optional.empty();

        } catch (IllegalArgumentException _) {
            // Log warning
            Theme.logger.warn("Color has out-of-bounds RGB(A) values. value={}", valueString);

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
            Theme.logger.warn("Color has hexadecimal value of invalid length. value={}", valueString);

            // Return empty optional
            return Optional.empty();

        } catch (NumberFormatException _) {
            // Log warning
            Theme.logger.warn("Color has invalid hexadecimal value. value={}", valueString);

            // Return empty optional
            return Optional.empty();
        }
    }

    /// Sets the color theme to the given registered color theme.
    ///
    /// Sets the corresponding color for the color key {@value #BACKGROUND_COLOR_KEY} as the background color if present.
    ///
    /// **Special cases:**
    /// - Logs a warning and does nothing if the given color theme name is not registered
    ///
    /// @param colorThemeName the registered name of the color theme
    public static void setColorTheme(String colorThemeName) {
        // If the color theme registry contains the given color theme name
        if (colorThemeRegistry.containsKey(colorThemeName)) {
            // Set the corresponding color theme
            colorTheme = colorThemeRegistry.get(colorThemeName);

            // Update the background color
            updateBackgroundColor();
        }

        // Color theme registry does not contain the given color theme name -> log warning and do nothing
        else logger.warn("Color theme name is not registered. value={}", colorThemeName);
    }

    /// Sets the corresponding color for the color key {@value #BACKGROUND_COLOR_KEY} as the background color if present.
    ///
    /// **Special cases:**
    /// - Does nothing if no color theme has been set yet, or if the color theme does not contain the key {@value BACKGROUND_COLOR_KEY}
    private static void updateBackgroundColor() {
        // If no color theme has been set, return
        if (colorTheme == null) return;

        // Get the background color from the color theme
        Color backgroundColor = colorTheme.get(BACKGROUND_COLOR_KEY);

        // If the color theme contains the background color, set it as the background color
        if (backgroundColor != null) GUIManager.setBackgroundColor(backgroundColor);
    }

    /// Returns the corresponding color for the given color key.
    ///
    /// Example color key: `menu.button.background`
    ///
    /// **Special cases:**
    /// - Logs a warning and returns {@link #PLACEHOLDER_COLOR} if no color theme has been set yet,
    ///   or if the corresponding color is missing
    ///
    /// @param key the color key
    /// @return the corresponding color
    /// @throws NullPointerException if the input `key` is null
    /// @see Color
    public static Color getColor(String key) {
        Objects.requireNonNull(key, "Color key cannot be null.");

        // If no color theme has been set
        if (colorTheme == null) {
            // Log warning
            logger.warn("Cannot access color because no color theme is set. key={}", key);

            // Return the placeholder color
            return PLACEHOLDER_COLOR;
        }

        // If the color theme does not contain the color key
        if (!colorTheme.containsKey(key)) {
            // Log warning
            logger.warn("Color is missing. key={}", key);

            // Return the placeholder color
            return PLACEHOLDER_COLOR;
        }

        // Return the color value
        return colorTheme.get(key);
    }

    // --- FONTS ---
    /// The default text font of the application
    public static final Font DEFAULT_FONT = new Font("Calibri", Font.PLAIN, 24);

    /// The current text font of the application
    private static Font font = DEFAULT_FONT;

    /// Returns the current text font of the application with the given font size and style.
    /// @param size the font size (in points)
    /// @param bold whether the returned font is bold
    /// @param italic whether the returned font is italic
    /// @return the current text font with the given font size and style
    /// @see Font
    public static Font getFont(float size, boolean bold, boolean italic) {
        // Get the font style based on the given parameters
        int style = (bold ? Font.BOLD : Font.PLAIN) | (italic ? Font.ITALIC : Font.PLAIN);

        // Derive and return the application font with the given font size and style
        return font.deriveFont(style, size);
    }

    /// Returns the current text font of the application with the given font size, in plain style.
    /// @param size the font size (in points)
    /// @return the current text font with the given font size, in plain style
    /// @see Font
    public static Font getFont(float size) {
        // Return the application font with the given font size in plain style
        return getFont(size, false, false);
    }

    /// Sets the text font of the application to the given text font.
    /// @param font the new text font of the application
    /// @throws NullPointerException if the input `font` is null
    /// @see Font
    public static void setFont(Font font) {
        Objects.requireNonNull(font, "Application font cannot be null.");

        // Set the application font
        Theme.font = font;
    }
}