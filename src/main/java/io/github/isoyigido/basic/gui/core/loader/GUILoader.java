package io.github.isoyigido.basic.gui.core.loader;

import io.github.isoyigido.basic.gui.core.GUI;
import io.github.isoyigido.basic.gui.core.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// This utility class provides the static method {@link #load(String)} to load and parse a GUI file from resources.
/// @see #load(String)
/// @see WidgetBuilder
/// @see WidgetBuilderRegistry
/// @see Parameter
/// @see GUI
/// @see Widget
public final class GUILoader {
    /// Private constructor to prevent instantiation
    private GUILoader() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(GUILoader.class);

    /// This cache maps GUI file resource paths to GUI suppliers to avoid parsing the same file twice.
    private static final Map<String, Supplier<GUI>> cache = new HashMap<>(4);

    /// The regex pattern for widget names (e.g., `title_text`, `button_1`)
    private static final String WIDGET_NAME_REGEX = "[A-Za-z0-9_]+";

    /// The pattern for widget names (e.g., `title_text`, `button_1`)
    private static final Pattern WIDGET_NAME_PATTERN
            = Pattern.compile(WIDGET_NAME_REGEX);

    /// The pattern for accessing widget attributes (`@{widgetname.attributekey}`)
    private static final Pattern WIDGET_ATTRIBUTE_ACCESS_PATTERN
            = Pattern.compile("@\\{(%s).(.+?)}".formatted(WIDGET_NAME_REGEX));

    /// The regex pattern for constant keys (e.g., `padding`, `text_color_1`)
    private static final String CONSTANT_KEY_REGEX = "[A-Za-z0-9_]+";

    /// The pattern for constant declaration lines (`key = value`)
    private static final Pattern CONSTANT_DECLARATION_PATTERN
            = Pattern.compile("^\\s*(%s)\\s*=\\s*(.+?)\\s*$".formatted(CONSTANT_KEY_REGEX));

    /// The pattern for accessing declared constant values (`${key}`)
    private static final Pattern CONSTANT_ACCESS_PATTERN
            = Pattern.compile("\\$\\{(%s)\\}".formatted(CONSTANT_KEY_REGEX));

