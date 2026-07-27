package io.github.isoyigido.basic.gui.core.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/// Stores lists of required and optional parameters as {@link Parameter} instances.
/// Provides the {@link #setParameterValue(String, String)} method to parse the given {@link String}
/// representation of a value and set the value of the parameter tied to the given key.
/// After setting parameter values, the {@link #buildNewObject()} method may be used to build
/// a new object using the stored parameter values. It returns an {@link Optional} containing
/// the built object, or an empty {@link Optional} if any required parameter does not have
/// a set value, or if the provided set of parameter values cannot be used to construct an object.
///
/// Subclasses of this class must add required and optional parameters in a parameterless constructor
/// and override the {@link #build()} method to provide the object building logic using the added
/// required and optional parameters.
///
/// Required parameters may be added using {@link #addRequiredParameter(String, Parameter)}, and
/// optional parameters may be added using {@link #addOptionalParameter(String, Parameter)}.
///
/// Required parameters must have a set value. The {@link #buildNewObject()} method ensures that
/// every required parameter has a set value before building the object, and returns an empty
/// {@link Optional} if any required parameter does not have a set value. Implementations of
/// {@link #build()} can safely use {@link Parameter#get()} on a required parameter without checking
/// if the parameter has a set value.
///
/// Optional parameters may not have a set value. Implementations of {@link #build()} must use
/// {@link Parameter#getOptional()} on optional parameters to get an {@link Optional} of the value,
/// or check whether the optional parameter has a value using {@link Parameter#isPresent()} before
/// using {@link Parameter#get()}. Using {@link Parameter#get()} on a parameter that does not have
/// a set value causes an {@link UnsupportedOperationException} to be thrown.
///
/// @see Parameter
abstract class Builder<T> {
    private static final Logger logger = LoggerFactory.getLogger(Builder.class);

    /// Maps keys to required parameters.
    private final Map<String, Parameter<?>> requiredParameters = new LinkedHashMap<>(4);

    /// Maps keys to optional parameters.
    private final Map<String, Parameter<?>> optionalParameters = new LinkedHashMap<>(4);

    /// Adds a required parameter. The parameter key can consist of lowercase letters, uppercase letters,
    /// digits, underscores (`_`), and hyphens (`-`).
    /// @param key the key for the parameter
    /// @param parameter the parameter that is effectively tied to the key
    /// @apiNote Required parameters must have a set value. The {@link #buildNewObject()} method ensures that
    ///          every required parameter has a set value before building the object, and returns
    ///          an empty {@link Optional} if any required parameter does not have a set value.
    ///          Implementations of {@link #build()} can safely use {@link Parameter#get()}
    ///          on a required parameter without checking if the parameter has a set value.
    /// @see Parameter
    protected void addRequiredParameter(String key, Parameter<?> parameter) {
        // Map the given key to the given required parameter
        this.requiredParameters.put(key, parameter);
    }

    /// Adds an optional parameter. The parameter key can consist of lowercase letters, uppercase letters,
    /// digits, underscores (`_`), and hyphens (`-`).
    /// @param key the key for the parameter
    /// @param parameter the parameter that is effectively tied to the key
    /// @apiNote Optional parameters may not have a set value. Implementations of {@link #build()}
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
    /// - Logs a warning and does nothing if the input `key` is not tied to any parameter
    ///
    /// @param key the key for the parameter
    /// @param valueString the {@link String} representation of the value
    /// @see Parameter
    protected void setParameterValue(String key, String valueString) {
        // Find whether the given parameter key is required or optional
        boolean required = this.requiredParameters.containsKey(key);
        boolean optional = this.optionalParameters.containsKey(key);

        // If the given parameter key is neither required nor optional (no parameter with that key)
        if (!required && !optional) {
            // Log warning
            Builder.logger.warn("Encountered unknown parameter. Skipping it. class={} key={}", this.getClass().getSimpleName(), key);

            // Return
            return;
        }

        // Parse the value string and set the value of the parameter with the given key
        if (required) this.requiredParameters.get(key).parseAndSet(valueString);
        if (optional) this.optionalParameters.get(key).parseAndSet(valueString);
    }

    /// Builds an object using the stored parameter values.
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if any required parameter does not have a set value
    /// - Returns an empty {@link Optional} if {@link #build()} returns an empty {@link Optional}
    ///
    /// @return an {@link Optional} containing the built object,
    ///         or an empty {@link Optional} if any required parameter does not have a set value,
    ///         or if the provided set of parameter values cannot be used to construct an object
    public Optional<T> buildNewObject() {
        // Check each required parameter
        for (Map.Entry<String, Parameter<?>> entry : this.requiredParameters.entrySet()) {
            // If the parameter has no set value
            if (entry.getValue().isEmpty()) {
                // Log warning
                Builder.logger.warn("Required parameter is missing. key={}", entry.getKey());

                // Return empty optional
                return Optional.empty();
            }
        }

        // Build and return the object
        return this.build();
    }

    /// Override this method to provide the building logic using the stored parameters.
    ///
    /// This method is called in {@link #buildNewObject()} after verifying that each required parameter
    /// has a set value.
    ///
    /// If the implementation of this method returns an empty {@link Optional}, the {@link #buildNewObject()}
    /// method returns an empty {@link Optional} as well.
    protected abstract Optional<T> build();
}