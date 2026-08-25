package io.github.isoyigido.basic.gui.core.loader.parameters.methods;

import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

/// Overrides the {@link #parse(String)} method to treat the given value string as a registered method name
/// and return the corresponding {@link Method} instance using {@link MethodRegistry#get(String)}.
/// @see #parse(String)
/// @see Parameter
/// @see Method
/// @see MethodRegistry
public class MethodParameter extends Parameter<Method> {
    private static final Logger logger = LoggerFactory.getLogger(MethodParameter.class);

    /// Treats the given value string as a registered method name and returns the corresponding {@link Method} instance
    /// using {@link MethodRegistry#get(String)}.
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the given method name is unregistered
    ///
    /// @param valueString the name of the registered method
    /// @return an {@link Optional} containing the registered {@link Method} instance,
    ///         or an empty {@link Optional} if the given method name is unregistered
    @Override
    public Optional<Method> parse(String valueString) {
        // Get an optional containing the method registered under the given name
        Optional<Method> optionalMethod = MethodRegistry.get(valueString.strip());

        // If the given method name is unregistered, log warning
        if (optionalMethod.isEmpty()) MethodParameter.logger.warn("Encountered unregistered method name. name={}", valueString);

        // Return the optional
        return optionalMethod;
    }
}