    /// Loads and parses the GUI file at the given path relative to the resources folder.
    /// If the GUI file has been parsed before, uses the cached GUI supplier.
    ///
    /// **GUI file structure:**
    ///
    /// A GUI file is a text based, simple way to build a GUI instance without writing code. GUI files are
    /// plain text files, and can have any extension, but `.gui` should be used to clarify the file format.
    ///
    /// The structure of a GUI file is shown below:
    /// ```text
    /// CONSTANTKEY = CONSTANTVALUE
    /// CONSTANTKEY = CONSTANTVALUE
    /// ...
    ///
    /// widgets = [
    ///     WIDGETTYPE {
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         ...
    ///     }
    ///     WIDGETTYPE {
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         ...
    ///     }
    ///     ...
    /// ]
    /// ```
    /// *Note: Whitespace (except newline characters) can be added anywhere without changing the outcome,
    ///        so long as it does not break individual arguments. Blank lines are skipped by the parser.*
    ///
    /// Constant values can be declared anywhere except inside the widget list. Constants can be declared
    /// with lines following the format `key = value`, and can be accessed inside a widget declaration
    /// with the format `${key}`. A constant can only be accessed after it is declared. A constant key can consist
    /// of lowercase letters, uppercase letters, digits, and an underscore (`_`). There is no character limitation
    /// for constant values.
    ///
    /// Below is an example for a GUI file `example.gui`:
    /// ```text
    /// image_path = /gui/images/example.jpg
    /// text_color = #248D96
    /// font_size = 20.5
    ///
    /// widgets = [
    ///     image {
    ///         name: example_image
    ///         x: 960
    ///         y: 500
    ///         anchor: center
    ///         path: ${image_path}
    ///         width: 256
    ///         height: 256
    ///     }
    ///     text {
    ///         x: @{example_image.center.x}
    ///         y: 650
    ///         anchor: top
    ///         text: "Example GUI"
    ///         color: ${text_color}
    ///         font-size: ${font_size}
    ///     }
    /// ]
    /// ```
    /// *Note that the widget types `text` and `image` are just examples and may not come built-in with
    /// the API, or even if they do, their implementations may be different. Check the relevant documentation
    /// for more information.*
    ///
    /// The special `name` parameter makes the attributes of a widget accessible via `@{widgetname.attributekey}`.
    /// A widget name can consist of lowercase letters, uppercase letters, digits, and an underscore (`_`).
    ///
    /// **Recognized attribute keys:**
    /// - `width`: the width of the widget
    /// - `height`: the height of the widget
    /// - `left`: the leftmost x-coordinate of the widget
    /// - `right`: the rightmost x-coordinate of the widget
    /// - `top`: the topmost y-coordinate of the widget
    /// - `bottom`: the bottommost y-coordinate of the widget
    /// - `center.x`: the x-coordinate of the widget center
    /// - `center.y`: the y-coordinate of the widget center
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if the given path does not exist in resources,
    ///   or if an {@link IOException} is caught
    /// - Returns a new {@link GUI} instance with no widgets if an empty or unrelated file is at the given path
    ///
    /// @param path the path to the GUI file relative to the resources folder (e.g., `/gui/menu.gui`)
    /// @return an {@link Optional} containing the parsed new {@link GUI} instance,
    ///         or an empty {@link Optional} if the given path does not exist in resources,
    ///         or if an {@link IOException} is caught
    /// @throws NullPointerException if the input `path` is null
    /// @throws IllegalArgumentException if the input `path` is empty
    public static Optional<GUI> load(String path) {
        Objects.requireNonNull(path, "Path cannot be null.");

        // If the path is empty, throw an illegal argument exception
        if (path.isEmpty()) throw new IllegalArgumentException("Path cannot be empty.");

        // If the path does not have a leading slash, add it
        if (path.charAt(0) != '/') path = '/' + path;

        // If the GUI file is already in cache, use cached GUI supplier
        if (cache.containsKey(path)) return Optional.of(cache.get(path).get());

        // - GUI file is not in cache -> load, parse, and cache the GUI file -
        // Get the GUI file from the resources as a stream
        try (InputStream is = GUILoader.class.getResourceAsStream(path)) {
            // If there is no GUI file at the given resource path, return empty optional
            if (is == null) return Optional.empty();

            // Initialize the scanner for the GUI file
            Scanner scanner = new Scanner(is);

            // Initialize the map of named widgets in the GUI file
            final Map<String, Widget> namedWidgets = new HashMap<>(4);

            // Initialize the map of constants declared in the GUI file
            final Map<String, String> constants = new HashMap<>(4);

            // Initialize the list of widgets declared in the GUI file
            final Collection<Widget> widgets = new ArrayList<>(4);

            // Parse each line of the GUI file
            while (scanner.hasNextLine()) {
                // Read the line and remove surrounding whitespace
                String line = scanner.nextLine().strip();

                // If the line is blank, skip it
                if (line.isBlank()) continue;

                // - Line declares a widget list -
                if (line.matches("^widgets\\s*=\\s*\\[")) {
                    // Parse and add declared widgets
                    widgets.addAll(parseWidgets(scanner, namedWidgets, constants));

                    // Continue
                    continue;
                }

                // - Line declares a constant -
                Matcher matcher = CONSTANT_DECLARATION_PATTERN.matcher(line);
                if (matcher.matches()) {
                    // Put constant key and value pair into map
                    constants.put(matcher.group(1), matcher.group(2));

                    // Continue
                    continue;
                }

                // Line has an unidentified expression -> log warning
                logger.warn("Encountered unidentified expression in GUI file. line={}", line);
            }

            // Put the path and GUI supplier in the cache
            cache.put(path, () -> build(widgets));

            // Build the GUI and return an optional containing it
            return Optional.of(build(widgets));

        } catch (IOException e) {
            // Log error
            logger.error("Unable to read GUI file. path={}", path, e);

            // Return empty optional
            return Optional.empty();
        }
    }

