package io.github.isoyigido.basic.gui.core.loader;

import io.github.isoyigido.basic.gui.core.Anchor;
import io.github.isoyigido.basic.gui.core.Component;
import io.github.isoyigido.basic.gui.core.GUI;
import io.github.isoyigido.basic.gui.core.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/// This utility class provides the static method {@link #load(String)} to load and parse a GUI file from resources.
/// @see #load(String)
/// @see ComponentBuilder
/// @see ComponentBuilderRegistry
/// @see Parameter
/// @see GUI
/// @see Widget
/// @see Component
public final class GUILoader {
    /// Private constructor to prevent instantiation
    private GUILoader() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(GUILoader.class);

    // --- REGEX PATTERNS ---
    /// The regex pattern for component types (e.g., `image`, `custom_button`)
    private static final String COMPONENT_TYPE_REGEX = "[A-Za-z0-9_-]+";

    /// The regex pattern for component names (e.g., `logo_image`, `button_1`)
    private static final String COMPONENT_NAME_REGEX = "[A-Za-z0-9_-]+";

    /// The regex pattern for parameter keys (e.g., `label`, `color_1`)
    private static final String PARAMETER_KEY_REGEX = "[A-Za-z0-9_-]+";

    /// The regex pattern for constant keys (e.g., `padding`, `text_color_1`)
    private static final String CONSTANT_KEY_REGEX = "[A-Za-z0-9_-]+";

    // --- PATTERNS ---
    /// The pattern for declaring components (`name = type {`)
    private static final Pattern COMPONENT_DECLARATION_PATTERN
            = Pattern.compile("^\\s*(%s)\\s*=\\s*(%s)\\s*\\{\\s*$".formatted(COMPONENT_NAME_REGEX, COMPONENT_TYPE_REGEX));

    /// The pattern for declaring widgets (`componentname {`)
    private static final Pattern WIDGET_DECLARATION_PATTERN
            = Pattern.compile("^\\s*(%s)\\s*\\{\\s*$".formatted(COMPONENT_NAME_REGEX));

    /// The pattern for accessing widget attributes (`@{componentname.attributekey}`)
    private static final Pattern WIDGET_ATTRIBUTE_ACCESS_PATTERN
            = Pattern.compile("@\\{(%s).(.+?)}".formatted(COMPONENT_NAME_REGEX));

    /// The pattern for declaring parameters (`key: value`)
    private static final Pattern PARAMETER_DECLARATION_PATTERN
            = Pattern.compile("^\\s*(%s)\\s*:\\s*(.+?)\\s*$".formatted(PARAMETER_KEY_REGEX));

    /// The pattern for declaring constants (`key = value`)
    private static final Pattern CONSTANT_DECLARATION_PATTERN
            = Pattern.compile("^\\s*(%s)\\s*=\\s*(.+?)\\s*$".formatted(CONSTANT_KEY_REGEX));

    /// The pattern for accessing declared constant values (`${key}`)
    private static final Pattern CONSTANT_ACCESS_PATTERN
            = Pattern.compile("\\$\\{(%s)\\}".formatted(CONSTANT_KEY_REGEX));

