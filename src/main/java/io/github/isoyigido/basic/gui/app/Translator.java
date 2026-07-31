package io.github.isoyigido.basic.gui.app;

import io.github.isoyigido.basic.gui.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/// Stores translations. Provides the method {@link #register(String, String...)} to register language JSON files
/// under language codes, and the method {@link #setLanguage(String)} to set the language to the given registered
/// language code. The set translations can be accessed via {@link #get(String)}, which returns the translated text
/// for the given translation key.
/// @see #register(String, String...)
/// @see #setLanguage(String)
/// @see #get(String)
public final class Translator {
    /// Private constructor to prevent instantiation
    private Translator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(Translator.class);

    /// Maps each registered language code to the corresponding translation map.
    private static final Map<String, Map<String, String>> languageRegistry = new HashMap<>(4);

    /// The currently loaded translation map (null indicates that no translation is loaded)
    private static Map<String, String> translations = null;

    /// Loads and flattens the language JSON files at the given resource directory. Registers the flattened
    /// translation maps under the given language codes.
    ///
    /// The names of the JSON files should match the given language codes (e.g., `en.json`, `de.json`).
    ///
    /// **Special cases:**
    /// - Logs a warning and skips the language code if no corresponding language file is found, or if the language file
    ///   cannot be read
    ///
    /// @param directory the path to the directory that contains the language files relative to the resources folder (e.g., `/app/language/`)
    /// @param languageCodes the language codes to be registered (e.g., `en`, `de`)
    /// @throws NullPointerException if the input `directory` or `languageCodes` is null
    public static void register(String directory, String... languageCodes) {
        Objects.requireNonNull(directory, "Path to language directory cannot be null.");
        Objects.requireNonNull(languageCodes, "Array of language codes cannot be null.");

        // If there is no language code to register, return
        if (languageCodes.length == 0) return;

        // If the directory path does not have a leading slash, add it
        // Use startsWith instead of charAt to handle empty path (root)
        if (!directory.startsWith("/")) directory = '/' + directory;

        // If the directory path does not have a trailing slash, add it
        if (directory.charAt(directory.length() - 1) != '/') directory += '/';

        // For each language code to register
        for (String languageCode : languageCodes) {
            // Get the resource path for the language JSON file
            String resourcePath = directory + languageCode + ".json";

            // Read and flatten the language JSON file
            JsonUtils.readFromResources(resourcePath).map(JsonUtils::flatten).ifPresentOrElse(
                    // Register the translations under the given language code
                    translations -> languageRegistry.put(languageCode, translations),
                    // Log warning
                    () -> logger.warn("Unable to find or read language JSON file. path={}", resourcePath)
            );
        }
    }

    /// Sets the language to the given registered language code by updating the loaded translations.
    ///
    /// **Special cases:**
    /// - Logs a warning and does nothing if the given language code is not registered
    ///
    /// @param languageCode the registered language code
    public static void setLanguage(String languageCode) {
        // Get the translations registered under the given language code
        Map<String, String> translations = languageRegistry.get(languageCode);

        // If the given language code is not registered
        if (translations == null) {
            // Log warning
            logger.warn("Language code is not registered. value={}", languageCode);

            // Return
            return;
        }

        // Set the translations
        Translator.translations = translations;
    }

    /// Returns the translated text for the given translation key.
    ///
    /// Example translation key: `main_menu.buttons.settings.label`
    ///
    /// **Special cases:**
    /// - Logs a warning and returns the given translation key if no translation has been set yet,
    ///   or if the corresponding translation is missing
    ///
    /// @param key the translation key
    /// @return the translated text
    /// @throws NullPointerException if the input `key` is null
    public static String get(String key) {
        Objects.requireNonNull(key, "Translation key cannot be null.");

        // If no translation has been set
        if (translations == null) {
            // Log warning
            logger.warn("Cannot access translation because no translation is set. key={}", key);

            // Return the key
            return key;
        }

        // Get the translated text from the translations
        String translatedText = translations.get(key);

        // If the translation map does not contain the given translation key
        if (translatedText == null) {
            // Log warning
            logger.warn("Translation is missing. key={}", key);

            // Return the key
            return key;
        }

        // Return the translated text
        return translatedText;
    }
}