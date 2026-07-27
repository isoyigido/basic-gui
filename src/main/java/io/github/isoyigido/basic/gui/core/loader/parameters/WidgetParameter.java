package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.Widget;
import io.github.isoyigido.basic.gui.core.loader.Parameter;
import io.github.isoyigido.basic.gui.core.loader.WidgetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/// Overrides the {@link #parse(String)} method to get the declared widget with the given name.
///
/// @see #parse(String)
/// @see Parameter
/// @see Widget
public class WidgetParameter extends Parameter<Widget> {
    private static final Logger logger = LoggerFactory.getLogger(WidgetParameter.class);

    /// The {@link WidgetBuilder} instance that holds this parameter
    private final WidgetBuilder widgetBuilder;

    /// Constructs a widget parameter.
    /// @param widgetBuilder the {@link WidgetBuilder} instance that holds this parameter
    public WidgetParameter(WidgetBuilder widgetBuilder) {
        // Set the widget builder instance
        this.widgetBuilder = widgetBuilder;
    }

    /// Uses {@link WidgetBuilder#getNamedWidgets()} to get the map of named widgets from the stored
    /// {@link WidgetBuilder} instance, and returns the declared widget with the given name.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if the stored {@link WidgetBuilder} instance is missing the map of named widgets,
    ///   or if no widget has been declared with the given name
    /// @param valueString the name of the declared widget
    /// @return an {@link Optional} containing the declared widget with the given name,
    ///         or an empty {@link Optional} if the stored {@link WidgetBuilder} instance is missing the map of named widgets,
    ///         or if no widget has been declared with the given name
    @Override
    public Optional<Widget> parse(String valueString) {
        // Get the map of named widgets from the stored widget builder instance
        Optional<Map<String, Widget>> optionalWidgetMap = this.widgetBuilder.getNamedWidgets();

        // If the stored widget builder instance is missing the map of named widgets
        if (optionalWidgetMap.isEmpty()) {
            // Log warning
            WidgetParameter.logger.warn("Widget builder is missing the map of named widgets.");

            // Return empty optional
            return Optional.empty();
        }

        // Get the widget with the given name from the map of named widgets
        Widget widget = optionalWidgetMap.get().get(valueString.strip());

        // If there is no declared widget with the given name, log warning
        if (widget == null) WidgetParameter.logger.warn("Widget cannot be found. name={}", valueString);

        // Return an optional of the widget
        return Optional.ofNullable(widget);
    }
}