    /// Loads the GUI file at the given path relative to the resources folder.
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
    /// components = [
    ///     COMPONENTNAME = COMPONENTTYPE {
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         ...
    ///     }
    ///     COMPONENTNAME = COMPONENTTYPE {
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         ...
    ///     }
    ///     ...
    /// ]
    ///
    /// widgets = [
    ///     COMPONENTNAME {
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         PARAMETERKEY: PARAMETERVALUE
    ///         ...
    ///     }
    ///     COMPONENTNAME {
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
    /// Constant values do NOT have to be declared at the top of the file. Constants can be declared
    /// with lines following the format `key = value`, and can be accessed inside a widget declaration
    /// with the format `${key}`. A constant can only be accessed after it is declared. A constant key
    /// can consist of lowercase letters, uppercase letters, digits, underscores (`_`), and hyphens (`-`).
    /// There is no character limitation for constant values.
    ///
    /// Below is an example for a GUI file `example.gui`:
    /// ```text
    /// image_path = /gui/images/example.jpg
    /// text_color = #248D96
    /// font_size = 20.5
    ///
    /// components = [
    ///     example_image = image {
    ///         path: ${image_path}
    ///         width: 256
    ///         height: 256
    ///     }
    ///     example_text = text {
    ///         text: "Example GUI"
    ///         color: ${text_color}
    ///         font-size: ${font_size}
    ///     }
    /// ]
    ///
    /// widgets = [
    ///     example_image {
    ///         x: 960
    ///         y: 500
    ///         anchor: center
    ///     }
    ///     example_text {
    ///         x: @{example_image.center.x}
    ///         y: 650
    ///         anchor: top
    ///     }
    /// ]
    /// ```
    /// *Note that the component types `text` and `image` are just examples and may not come built-in with
    /// the API, or even if they do, their implementations may be different. Check the relevant documentation
    /// for more information.*
    ///
    /// A component name can consist of lowercase letters, uppercase letters, digits, underscores (`_`), and hyphens (`-`).
    /// Attributes of a declared widget can be accessed via `@{componentname.attributekey}`.
    ///
    /// **Recognized attribute keys:**
    /// - `left`: the leftmost x-coordinate of the widget
    /// - `right`: the rightmost x-coordinate of the widget
    /// - `top`: the topmost y-coordinate of the widget
    /// - `bottom`: the bottommost y-coordinate of the widget
    /// - `center.x`: the x-coordinate of the widget center
    /// - `center.y`: the y-coordinate of the widget center
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the given path does not exist in resources
    /// - Logs an error and returns an empty {@link Optional} if an {@link IOException} is caught
    /// - Returns a supplier for a {@link GUI} instance with no widgets if an empty or unrelated file is at the given path
    ///
    /// @param path the path to the GUI file relative to the resources folder (e.g., `/gui/menu.gui`)
    /// @return an {@link Optional} containing the loaded {@link GUI} instance supplier,
    ///         or an empty {@link Optional} if the given path does not exist in resources,
    ///         or if an {@link IOException} is caught
    /// @throws NullPointerException if the input `path` is null
    /// @throws IllegalArgumentException if the input `path` is empty
    public static Optional<Supplier<GUI>> load(String path) {
        // Read and parse the GUI file content, and return the supplier for the GUI
        return read(path).map(content -> () -> GUILoader.parseContent(content));
    }

