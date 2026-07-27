package io.github.isoyigido.basic.gui.core.loader;

import io.github.isoyigido.basic.gui.core.Component;

import java.util.Map;
import java.util.Optional;

/// Represents a {@link Builder} for {@link Component} objects. Subclasses of this class must add required
/// and optional parameters in a parameterless constructor and override the {@link #build()} method to provide
/// the component building logic using the added required and optional parameters. A component declaration
/// in a GUI file which is missing any required parameter will be ignored.
///
/// Required parameters may be added using {@link #addRequiredParameter(String, Parameter)}, and
/// optional parameters may be added using {@link #addOptionalParameter(String, Parameter)}.
/// Adding parameters using these methods effectively ties the given key in the GUI file to the given parameter.
/// For example, if `addRequiredParameter("color", colorParameter)` is used, and {@link GUILoader} encounters
/// the field `color` in the component declaration, the corresponding value will be parsed and set for `colorParameter`
/// using {@link #setParameterValue(String, String)}.
///
/// To allow usage in {@link GUILoader}, subclasses of this class must be annotated with {@link RegisterComponentBuilder}.
/// Subclasses of this class annotated with {@link RegisterComponentBuilder} are registered statically in {@link ComponentBuilderRegistry}.
///
/// **Example usage:** \
/// In the example below, components of type `button` in the GUI file will use a new instance of `ButtonBuilder` for creation.
/// The button declaration must include `width`, `height`, and `color` parameters; while it could also include a `label` parameter.
/// ```java
/// @RegisterComponentBuilder(type = "button")
/// public class ButtonBuilder extends ComponentBuilder {
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
///     protected Optional<Component> build() {
///         // - Get required parameter values -
///         int width = this.width.get();
///         int height = this.height.get();
///         Color color = this.color.get();
///
///         // Component building logic with optional parameters...
///         Button button = new Button(width, height, color);
///         this.label.getOptional().ifPresent(button::setLabel);
///
///         // Return an optional of the component
///         return Optional.of(button);
///     }
/// }
/// ```
///
/// @apiNote Multiple ComponentBuilder implementations may exist for a single {@link Component} type.
///          For example, a component builder of type `img-path` and a component builder of type `img-resource` could
///          both build the same type of image component, while one could load the image from the given path and
///          the other from the given path relative to the resources folder. Furthermore, a single ComponentBuilder
///          implementation may allow for multiple ways to declare the component by making use of optional parameters.
///          For example, a component builder of type `image` may have optional `path` and `resource` parameters,
///          and load the image based on whichever one the user uses in the component  declaration. In the case
///          where the user uses neither, or an illegal combination of both, the {@link #build()} implementation
///          would return an empty {@link Optional} and preferably log a warning. When the {@link #build()} method
///          returns an empty {@link Optional}, the component declaration is ignored.
/// @see Builder
/// @see Parameter
/// @see RegisterComponentBuilder
/// @see ComponentBuilderRegistry
/// @see GUILoader
/// @see Component
public abstract class ComponentBuilder extends Builder<Component> {
    /// The map of other components (null indicates that no map is set)
    private Map<String, Component> otherComponents = null;

    /// Sets the map of other components.
    /// @param otherComponents the map of other components
    /// @return this
    /// @apiNote This method is called in {@link GUILoader}. Setting the map of other components
    ///          enables parameter implementations to access other declared components.
    public ComponentBuilder setOtherComponents(Map<String, Component> otherComponents) {
        // Set the map of other components
        this.otherComponents = otherComponents;

        // Return this
        return this;
    }

    /// Returns an {@link Optional} containing the map of other components,
    /// or an empty {@link Optional} if no map is set.
    /// @return an {@link Optional} containing the map of other components,
    ///         or an empty {@link Optional} if no map is set
    public Optional<Map<String, Component>> getOtherComponents() {
        // Return an optional of the map of other components
        return Optional.ofNullable(this.otherComponents);
    }
}