package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.Component;
import io.github.isoyigido.basic.gui.core.loader.ComponentBuilder;
import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/// Overrides the {@link #parse(String)} method to get the declared component with the given name.
///
/// @see #parse(String)
/// @see Parameter
/// @see Component
public class ComponentParameter extends Parameter<Component> {
    private static final Logger logger = LoggerFactory.getLogger(ComponentParameter.class);

    /// The {@link ComponentBuilder} instance that holds this parameter
    private final ComponentBuilder componentBuilder;

    /// Constructs a component parameter.
    /// @param componentBuilder the {@link ComponentBuilder} instance that holds this parameter
    public ComponentParameter(ComponentBuilder componentBuilder) {
        // Set the component builder instance
        this.componentBuilder = componentBuilder;
    }

    /// Uses {@link ComponentBuilder#getOtherComponents()} to get the map of other components from the stored
    /// {@link ComponentBuilder} instance, and returns the declared component with the given name.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if the stored {@link ComponentBuilder} instance is missing the map of other components,
    ///   or if no component has been declared with the given name
    ///
    /// @param valueString the name of the declared component
    /// @return an {@link Optional} containing the declared component with the given name,
    ///         or an empty {@link Optional} if the stored {@link ComponentBuilder} instance is missing the map of other components,
    ///         or if no component has been declared with the given name
    @Override
    public Optional<Component> parse(String valueString) {
        // Get the map of other components from the stored component builder instance
        Optional<Map<String, Component>> optionalComponentMap = this.componentBuilder.getOtherComponents();

        // If the stored component builder instance is missing the map of other components
        if (optionalComponentMap.isEmpty()) {
            // Log warning
            ComponentParameter.logger.warn("Component builder is missing the map of other components.");

            // Return empty optional
            return Optional.empty();
        }

        // Remove surrounding whitespace
        String name = valueString.strip();

        // Get the component with the given name from the map of other components
        Component component = optionalComponentMap.get().get(name);

        // If there is no declared component with the given name, log warning
        if (component == null) ComponentParameter.logger.warn("Component cannot be found. name={}", name);

        // Return an optional of the component
        return Optional.ofNullable(component);
    }
}