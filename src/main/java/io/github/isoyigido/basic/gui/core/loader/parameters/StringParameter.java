package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.loader.Parameter;

import java.util.Objects;
import java.util.Optional;

/// Overrides the {@link #parse(String)} method to remove a single pair of surrounding quotation marks (if present)
/// from the given {@link String} value using the public static method {@link #stripQuotationMarks(String)}.
///
/// If the text has a pair of surrounding quotation marks, after the pair is removed, the resulting text is
/// **NOT** stripped of surrounding whitespace. Example usage is shown below:
/// - <pre>`  Example Text  ` -> `Example Text` (whitespace removed)</pre>
/// - <pre>`"  Example Text  "` -> `  Example Text  ` (whitespace kept)</pre>
///
/// If the text has multiple pairs of surrounding quotation marks, only one is removed. Example is shown below:
/// - <pre>`"""  Example Text  """` -> `""  Example Text  ""`</pre>
///
/// Except for the removal of the surrounding whitespace and the pair of surrounding quotation marks, the given
/// value {@link String} is not altered/formatted in any way and is simply wrapped in an {@link Optional}.
///
/// @see #parse(String) 
/// @see #stripQuotationMarks(String)
/// @see Parameter
public class StringParameter extends Parameter<String> {
    /// Removes surrounding whitespace, and a single pair of surrounding quotation marks (if present) from the text.
    /// @param valueString the text value (e.g., `Example Text` or `"Example Text"`)
    /// @return an {@link Optional} containing the stripped text
    @Override
    public Optional<String> parse(String valueString) {
        // Strip quotation marks and return an optional containing the stripped text
        return Optional.of(StringParameter.stripQuotationMarks(valueString));
    }

    /// Removes surrounding whitespace, and a single pair of surrounding quotation marks (if present) from the text.
    /// @param text the original text
    /// @return the stripped text
    /// @throws NullPointerException if the input `text` is null
    public static String stripQuotationMarks(String text) {
        Objects.requireNonNull(text, "Text to strip of quotation marks cannot be null.");

        // Remove surrounding whitespace
        text = text.strip();

        // If the text has a single pair of surrounding quotation marks, remove them
        return (text.startsWith("\"") && text.endsWith("\""))
                ? text.substring(1, text.length() - 1)
                : text;
    }
}