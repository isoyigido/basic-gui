package io.github.isoyigido.basic.gui.app;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.isoyigido.basic.gui.main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// Stores translations. The language can be changed using {@link #setLanguage(String, String)} to load a JSON file containing translations. The stored translations can be accessed using {@link #get(String)}.
public final class Translator {
    /// Private constructor to prevent instantiation
    private Translator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(Translator.class);

    /// The language code of the currently loaded language (e.g., `en`)
    private static String languageCode = null;

    /// The translation map containing the key-value pairs for the translations
    private static Map<String, String> translationMap = null;

    /// Returns the translated text for the given translation key.
    ///
    /// Example translation key: `main_menu.buttons.settings.label`
    ///
    /// **Special cases:**
    /// - Returns the input `key` if no translation has been loaded
    /// - Returns the input `key` if the translations do not contain it
    ///
    /// @param key the translation key
    /// @return the translated text
    public static String get(String key) {
        // If no translation has been loaded, return the key
        if (translationMap == null) return key;

        // If the translation map contains the key, return the value
        if (translationMap.containsKey(key)) return translationMap.get(key);

        // Log warning
        logger.warn("Translation is missing. lang={} key={}", languageCode, key);

        // Return the key
        return key;
    }

    /// Sets the language based on the given language code.
    /// @param directory the relative path to the directory that holds the language files in the resources folder (e.g., `/app/language`)
    /// @param languageCode the language code (e.g., `en`)
    public static void setLanguage(String directory, String languageCode) {
        // Load the translations for the given language code
        loadTranslations(directory, languageCode).ifPresent(translations -> {
            // Set the language code
            Translator.languageCode = languageCode;

            // Update the translation map
            Translator.translationMap = flatten(translations);
        });
    }

    /// Loads the translations for the given language code.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if an {@link IOException} is caught
    ///
    /// @param directory the relative path to the directory that holds the language files in the resources folder (e.g., `/app/language`)
    /// @param languageCode the language code (e.g., `en`)
    /// @return an {@link Optional} containing the {@link JsonObject} of the translations,
    ///         or an empty {@link Optional} if an {@link IOException} is caught
    private static Optional<JsonObject> loadTranslations(String directory, String languageCode) {
        // Get the path inside the resources folder
        String resourcePath = directory + '/' + languageCode + ".json";

        // Get the language file as an input stream
        try (InputStream is = Main.class.getResourceAsStream(resourcePath)) {
            // If the language file cannot be found
            if (is == null) {
                // Log error
                logger.error("Cannot find language file. path={}", resourcePath);

                // Return empty optional
                return Optional.empty();
            }

            // Force the InputStreamReader to read the stream as UTF-8
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                // Use Gson to parse the JSON file
                Gson gson = new Gson();

                // Return an optional containing the JsonObject
                return Optional.of(gson.fromJson(reader, JsonObject.class));

            }
        } catch (IOException e) {
            // Log error
            logger.error("Unable to read language file. path={}", resourcePath, e);

            // Return empty optional
            return Optional.empty();
        }
    }

    /// Recursively flattens the given {@link JsonObject} into a {@link HashMap} of key-value pairs.
    /// @param json the nested JSON object
    /// @return the {@link HashMap} containing the JSON data
    private static Map<String, String> flatten(JsonObject json) {
        // Initialize the hash map
        Map<String, String> result = new HashMap<>(256);

        // Flatten the JSON recursively
        flatten("", json, result);

        // Return the resulting map
        return result;
    }

    /// Recursively flattens the given {@link JsonObject} into the given map.
    /// @param prefix the prefix for the current scope (e.g., `main_menu.buttons`)
    /// @param json the nested JSON object
    /// @param result the map where the flattened key-value pairs are put
    private static void flatten(String prefix, JsonObject json, Map<String, String> result) {
        // For each entry in the current JSON scope
        json.entrySet().forEach(entry ->  {
            // Get flattened key for the entry
            String key = prefix.isEmpty() ? entry.getKey() : (prefix + '.' + entry.getKey());
            // Get the value
            JsonElement value = entry.getValue();

            // If the value indicates a scope
            if (value.isJsonObject()) {
                // Flatten the scope recursively into the given map
                flatten(key, value.getAsJsonObject(), result);
            }
            // Else if the value is a primitive
            else if (value.isJsonPrimitive()) {
                // Put the value string to the map
                result.put(key, value.getAsString());
            }
        });
    }
}