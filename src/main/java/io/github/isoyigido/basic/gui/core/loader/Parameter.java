package io.github.isoyigido.basic.gui.core.loader;

import java.util.Objects;
import java.util.Optional;

/// Represents a parameter in a GUI file. The generic value of the parameter should
/// be parsable from a {@link String} representation. The parsing logic must be provided
/// by the subclasses of this class by overriding the {@link #parse(String)} method.
///
/// The {@link #parseAndSet(String)} should be used to set the value of a parameter
/// from a {@link String} representation in a GUI file. Additionally, a value of generic type
/// may be set directly with the {@link #set(Object)} method. To check whether a parameter
/// has a set value, {@link #isPresent()} and {@link #isEmpty()} methods are provided.
/// The {@link #get()} method throws an {@link UnsupportedOperationException} if the parameter does not
/// have a set value, and should only be used when it is known that the parameter has a set value.
/// When it is not known whether a parameter has a set value, the {@link #getOptional()} method may be used
/// to obtain an {@link Optional} containing the value of the parameter, or an empty {@link Optional}
/// if the parameter does not have a set value.
///
/// **Example Usage:**```java
/// public class ColorParameter extends Parameter<Color> {
///     @Override
///     public Optional<Color> parse(String valueString) {
///         // ... parse color hex (e.g., #52CBFF) or RGB values
///         return Optional.of(new Color(r, g, b));
///     }
/// }
/// ```
///
/// @apiNote This abstract class is intended to make defining custom parameter types easier.
///          By providing flexible parsing logic, the user may implement multiple ways to define a custom parameter.
///          For example, a color may be parsed from both a hex value and integer RGB values.
///          Further functionality may be added in the subclasses, like minimum and maximum values
///          for an integer parameter. The {@link #parse(String)} method implementation should log a warning
///          and return an empty {@link Optional} if the input value is invalid in any way (e.g., out-of-bounds integer).
/// @param <T> the object type of the value contained in this parameter
/// @see WidgetBuilder
/// @see GUILoader
public abstract class Parameter<T> {
    /// The value of this parameter (null indicates that no value is set)
    protected T value = null;

    /// Returns the non-null value of this parameter.
    /// @return the non-null value of this parameter
    /// @throws UnsupportedOperationException if this parameter does not have a set value
    public T get() throws UnsupportedOperationException {
        // If no value has been set for this parameter, throw an unsupported operation exception
        if (this.value == null) throw new UnsupportedOperationException("Parameter does not have a set value.");

        // Return the value of this parameter
        return this.value;
    }

    /// Returns an {@link Optional} containing the non-null value of this parameter,
    /// or an empty {@link Optional} if this parameter does not have a set value.
    /// @return an {@link Optional} containing the non-null value of this parameter,
    ///         or an empty {@link Optional} if this parameter does not have a set value
    /// @see Optional
    public Optional<T> getOptional() {
        // Return an optional containing the set value
        return Optional.ofNullable(this.value);
    }

    /// Sets the value of this parameter.
    /// @param value the new non-null value of this parameter
    /// @return this
    /// @throws NullPointerException if the input `value` is null
    /// @apiNote This method sets the value of this parameter to the given literal non-null value.
    ///          To remove the set value of this parameter, {@link #removeValue()} must be used
    ///          instead of `parameter.set(null)`. Additionally, to set the value of this parameter
    ///          based on a {@link String} representation (e.g., in a GUI file), {@link #parseAndSet(String)}
    ///          should be used to parse the {@link String} representation and set the value.
    public Parameter<T> set(T value) {
        Objects.requireNonNull(value, "The value to set cannot be null. Use removeValue() to remove the set value.");

        // Set the value of this parameter
        this.value = value;

        // Return this
        return this;
    }

    /// Removes the set value of this parameter.
    /// @return this
    public Parameter<T> removeValue() {
        // Set the value of this parameter to null
        this.value = null;

        // Return this
        return this;
    }

    /// Returns whether this parameter has a set value.
    /// @return whether this parameter has a set value
    public boolean isPresent() {
        // Return whether the value of this parameter is not null
        return this.value != null;
    }

    /// Returns whether this parameter does not have a set value.
    /// @return whether this parameter does not have a set value
    public boolean isEmpty() {
        // Return whether the value of this parameter is null
        return this.value == null;
    }

    /// Parses the given {@link String} representation and returns the corresponding generic parameter value.
    ///
    /// Override this method to provide parsing logic.
    ///
    /// @param valueString the {@link String} representation of the value
    /// @return an {@link Optional} containing the value of generic type,
    ///         or an empty {@link Optional} if the given {@link String} representation is invalid in any way
    /// @implSpec Implementations of this method should log a warning and return an empty {@link Optional}
    ///           if the input `valueString` is invalid in any way.
    public abstract Optional<T> parse(String valueString);

    /// Parses the given {@link String} representation and sets the parsed value as the new value of this parameter.
    ///
    /// **Special cases:**
    /// - Does not set the value of this parameter if the given {@link String} representation is invalid
    ///
    /// @param valueString the {@link String} representation of the value
    /// @return this
    /// @implNote This method uses {@link #parse(String)} to parse the {@link String} representation,
    ///           and if the returned {@link Optional} contains a value, sets it as the new value of
    ///           this parameter using {@link #set(Object)}.
    public Parameter<T> parseAndSet(String valueString) {
        // Parse the value string and set the value if it is valid
        this.parse(valueString).ifPresent(this::set);

        // Return this
        return this;
    }
}