    /// Parses and returns the list of widgets declared in the widget list in the GUI file.
    ///
    /// **Special cases:**
    /// - Logs a warning and skips the line if an unidentified expression is encountered
    /// - Logs a warning and skips the widget declaration if an unknown component type is encountered
    /// - Logs a warning and returns the declared list of widgets if the end of the file is reached
    ///   without concluding the widget list
    ///
    /// @param scanner the GUI file scanner (reader)
    /// @param namedWidgets the map of named widgets in the GUI file (may be updated)
    /// @param constants the map of constants declared in the GUI file
    /// @return the list of widgets
    private static List<Widget> parseWidgets(Scanner scanner, Map<String, Widget> namedWidgets, Map<String, String> constants) {
        // Initialize the list of widgets declared in the widget list
        final List<Widget> widgets = new ArrayList<>(4);

        // Iterate over the lines of the widget list
        while (scanner.hasNextLine()) {
            // Read the line and remove surrounding whitespace
            String line = scanner.nextLine().strip();

            // If the line is blank, skip it
            if (line.isBlank()) continue;

            // If the line marks the end of the widget list, stop iterating and return the list of widgets
            if ("]".equals(line)) return widgets;

            // Line ends with "{" -> widget declaration
            if (line.endsWith("{")) {
                // Get the type of the declared component (e.g., button)
                String type = line.substring(0, line.length() - 1).strip();

                // Get the corresponding widget builder instance from the widget builder registry
                WidgetBuilderRegistry.get(type).ifPresentOrElse(
                        // Parse the widget declaration using the registered widget builder instance
                        widgetBuilder -> parseWidget(widgetBuilder, scanner, namedWidgets, constants).ifPresent(widgets::add),
                        () -> {
                            // Log warning
                            logger.warn("Unknown component type. type={}", type);

                            // Skip lines until this widget declaration is over
                            while (scanner.hasNextLine()) {
                                if (scanner.nextLine().strip().matches("^}\\s*,?")) break;
                            }
                        }
                );
            }

            // Line has an unidentified expression -> log warning
            else logger.warn("Encountered unidentified expression while parsing widgets. line={}", line);
        }

        // - The end of the GUI file is reached without concluding the widget list -
        // Log warning
        logger.warn("The end of the GUI file has been reached without concluding the widget list.");

        // Return the list of widgets
        return widgets;
    }

    /// Parses and returns the widget declared in the GUI file.
    ///
    /// **Special cases:**
    /// - Logs a warning and skips the line if an unidentified expression is encountered
    /// - Logs a warning and returns an empty {@link Optional} if the end of the file is reached
    ///   without concluding the widget declaration
    ///
    /// @param widgetBuilder the new {@link WidgetBuilder} instance for building the {@link Widget}
    /// @param scanner the GUI file scanner (reader)
    /// @param constants the map of constants declared in the GUI file
    /// @param namedWidgets the map of named widgets in the GUI file (may be updated)
    /// @return an {@link Optional} containing the built widget,
    ///         or an empty {@link Optional} if the {@link WidgetBuilder} instance returns an empty {@link Optional},
    ///         or if the end of the file is reached without concluding the widget declaration
    private static Optional<Widget> parseWidget(WidgetBuilder widgetBuilder, Scanner scanner, Map<String, Widget> namedWidgets, Map<String, String> constants) {
        // Initialize the name of the widget (null indicates no set name)
        String name = null;

        // Iterate over the lines
        while (scanner.hasNextLine()) {
            // Read the line, remove surrounding whitespace, and resolve widget attributes and constants
            String line = resolveWidgetAttributesAndConstants(scanner.nextLine().strip(), namedWidgets, constants);

            // If the line is blank, continue
            if (line.isBlank()) continue;

            // If the line marks the end of the widget declaration
            if (line.matches("^}\\s*,?")) {
                // Build the widget using the widget builder instance
                Optional<Widget> optionalWidget = widgetBuilder.build();

                // If the widget is present and has a set name
                if (optionalWidget.isPresent() && (name != null)) {
                    // Put the name-widget pair in the map of named widgets
                    namedWidgets.put(name, optionalWidget.get());
                }

                // Return the optional widget
                return optionalWidget;
            }

            // Split the line into the key and the value (key: value)
            String[] args = line.split(":");

            // If there are not exactly two arguments (key and value)
            if (args.length != 2) {
                // Log warning
                logger.warn("Encountered unidentified expression while parsing widget. line={}", line);

                // Skip the line
                continue;
            }

            // Remove surrounding whitespace
            args[0] = args[0].strip();
            args[1] = args[1].strip();

            // If the parameter key is "name"
            if ("name".equals(args[0])) {
                // If the parameter value is a valid widget name, set the name of the widget
                if (WIDGET_NAME_PATTERN.matcher(args[1]).matches()) name = args[1];

                // Widget name contains unsupported characters -> log warning
                else logger.warn("Widget name contains unsupported characters. value={}", args[1]);

                // Continue with the next line
                continue;
            }

            // Set the value of the parameter tied to the key
            widgetBuilder.setParameterValue(args[0], args[1]);
        }

        // - The end of the GUI file is reached without concluding the widget declaration -
        // Log warning
        logger.warn("The end of the GUI file has been reached without concluding the widget declaration.");

        // Return empty optional
        return Optional.empty();
    }

