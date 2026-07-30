package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.app.Translator;
import io.github.isoyigido.basic.gui.core.loader.Parameter;

import java.util.Optional;

/// Overrides the {@link #parse(String)} method to parse the given value string:
/// 1. If the value string has a pair of surrounding quotation marks, strips the pair of quotation marks
///    and treats the value string as a literal {@link String} (e.g., `"Current Settings"` -> `Current Settings`).
/// 2. Otherwise, treats the value string as a translation key and uses {@link Translator#get(String)}
///    to get the translation (e.g., `menu.settings.current_settings` -> `Current Settings`).
/// @see #parse(String)
/// @see Parameter
/// @see String
/// @see Translator
public class TextParameter extends Parameter<String> {
    /// Parses the given value string:
    /// 1. If the value string has a pair of surrounding quotation marks, strips the pair of quotation marks
    ///    and treats the value string as a literal {@link String} (e.g., `"Current Settings"` -> `Current Settings`).
    /// 2. Otherwise, treats the value string as a translation key and uses {@link Translator#get(String)}
    ///    to get the translation (e.g., `menu.settings.current_settings` -> `Current Settings`).
    /// @param valueString either the literal {@link String} surrounded by quotation marks or the translation key
    /// @return an {@link Optional} containing either the literal {@link String} or the translated text
    @Override
    public Optional<String> parse(String valueString) {
        // Remove surrounding whitespace
        valueString = valueString.strip();

        // If the value string has a pair of surrounding quotation marks
        if (valueString.startsWith("\"") && valueString.endsWith("\"")) {
            // Strip the quotation marks and return an optional containing the literal string
            return Optional.of(valueString.substring(1, valueString.length() - 1));
        }

        // Value string represents a translation key -> return an optional containing the translated text
        return Optional.of(Translator.get(valueString));
    }
}