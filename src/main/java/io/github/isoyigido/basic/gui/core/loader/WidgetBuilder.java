package io.github.isoyigido.basic.gui.core.loader;

import io.github.isoyigido.basic.gui.core.Component;
import io.github.isoyigido.basic.gui.core.Widget;
import io.github.isoyigido.basic.gui.core.loader.parameters.AnchorParameter;
import io.github.isoyigido.basic.gui.core.loader.parameters.numbers.IntegerParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/// Stores lists of required and optional widget parameters as {@link Parameter} instances.
/// Provides the {@link #setParameterValue(String, String)} method to parse a {@link String}
/// representation from a GUI file and set the value of the parameter tied to the given key.
/// After setting the parameter values, the {@link #build()} method may be used to build
/// a {@link Widget} using the stored parameter values. It returns an {@link Optional} containing
/// the built {@link Widget}, or an empty {@link Optional} if any required parameter does not have
/// a set value or if the provided set of parameter values cannot be used to construct a component.
///
/// Subclasses of this class must add required and optional parameters in a parameterless constructor
/// and override the {@link #buildComponent()} method to provide the component building logic
/// using the added required and optional parameters. A widget declaration in a GUI file which is
/// missing any required parameter will be ignored and the widget will not be added.
///
/// Required parameters may be added using {@link #addRequiredParameter(String, Parameter)} and
/// optional parameters may be added using {@link #addOptionalParameter(String, Parameter)}.
/// Adding parameters using these methods effectively ties the given key in the GUI file to the given parameter.
/// For example, if `addRequiredParameter("color", colorParameter)` is used, and {@link GUILoader} encounters
/// the field `color` in the widget declaration, the corresponding value will be parsed and set for `colorParameter`
/// using {@link #setParameterValue(String, String)}.
///
/// The default parameterless constructor adds `x`, `y`, and `anchor` required parameters;
/// and `layer` optional parameter.
///
/// To allow usage in {@link GUILoader}, subclasses of this class must be annotated with {@link RegisterWidgetBuilder}.
/// Subclasses of this class annotated with {@link RegisterWidgetBuilder} are registered statically in {@link WidgetBuilderRegistry}.
///
/// **Example usage:** \
/// In the example below, the widgets of type `button` in the GUI file will use a new instance
/// of `ButtonBuilder` for creation. The button declaration, along with the default parameters,
/// must include `width`, `height`, and `color` parameters; while it could also include a `label` parameter.
/// ```java
/// @RegisterWidgetBuilder(type = "button")
/// public class ButtonBuilder extends WidgetBuilder {
///     // - Initialize parameter fields -
///     private final IntegerParameter width = new IntegerParameter();
///     private final IntegerParameter height = new IntegerParameter();
///     private final ColorParameter color = new ColorParameter();
///     private final StringParameter label = new StringParameter();
///
///     public ButtonBuilder() {
///         // - Add required and optional parameters in the constructor -
///         super.addRequiredParameter("width", this.width);
///         super.addRequiredParameter("height", this.height);
///         super.addRequiredParameter("color", this.color);
///         super.addOptionalParameter("label", this.label);
///     }
///
///     @Override
///     protected Optional<Component> buildComponent() {
///         // - Get required parameter values -
///         int width = this.width.get();
///         int height = this.height.get();
///         Color color = this.color.get();
///
///         // Component creation logic with optional parameters...
///         Button button = new Button(width, height, color);
///         this.label.getOptional().ifPresent(button::setLabel);
///
///         // Return an optional of the component
///         return Optional.of(button);
///     }
/// }
/// ```
///
/// @apiNote Multiple WidgetBuilder implementations may exist for a single {@link Component} type.
///          For example, a widget builder of type `img-path` and a widget builder of type `img-resource` could
///          both build the same type of image component, while one could load the image from the given path and
///          the other from the given path relative to the resources folder. Furthermore, a single {@link WidgetBuilder}
///          implementation may allow for multiple ways to declare the widget by making use of optional parameters.
///          For example, a widget builder of type `image` may have optional `path` and `resource` parameters,
///          and load the image based on whichever one the user uses in the widget declaration. In the case
///          where the user uses neither, or an illegal combination of both, the {@link #buildComponent()} implementation
///          would return an empty {@link Optional} and preferably log a warning. When the {@link #buildComponent()} method
///          returns an empty {@link Optional}, the widget declaration is ignored and the widget is not added.
/// @see Parameter
/// @see RegisterWidgetBuilder
/// @see WidgetBuilderRegistry
/// @see GUILoader
/// @see Widget
/// @see Component
public abstract class WidgetBuilder {
    private static final Logger logger = LoggerFactory.getLogger(WidgetBuilder.class);

    /// Maps keys to required parameters.
    private final Map<String, Parameter<?>> requiredParameters = new LinkedHashMap<>(4);

    /// Maps keys to optional parameters.
    private final Map<String, Parameter<?>> optionalParameters = new LinkedHashMap<>(4);

    /// The x-coordinate of the widget anchor point (required)
    protected final IntegerParameter x = new IntegerParameter();

    /// The y-coordinate of the widget anchor point (required)
    protected final IntegerParameter y = new IntegerParameter();

    /// The point on the widget anchored to the coordinates (required)
    protected final AnchorParameter anchor = new AnchorParameter();

    /// The layer index of the widget (optional)
    protected final IntegerParameter layerIndex = new IntegerParameter();