    /// Uses {@link #resolveWidgetAttributes(CharSequence, Map)} and {@link #resolveConstants(CharSequence, Map)}
    /// to resolve widget attributes and constant values sequentially, and returns the resolved line.
    /// @param line the original line
    /// @param namedWidgets the map of named widgets in the GUI file
    /// @param constants the map of constants declared in the GUI file
    /// @return the resolved line
    private static String resolveWidgetAttributesAndConstants(CharSequence line, Map<String, Widget> namedWidgets, Map<String, String> constants) {
        // Resolve widget attributes and constant values sequentially
        return resolveConstants(resolveWidgetAttributes(line, namedWidgets), constants);
    }

    /// Resolves widget attributes in the given line by replacing widget attribute access strings with corresponding values.
    /// Logs a warning and keeps the widget attribute access string unchanged if an undefined widget name or an unrecognized
    /// attribute key is encountered.
    ///
    /// **Format:** `@{widgetname.attributekey}` -> `value`
    ///
    /// **Examples:**
    /// - `x: @{image.center.x}` -> `x: 400`
    /// - `y: @{image.bottom}` -> `y: 600`
    ///
    /// **Special cases:**
    /// - Logs a warning and keeps the widget attribute access string unchanged if an undefined widget name
    ///   or an unrecognized attribute key is encountered
    ///
    /// @param line the original line
    /// @param namedWidgets the map of named widgets in the GUI file
    /// @return the resolved line
    private static String resolveWidgetAttributes(CharSequence line, Map<String, Widget> namedWidgets) {
        // Get the matcher for the widget attribute access pattern
        Matcher matcher = WIDGET_ATTRIBUTE_ACCESS_PATTERN.matcher(line);

        // Initialize a new StringBuilder instance
        StringBuilder sb = new StringBuilder(line.length());

        // Iterate over each widget attribute access
        while (matcher.find()) {
            // Get the name of the widget
            String name = matcher.group(1);

            // If no widget has been declared with the name
            if (!namedWidgets.containsKey(name)) {
                // Log warning
                logger.warn("Encountered undefined widget name. value={}", name);

                // Keep the widget attribute access string unchanged
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));

                // Continue with the next occurrence
                continue;
            }

            // Get the attribute key
            String attributeKey = matcher.group(2);