    /// Reads the GUI file at the given path relative to the resources folder, and returns its content.
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the given path does not exist in resources
    /// - Logs an error and returns an empty {@link Optional} if an {@link IOException} is caught
    ///
    /// @param path the path to the GUI file relative to the resources folder (e.g., `/gui/menu.gui`)
    /// @return an {@link Optional} containing the content of the GUI file,
    ///         or an empty {@link Optional} if the given path does not exist in resources,
    ///         or if an {@link IOException} is caught
    /// @throws NullPointerException if the input `path` is null
    /// @throws IllegalArgumentException if the input `path` is empty
    private static Optional<String> read(String path) {
        Objects.requireNonNull(path, "Path cannot be null.");

        // If the path is empty, throw an illegal argument exception
        if (path.isEmpty()) throw new IllegalArgumentException("Path cannot be empty.");

        // If the path does not have a leading slash, add it
        if (path.charAt(0) != '/') path = '/' + path;

        // Get the GUI file from the resources as a stream
        try (InputStream is = GUILoader.class.getResourceAsStream(path)) {
            // If there is no GUI file at the given resource path
            if (is == null) {
                // Log warning
                logger.warn("Unable to find GUI file. path={}", path);

                // Return empty optional
                return Optional.empty();
            }

            // Get a new BufferedReader instance to read the input stream
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                // Join the lines of the GUI file with newline characters and return the joined string
                return Optional.of(reader.lines().collect(Collectors.joining("\n")));
            }

        } catch (IOException e) {
            // Log error
            logger.error("Unable to read GUI file. path={}", path, e);

            // Return empty optional
            return Optional.empty();
        }
    }

    /// Parses the given GUI file content and builds a new {@link GUI} instance.
    /// @param content the GUI file content
    /// @return the built {@link GUI} instance
    private static GUI parseContent(String content) {
        // Initialize the scanner for the GUI file
        Scanner scanner = new Scanner(content);

        // Initialize the map of components declared in the GUI file
        final Map<String, Component> components = new HashMap<>(4);

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

            // - Line declares a component list -
            if (line.matches("^components\\s*=\\s*\\[")) {
                // Parse components
                parseComponents(scanner, components, constants);

                // Continue
                continue;
            }

            // - Line declares a widget list -
            if (line.matches("^widgets\\s*=\\s*\\[")) {
                // Parse and add declared widgets
                widgets.addAll(parseWidgets(scanner, components, constants));

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
            logger.warn("Encountered unidentified expression in the GUI file. line={}", line);
        }

        // Build and return the GUI
        return build(widgets);
    }

    /// Parses the list of components declared in the component list in the GUI file.
    ///
    /// **Special cases:**
    /// - Logs a warning and skips the line if an unidentified expression is encountered
    /// - Logs a warning and skips the component declaration if an unknown component type is encountered
    ///
    /// @param scanner the GUI file scanner (reader)
    /// @param components the map of components declared in the GUI file (will be updated)
    /// @param constants the map of constants declared in the GUI file
    private static void parseComponents(Scanner scanner, Map<String, Component> components, Map<String, String> constants) {
        // Iterate over the lines of the component list
        while (scanner.hasNextLine()) {
            // Read the line and remove surrounding whitespace
            String line = scanner.nextLine().strip();

            // If the line is blank, skip it
            if (line.isBlank()) continue;

            // If the line marks the end of the component list, return
            if ("]".equals(line)) return;

            // Get the matcher for the component declaration pattern
            Matcher matcher = COMPONENT_DECLARATION_PATTERN.matcher(line);

            // If the pattern does not match
            if (!matcher.matches()) {
                // Log warning
                logger.warn("Encountered unidentified expression while parsing components. line={}", line);

                // Skip the line
                continue;
            }

            // Get the type of the component
            String type = matcher.group(2);

            // Get the corresponding component builder instance from the component builder registry
            Optional<ComponentBuilder> optionalComponentBuilder = ComponentBuilderRegistry.get(type);

            // If the component type is not registered
            if (optionalComponentBuilder.isEmpty()) {
                // Log warning
                logger.warn("Encountered unknown component type. Skipping the component declaration. type={}", type);

                // Skip lines until this component declaration is over
                while (scanner.hasNextLine()) {
                    if (scanner.nextLine().strip().matches("^}\\s*")) break;
                }

                // Continue with the next line
                continue;
            }

            // Get the actual component builder instance and set the map of other components for it
            ComponentBuilder componentBuilder = optionalComponentBuilder.get().setOtherComponents(components);

            // Parse the component declaration using the registered component builder instance,
            // and put the component into the map of components
            parseComponent(componentBuilder, scanner, components, constants)
                    .ifPresent(component -> components.put(matcher.group(1), component));
        }

        // - The end of the GUI file is reached without concluding the component list -
        // Log warning
        logger.warn("The end of the GUI file has been reached without concluding the component list.");
    }

    /// Parses and returns the component declared in the GUI file.
    ///
    /// **Special cases:**
    /// - Logs a warning and skips the line if an unidentified expression is encountered
    /// - Logs a warning and returns an empty {@link Optional} if the end of the file is reached
    ///   without concluding the component declaration
    ///
    /// @param componentBuilder the new {@link ComponentBuilder} instance for building the component
    /// @param scanner the GUI file scanner (reader)
    /// @param components the map of components declared in the GUI file
    /// @param constants the map of constants declared in the GUI file
    /// @return an {@link Optional} containing the built component,
    ///         or an empty {@link Optional} if the {@link ComponentBuilder} instance returns an empty {@link Optional},
    ///         or if the end of the file is reached without concluding the component declaration
    private static Optional<Component> parseComponent(ComponentBuilder componentBuilder, Scanner scanner, Map<String, Component> components, Map<String, String> constants) {
        // Iterate over the lines of the component declaration
        while (scanner.hasNextLine()) {
            // Read the line and remove surrounding whitespace
            String line = scanner.nextLine().strip();

            // If the line is blank, continue
            if (line.isBlank()) continue;

            // If the line marks the end of the component declaration, build and return the component
            if ("}".equals(line)) return componentBuilder.buildNewObject();

            // Parse the parameter declaration
            parseParameter(componentBuilder, line, scanner, components, constants);
        }

        // - The end of the GUI file is reached without concluding the component declaration -
        // Log warning
        logger.warn("The end of the GUI file has been reached without concluding the component declaration.");

        // Return empty optional
        return Optional.empty();
    }

    /// Parses and returns the list of widgets declared in the widget list in the GUI file.
    ///
    /// **Special cases:**
    /// - Logs a warning and skips the line if an unidentified expression is encountered
    /// - Logs a warning and skips the widget declaration if the component name is not found
    /// - Logs a warning and returns the declared list of widgets if the end of the file is reached
    ///   without concluding the widget list
    ///
    /// @param scanner the GUI file scanner (reader)
    /// @param components the map of components declared in the GUI file
    /// @param constants the map of constants declared in the GUI file
    /// @return the list of widgets
    private static List<Widget> parseWidgets(Scanner scanner, Map<String, Component> components, Map<String, String> constants) {
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

            // Get the matcher for the widget declaration pattern
            Matcher matcher = WIDGET_DECLARATION_PATTERN.matcher(line);

            // If the pattern does not match
            if (!matcher.matches()) {
                // Log warning
                logger.warn("Encountered unidentified expression while parsing widgets. line={}", line);

                // Skip the line
                continue;
            }

            // Get the name of the component
            String componentName = matcher.group(1);

            // Get the component
            Component component = components.get(componentName);

            // If the map of components does not contain a component with the given name
            if (component == null) {
                // Log warning
                logger.warn("Encountered undefined component while parsing widgets. name={}", componentName);

                // Skip lines until this widget declaration is over
                while (scanner.hasNextLine()) {
                    if (scanner.nextLine().strip().matches("^}\\s*")) break;
                }

                // Continue with the next line
                continue;
            }

            // Parse the widget declaration and add the widget to the list
            parseWidget(component, scanner, components, constants).ifPresent(widgets::add);
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
    /// @param component the component stored in the widget
    /// @param scanner the GUI file scanner (reader)
    /// @param components the map of components declared in the GUI file
    /// @param constants the map of constants declared in the GUI file
    /// @return an {@link Optional} containing the built widget,
    ///         or an empty {@link Optional} if the {@link WidgetBuilder} instance returns an empty {@link Optional},
    ///         or if the end of the file is reached without concluding the widget declaration
    private static Optional<Widget> parseWidget(Component component, Scanner scanner, Map<String, Component> components, Map<String, String> constants) {
        // Initialize a new WidgetBuilder instance
        WidgetBuilder widgetBuilder = new WidgetBuilder(component);

        // Iterate over the lines of the widget declaration
        while (scanner.hasNextLine()) {
            // Read the line and remove surrounding whitespace
            String line = scanner.nextLine().strip();

            // If the line is blank, continue
            if (line.isBlank()) continue;

            // If the line marks the end of the widget declaration, build and return the widget
            if ("}".equals(line)) return widgetBuilder.buildNewObject();

            // Parse the parameter declaration
            parseParameter(widgetBuilder, line, scanner, components, constants);
        }

        // - The end of the GUI file is reached without concluding the widget declaration -
        // Log warning
        logger.warn("The end of the GUI file has been reached without concluding the widget declaration.");

        // Return empty optional
        return Optional.empty();
    }

    /// Parses the parameter declaration and sets the value of the corresponding parameter of the given {@link Builder} instance.
    ///
    /// **Special cases:**
    /// - Logs a warning and does nothing if an unidentified expression is encountered
    ///
    /// @param builder the builder whose parameter value is set
    /// @param line the line which declares the parameter
    /// @param scanner the GUI file scanner (reader)
    /// @param components the map of components declared in the GUI file
    /// @param constants the map of constants declared in the GUI file
    private static void parseParameter(Builder<?> builder, String line, Scanner scanner, Map<String, Component> components, Map<String, String> constants) {
        // Remove surrounding whitespace, resolve widget attributes and constants
        line = resolveWidgetAttributesAndConstants(line.strip(), components, constants);

        // Get the matcher for the parameter declaration pattern
        Matcher matcher = PARAMETER_DECLARATION_PATTERN.matcher(line);

        // If the pattern does not match
        if (!matcher.matches()) {
            // Log warning
            logger.warn("Encountered unidentified expression while parsing parameters. line={}", line);

            // Return
            return;
        }

        // Get the parameter key and value
        String key = matcher.group(1);
        String value = matcher.group(2);

        // Line declares a list parameter -> parse list parameter and set the value of the parameter tied to the key
        if ("[".equals(value)) parseListParameter(scanner, components, constants)
                .ifPresent(parsed -> builder.setParameterValue(key, parsed));

        // Line declares a regular parameter -> set the value of the parameter tied to the key
        else builder.setParameterValue(key, value);
    }

    /// Parses the list parameter and returns the string where the string representations of the elements are separated by newline characters.
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the end of the file is reached without concluding the list parameter
    ///
    /// @param scanner the GUI file scanner (reader)
    /// @param components the map of components declared in the GUI file
    /// @param constants the map of constants declared in the GUI file
    /// @return an {@link Optional} containing the string where the string representations of the elements are separated by newline characters,
    ///         or an empty {@link Optional} if the end of the file is reached without concluding the list parameter
    private static Optional<String> parseListParameter(Scanner scanner, Map<String, Component> components, Map<String, String> constants) {
        // Initialize a new StringBuilder instance with the opening bracket
        StringBuilder sb = new StringBuilder("[");

        // Iterate over the lines of the list
        while (scanner.hasNextLine()) {
            // Read the line, remove surrounding whitespace, and resolve widget attributes and constants
            String line = resolveWidgetAttributesAndConstants(scanner.nextLine().strip(), components, constants);

            // Append a newline character and the line to the string builder
            sb.append('\n').append(line);

            // If the line marks the end of the list, return an optional containing the built string
            if ("]".equals(line)) return Optional.of(sb.toString());
        }

        // - The end of the GUI file is reached without concluding the list parameter -
        // Log warning
        logger.warn("The end of the GUI file has been reached without concluding the list parameter.");

        // Return empty optional
        return Optional.empty();
    }

    /// Uses {@link #resolveWidgetAttributes(CharSequence, Map)} and {@link #resolveConstants(CharSequence, Map)}
    /// to resolve widget attributes and constant values sequentially, and returns the resolved line.
    /// @param line the original line
    /// @param components the map of components declared in the GUI file
    /// @param constants the map of constants declared in the GUI file
    /// @return the resolved line
    private static String resolveWidgetAttributesAndConstants(CharSequence line, Map<String, Component> components, Map<String, String> constants) {
        // Resolve widget attributes and constant values sequentially
        return resolveConstants(resolveWidgetAttributes(line, components), constants);
    }

    /// Resolves widget attributes in the given line by replacing widget attribute access strings with corresponding values.
    /// Logs a warning and keeps the widget attribute access string unchanged if an undefined component name or an unrecognized
    /// attribute key is encountered.
    ///
    /// **Format:** `@{componentname.attributekey}` -> `value`
    ///
    /// **Examples:**
    /// - `x: @{image.center.x}` -> `x: 400`
    /// - `y: @{image.bottom}` -> `y: 600`
    ///
    /// **Special cases:**
    /// - Logs a warning and keeps the widget attribute access string unchanged if an undefined component name
    ///   or an unrecognized attribute key is encountered
    ///
    /// @param line the original line
    /// @param components the map of components declared in the GUI file
    /// @return the resolved line
    private static String resolveWidgetAttributes(CharSequence line, Map<String, Component> components) {
        // Get the matcher for the widget attribute access pattern
        Matcher matcher = WIDGET_ATTRIBUTE_ACCESS_PATTERN.matcher(line);

        // Initialize a new StringBuilder instance
        StringBuilder sb = new StringBuilder(line.length());

        // Iterate over each widget attribute access
        while (matcher.find()) {
            // Get the name of the component
            String name = matcher.group(1);

            // Get the component
            Component component = components.get(name);

            // If no component has been declared with the name
            if (component == null) {
                // Log warning
                logger.warn("Encountered undefined component while trying to access widget attribute. name={}", name);

                // Keep the widget attribute access string unchanged
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));

                // Continue with the next occurrence
                continue;
            }

            // Get the widget which the component with the given name is stored in
            Widget widget = component.getWidget();

            // If the component is not stored in a widget
            if (widget == null) {
                // Log warning
                logger.warn("Cannot access attributes of a component that is not stored in a widget. name={}", name);

                // Keep the widget attribute access string unchanged
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));

                // Continue with the next occurrence
                continue;
            }

            // Get the attribute key
            String attributeKey = matcher.group(2);

            // Get the attribute of the named widget based on the attribute key
            getWidgetAttribute(widget, attributeKey).ifPresentOrElse(
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
    /// - `left`: the leftmost x-coordinate of the widget
    /// - `right`: the rightmost x-coordinate of the widget
    /// - `top`: the topmost y-coordinate of the widget
    /// - `bottom`: the bottommost y-coordinate of the widget
    /// - `center.x`: the x-coordinate of the widget center
    /// - `center.y`: the y-coordinate of the widget center
    ///
    /// @param widget the widget whose attribute is returned
    /// @param attributeKey the attribute key
    /// @return an {@link Optional} containing the widget attribute string,
    ///         or an empty {@link Optional} if the given attribute key is unrecognized
    private static Optional<String> getWidgetAttribute(Widget widget, String attributeKey) {
        // Get the attribute integer based on the attribute key
        Integer value = switch (attributeKey) {
            case "left"     -> widget.getX(Anchor.LEFT);
            case "center.x" -> widget.getX(Anchor.CENTER);
            case "right"    -> widget.getX(Anchor.RIGHT);
            case "top"      -> widget.getY(Anchor.TOP);
            case "center.y" -> widget.getY(Anchor.CENTER);
            case "bottom"   -> widget.getY(Anchor.BOTTOM);
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

            // Get the constant value
            String value = constants.get(key);

            // If no constant has been declared with the key
            if (value == null) {
                // Log warning
                logger.warn("Encountered undefined constant. value={}", key);

                // Keep the constant access string unchanged
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));

                // Continue with the next occurrence
                continue;
            }

            // Replace the constant access string with the value of the declared constant
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
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

        // Add declared widgets
        widgets.forEach(gui::addWidget);

        // Return the GUI
        return gui;
    }
}