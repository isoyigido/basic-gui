package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/// Overrides the {@link #parse(String)} method to parse the {@link String} representation of a boolean value.
/// @see #parse(String)
/// @see Parameter
/// @see Boolean
public class BooleanParameter extends Parameter<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(BooleanParameter.class);

    /// Parses the given {@link String} representation of a boolean value.
    ///
    /// **Recognized values:** `true`, `false` (case-insensitive)
    ///
    /// **Special cases:**
    /// - Logs a warning and returns an empty {@link Optional} if the given {@link String} representation is unrecognized
    ///
    /// @param valueString the {@link String} representation of the boolean value
    /// @return an {@link Optional} containing the boolean value,
    ///         or an empty {@link Optional} if the given {@link String} representation is unrecognized
    @Override
    public Optional<Boolean> parse(String valueString) {
        // Remove surrounding whitespace and make all letters lowercase
        valueString = valueString.strip().toLowerCase();

        // If the lowercase value string is "true", return true
        if ("true".equals(valueString)) return Optional.of(true);

        // If the lowercase value string is "false", return false
        if ("false".equals(valueString)) return Optional.of(false);

        // - Invalid value -
        // Log warning
        BooleanParameter.logger.warn("Boolean parameter has unrecognized value. value={}", valueString);

        // Return empty optional
        return Optional.empty();
    }
}