            // Get the attribute of the named widget based on the attribute key
            getWidgetAttribute(namedWidgets.get(name), attributeKey).ifPresentOrElse(
                    // Replace the widget attribute access string with the attribute value
                    attribute -> matcher.appendReplacement(sb, Matcher.quoteReplacement(attribute)),
                    () -> {
                        // Log warning
                        logger.warn("Encountered unrecognized attribute key. value={}", attributeKey);

                        // Keep the widget attribute access string unchanged
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
                    }
            );
        }

        // Append any remaining text after the last widget attribute access
        matcher.appendTail(sb);

        // Return the resolved string
        return sb.toString();
    }

    /// Returns an {@link Optional} containing the widget attribute string for the given attribute key,
    /// or an empty {@link Optional} if the given attribute key is unrecognized.
    ///
    /// **Recognized attribute keys:**
    /// - `width`: the width of the widget
    /// - `height`: the height of the widget
    /// - `left`: the leftmost x-coordinate of the widget
    /// - `right`: the rightmost x-coordinate of the widget
    /// - `top`: the topmost y-coordinate of the widget
    /// - `bottom`: the bottommost y-coordinate of the widget
    /// - `center.x`: the x-coordinate of the widget center
    /// - `center.y`: the y-coordinate of the widget center
    ///
    /// @param widget the widget whose attribute is returned
    /// @param attributeKey the attribute key (e.g., `width`)
    /// @return an {@link Optional} containing the widget attribute string,
    ///         or an empty {@link Optional} if the given attribute key is unrecognized
    private static Optional<String> getWidgetAttribute(Widget widget, String attributeKey) {
        // Get the attribute integer based on the attribute key
        Integer value = switch (attributeKey) {
            case "width"    -> widget.getComponent().getWidth();
            case "height"   -> widget.getComponent().getHeight();
            case "left"     -> widget.getX();
            case "center.x" -> widget.getX() + (widget.getComponent().getWidth() / 2);
            case "right"    -> widget.getX() + widget.getComponent().getWidth();
            case "top"      -> widget.getY();
            case "center.y" -> widget.getY() + (widget.getComponent().getHeight() / 2);
            case "bottom"   -> widget.getY() + widget.getComponent().getHeight();
            default -> null; // unrecognized attribute key
        };

        // If the attribute key is unrecognized, return empty optional
        if (value == null) return Optional.empty();

        // Return an optional containing the attribute string
        return Optional.of(Integer.toString(value));
    }

    /// Resolves constant values in the given line by replacing constant access strings with corresponding values.
    /// Logs a warning and keeps the constant access string unchanged if an undefined constant key is encountered.
    ///
    /// **Format:** `${key}` -> `value`
    ///
    /// **Examples:**
    /// - `x: ${padding}` -> `x: 32`
    /// - `color: ${text_color_1}` -> `color: #248D96`
    /// - `label: ${button_label}` -> `label: "Settings"`
    ///
    /// **Special cases:**
    /// - Logs a warning and keeps the constant access string unchanged if an undefined constant key is encountered
    ///   (e.g., `x: ${padding}` is kept as `x: ${padding}` if `padding` is undefined)
    ///
    /// @param line the original line
    /// @param constants the map of constants declared in the GUI file
    /// @return the resolved line
    private static String resolveConstants(CharSequence line, Map<String, String> constants) {
        // Get the matcher for the constant access pattern
        Matcher matcher = CONSTANT_ACCESS_PATTERN.matcher(line);

        // Initialize a new StringBuilder instance
        StringBuilder sb = new StringBuilder(line.length());

        // Iterate over each constant access
        while (matcher.find()) {
            // Get the key for the constant
            String key = matcher.group(1);

            // If a constant has been declared with the key
            if (constants.containsKey(key)) {
                // Replace the constant access string with the value of the declared constant
                matcher.appendReplacement(sb, Matcher.quoteReplacement(constants.get(key)));
            } else {
                // Log warning
                logger.warn("Encountered undefined constant. value={}", key);

                // Keep the constant access string unchanged
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }

        // Append any remaining text after the last constant access
        matcher.appendTail(sb);

        // Return the resolved string
        return sb.toString();
    }

    /// Builds a new {@link GUI} instance with the given widgets.
    /// @param widgets the widgets added to the new {@link GUI} instance
    /// @return the new {@link GUI} instance
    private static GUI build(Iterable<Widget> widgets) {
        // Initialize a new GUI instance
        GUI gui = new GUI();

        // Add loaded widgets
        widgets.forEach(gui::addWidget);

        // Return the GUI
        return gui;
    }
}