    /// Adds the default required parameters `x`, `y`, and `anchor`; and the optional parameter `layer`.
    /// @see #addRequiredParameter(String, Parameter)
    /// @see #addOptionalParameter(String, Parameter)
    protected WidgetBuilder() {
        // Add required parameters (x, y, anchor)
        this.addRequiredParameter("x", this.x);
        this.addRequiredParameter("y", this.y);
        this.addRequiredParameter("anchor", this.anchor);

        // Add optional layer index parameter
        this.addOptionalParameter("layer", this.layerIndex);
    }

    /// Adds a required widget parameter.
    /// @param key the key for the parameter in the GUI file (e.g., `color`)
    /// @param parameter the parameter that is effectively tied to the key
    /// @apiNote Required parameters must have a set value. The {@link #build()} method ensures that
    ///          every required parameter has a set value before building the component and creating
    ///          the widget, and returns an empty {@link Optional} if any required parameter does
    ///          not have a set value. Implementations of {@link #buildComponent()} can safely use
    ///          {@link Parameter#get()} on a required parameter without checking if the parameter
    ///          has a set value.
    /// @see Parameter
    protected void addRequiredParameter(String key, Parameter<?> parameter) {
        // Map the given key to the given required parameter
        this.requiredParameters.put(key, parameter);
    }

    /// Adds an optional widget parameter.
    /// @param key the key for the parameter in the GUI file (e.g., `color`)
    /// @param parameter the parameter that is effectively tied to the key
    /// @apiNote Optional parameters may not have a set value. Implementations of {@link #buildComponent()}
    ///          must use {@link Parameter#getOptional()} on optional parameters to get an {@link Optional}
    ///          of the value, or check whether the optional parameter has a value using {@link Parameter#isPresent()}
    ///          before using {@link Parameter#get()}. Using {@link Parameter#get()} on a parameter that does
    ///          not have a set value causes an {@link UnsupportedOperationException} to be thrown.
    /// @see Parameter
    protected void addOptionalParameter(String key, Parameter<?> parameter) {
        // Map the given key to the given optional parameter
        this.optionalParameters.put(key, parameter);
    }

    /// Parses the given value string and sets the value of the parameter tied to the given key.
    ///
    /// **Special cases:**
    /// - Logs a warning and does nothing if the input `valueString` is blank
    /// - Logs a warning and does nothing if the input `key` is not tied to any parameter
    ///
    /// @param key the key for the parameter in the GUI file (e.g., `label`)
    /// @param valueString the {@link String} representation of the value (e.g., `"My Button"`)
    /// @see Parameter
    protected void setParameterValue(String key, String valueString) {
        // If the value String is blank
        if (valueString.isBlank()) {
            // Log warning
            WidgetBuilder.logger.warn("Encountered empty widget parameter. Skipping it. class={} key={}", this.getClass().getSimpleName(), key);

            // Return
            return;
        }

        // Find whether the given parameter key is required or optional
        boolean required = this.requiredParameters.containsKey(key);
        boolean optional = this.optionalParameters.containsKey(key);

        // If the given parameter key is neither required nor optional (no parameter with that key)
        if (!required && !optional) {
            // Log warning
            WidgetBuilder.logger.warn("Encountered unknown widget parameter. Skipping it. class={} key={}", this.getClass().getSimpleName(), key);

            // Return
            return;
        }

        // Parse the value string and set the value of the parameter with the given key
        if (required) this.requiredParameters.get(key).parseAndSet(valueString);
        if (optional) this.optionalParameters.get(key).parseAndSet(valueString);
    }

    /// Builds a {@link Widget} using the stored parameter values.
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if any required parameter does not have a set value
    /// - Returns an empty {@link Optional} if {@link #buildComponent()} returns an empty {@link Optional}
    ///
    /// @return an {@link Optional} containing the built {@link Widget},
    ///         or an empty {@link Optional} if any required parameter does not have a set value,
    ///         or if the provided set of parameter values cannot be used to construct a component
    /// @see Widget
    protected Optional<Widget> build() {
        // Check each required parameter
        for (Map.Entry<String, Parameter<?>> entry : this.requiredParameters.entrySet()) {
            // If the parameter has no set value
            if (entry.getValue().isEmpty()) {
                // Log warning
                WidgetBuilder.logger.warn("Required widget parameter is missing. key={}", entry.getKey());

                // Return empty optional
                return Optional.empty();
            }
        }

        // Build the component and return a widget
        return this.buildComponent()
                .flatMap(component -> this.anchor.getWidget(component, this.x.get(), this.y.get()))
                .map(widget -> {
                    // Set the layer index of the widget if the layer index parameter has a set value
                    this.layerIndex.getOptional().ifPresent(widget::setLayerIndex);

                    // Return the widget
                    return widget;
                });
    }

    /// Override this method to provide the component building logic using the stored parameters.
    /// This method is called in {@link #build()} after verifying that each required parameter
    /// has a set value.
    ///
    /// If the implementation of this method returns an empty {@link Optional}, the widget declaration
    /// is ignored and the widget is not added.
    ///
    /// @apiNote A single `buildComponent()` implementation may allow for multiple ways to declare the widget
    ///          by making use of optional parameters. For example, a widget builder of type `image` may have
    ///          optional `path` and `resource` parameters, and load the image based on whichever one the user
    ///          uses in the widget declaration. In the case where the user uses neither, or an illegal combination
    ///          of both, the implementation would return an empty {@link Optional} and preferably log a warning.
    /// @see Component
    protected abstract Optional<Component> buildComponent();
}