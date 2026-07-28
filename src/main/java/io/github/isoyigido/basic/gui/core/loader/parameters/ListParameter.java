package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/// Represents a parameter that holds a {@linkplain List} of values. Gets the parser function used for parsing individual elements
/// as a parameter in the constructor. Provides the static factory method {@link #of(Parameter)} to create a new
/// `ListParameter` instance that uses the parser method of the given {@link Parameter} instance as its parser function.
/// Overrides the {@link #parse(String)} method to split the given value string by newline characters and parse the
/// individual elements into a {@linkplain List} using the stored parser function.
/// @param <T> the object type of the values in the {@linkplain List}
/// @see #parse(String)
/// @see Parameter
/// @see List
public class ListParameter<T> extends Parameter<List<T>> {
    private static final Logger logger = LoggerFactory.getLogger(ListParameter.class);

    /// The parser function used for parsing individual elements
    private final Function<String, Optional<T>> parser;

    /// Constructs a list parameter that uses the given parser function to parse individual elements.
    /// @param parser the parser function used for parsing individual elements
    public ListParameter(Function<String, Optional<T>> parser) {
        // Set the parser function
        this.parser = parser;
    }

    /// Removes a single pair of surrounding brackets from the given {@link String} representation of a list,
    /// and splits it by newline characters to get the individual elements. Uses the stored parser function to parse
    /// the individual elements into a {@linkplain List}.
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the given {@link String} representation does not have
    ///   a pair of surrounding brackets
    /// - Logs a warning and skips any element for which the parser function returns an empty {@link Optional}
    ///
    /// @param valueString the {@link String} representation of the list
    /// @return an {@link Optional} containing the {@linkplain List} of parsed values,
    ///         or an empty {@link Optional} if the given {@link String} representation does not have a pair of surrounding brackets
    @Override
    public Optional<List<T>> parse(String valueString) {
        // Remove surrounding whitespace
        valueString = valueString.strip();

        // If the value string does not have a pair of surrounding brackets
        if (!valueString.startsWith("[") || !valueString.endsWith("]")) {
            // Log warning
            ListParameter.logger.warn("List parameter is missing surrounding brackets. value={}", valueString);

            // Return empty optional
            return Optional.empty();
        }

        // Remove the pair of surrounding brackets, then remove surrounding whitespace
        valueString = valueString.substring(1, valueString.length() - 1).strip();

        // Split the value string by newline characters to get the individual elements
        String[] elements = valueString.split("\n");

        // Initialize the list of parsed values
        final List<T> list = new ArrayList<>(elements.length);

        // Remove surrounding whitespace and apply the parser function to each individual element
        for (String element : elements) this.parser.apply(element.strip()).ifPresentOrElse(
                // Add the parsed value to the list
                list::add,
                // Log warning
                () -> ListParameter.logger.warn("Element has invalid value. value={}", element)
        );

        // Return an optional containing the list
        return Optional.of(list);
    }

    /// Returns a new `ListParameter` instance that uses the parser method of the given {@link Parameter} instance
    /// as its parser function.
    /// @param parameter the {@link Parameter} instance whose parser method is used for parsing individual elements
    /// @return a new `ListParameter` instance that uses the parser method of the given {@link Parameter} instance
    ///         as its parser function
    /// @param <T> the object type of the values in the {@linkplain List}
    public static <T> ListParameter<T> of(Parameter<T> parameter) {
        // Return a new ListParameter instance that uses the parser method of the given parameter
        return new ListParameter<>(parameter::parse);
    }
}