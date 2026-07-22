package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/// Defines the contract for a parameter which contains comparable numbers. Gets the parser function for
/// the specific type of number (e.g., {@link Integer#parseInt(String)} or {@link Float#parseFloat(String)})
/// as a parameter in the constructor. Provides the methods {@link #setMinimumValue(Number)},
/// {@link #setMaximumValue(Number)}, and {@link #setBounds(Number, Number)} for setting
/// minimum and maximum values for the stored value.
///
/// Overrides the {@link #parse(String)} method to apply the parser function on the {@link String} representation.
/// If the parser function returns null, or if any {@link Exception} is thrown by the parser function,
/// the {@link #parse(String)} implementation returns an empty {@link Optional}. If the value returned from
/// the parser function is less than the set minimum value or greater than the set maximum value,
/// the implementation logs a warning and returns an empty {@link Optional}. Otherwise, an {@link Optional}
/// containing the value returned from the parser function is returned.
///
/// @param <T> the object type of the {@link Number} contained in this parameter
/// @see #parse(String)
/// @see Parameter
/// @see Number
/// @see Comparable
public class NumberParameter<T extends Number & Comparable<T>> extends Parameter<T>{
    private static final Logger logger = LoggerFactory.getLogger(NumberParameter.class);

    /// The parser function for parsing the {@link String} representation of numbers
    private final Function<String, T> parser;

    /// The minimum allowed value (inclusive, null for no minimum value)
    private T minimumValue = null;

    /// The maximum allowed value (inclusive, null for no maximum value)
    private T maximumValue = null;

    /// Constructs a number parameter that uses the given parser function to parse
    /// the {@link String} representation of numbers. The parser function should return null
    /// or throw an {@link Exception} when the {@link String} representation is invalid.
    /// @param parser the parser function
    /// @throws NullPointerException if the input `parser` is null
    public NumberParameter(Function<String, T> parser) {
        Objects.requireNonNull(parser, "Parser function cannot be null.");

        // Set the parser function
        this.parser = parser;
    }

    /// Sets the minimum allowed value for this parameter.
    /// @param minimumValue the minimum allowed value (inclusive)
    /// @return this
    public NumberParameter<T> setMinimumValue(T minimumValue) {
        // Set the minimum allowed value
        this.minimumValue = minimumValue;

        // Return this
        return this;
    }

    /// Sets the maximum allowed value for this parameter.
    /// @param maximumValue the maximum allowed value (inclusive)
    /// @return this
    public NumberParameter<T> setMaximumValue(T maximumValue) {
        // Set the maximum allowed value
        this.maximumValue = maximumValue;

        // Return this
        return this;
    }

    /// Sets the minimum and maximum allowed values for this parameter.
    /// @param minimumValue the minimum allowed value (inclusive)
    /// @param maximumValue the maximum allowed value (inclusive)
    /// @return this
    public NumberParameter<T> setBounds(T minimumValue, T maximumValue) {
        // Set the minimum and maximum allowed values
        this.setMinimumValue(minimumValue);
        this.setMaximumValue(maximumValue);

        // Return this
        return this;
    }

    /// Parses the given {@link String} representation of a number using the stored parser function.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if the parser function returns null or throws an {@link Exception}
    ///
    /// @param valueString the {@link String} representation of the number
    /// @return an {@link Optional} containing the parsed numerical value,
    ///         or an empty {@link Optional} if the parser function returns null or throws an {@link Exception}
    @Override
    public Optional<T> parse(String valueString) {
        try {
            // Apply the parser function on the value String
            T value = this.parser.apply(valueString.strip());

            // If the returned value is null, return empty optional
            if (value == null) return Optional.empty();

            // If there is a set minimum value and the returned value is less than the set minimum value
            if ((this.minimumValue != null) && ((value.compareTo(this.minimumValue)) < 0)) {
                // Log warning
                NumberParameter.logger.warn("Number parameter value is less than the minimum value. value={}", value);

                // Return empty optional
                return Optional.empty();
            }

            // If there is a set maximum value and the returned value is greater than the set maximum value
            if ((this.maximumValue != null) && ((value.compareTo(this.maximumValue)) > 0)) {
                // Log warning
                NumberParameter.logger.warn("Number parameter value is greater than the maximum value. value={}", value);

                // Return empty optional
                return Optional.empty();
            }

            // Return an optional containing the returned value
            return Optional.of(value);

        } catch (Exception _) {
            // Log warning
            NumberParameter.logger.warn("Number parameter has invalid value. value={}", valueString);

            // Return empty optional
            return Optional.empty();
        }
    }
}