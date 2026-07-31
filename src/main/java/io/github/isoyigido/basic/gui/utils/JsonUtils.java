package io.github.isoyigido.basic.gui.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/// Stores utility methods for JSON operations.
/// @see #readFromResources(String)
/// @see #flatten(JsonObject)
/// @see #flatten(JsonObject, Function)
public final class JsonUtils {
    /// Private constructor to prevent instantiation
    private JsonUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /// Reads the JSON file at the given path relative to the resources folder,
    /// and parses it into a {@link JsonObject} instance using {@link Gson}.
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if there is no file at the given path
    /// - Logs an error and returns an empty {@link Optional} if an {@link Exception} is caught
    ///
    /// @param path the path to the JSON file relative to the resources folder (`/folder/file.json`)
    /// @return an {@link Optional} containing the parsed {@link JsonObject} instance,
    ///         or an empty {@link Optional} if there is no file at the given path,
    ///         or if an {@link Exception} is caught
    /// @see Gson
    public static Optional<JsonObject> readFromResources(String path) {
        Objects.requireNonNull(path, "Path cannot be null.");

        // If the path is empty, throw an illegal argument exception
        if (path.isEmpty()) throw new IllegalArgumentException("Path cannot be empty.");

        // If the path does not have a leading slash, add it
        if (path.charAt(0) != '/') path = '/' + path;

        // Get the JSON file as an input stream
        try (InputStream is = JsonUtils.class.getResourceAsStream(path)) {
            // If there is no file at the given path, return empty optional
            if (is == null) return Optional.empty();

            // Force the InputStreamReader to read the stream as UTF-8
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                // Initialize a new Gson instance
                Gson gson = new Gson();

                // Use Gson to parse the JSON file and return an optional containing the JsonObject
                return Optional.ofNullable(gson.fromJson(reader, JsonObject.class));
            }

        } catch (Exception e) {
            // Log error
            logger.error("Unable to read JSON file. path={}", path, e);

            // Return empty optional
            return Optional.empty();
        }
    }

    /// Flattens the given {@link JsonObject} into a {@link HashMap} of key-value pairs.
    ///
    /// **Example:**
    /// ```json
    /// {
    ///     "text": {
    ///         "title": "My Application",
    ///         "credit": "by isoyigido"
    ///     },
    ///     "button": {
    ///         "start": "Start",
    ///         "settings": "Settings"
    ///     }
    /// }
    /// ```
    /// The JSON above would be flattened to the following key-value pairs:
    /// ```java
    /// "text.title" -> "My Application"
    /// "text.credit" -> "by isoyigido"
    /// "button.start" -> "Start"
    /// "button.settings" -> "Settings
    /// ```
    /// @param json the nested JSON object
    /// @return the {@link HashMap} containing the key-value pairs
    public static Map<String, String> flatten(JsonObject json) {
        // Flatten the JSON using a parser function that returns the string value
        return flatten(json, value -> value);
    }

    /// Flattens the given {@link JsonObject} into a {@link HashMap} of key-value pairs
    /// using the given parser function to parse string values.
    ///
    /// **Example:**
    /// ```json
    /// {
    ///     "text": {
    ///         "title": "FFFFFF",
    ///         "credit": "A8A8A8"
    ///     },
    ///     "button": {
    ///         "start": "007BFF",
    ///         "settings": "0A9797"
    ///     }
    /// }
    /// ```
    /// If the given parser function parses string representations of colors,
    /// the JSON above would be flattened to the following key-value pairs:
    /// ```java
    /// "text.title" -> new Color(255, 255, 255)
    /// "text.credit" -> new Color(168, 168, 168)
    /// "button.start" -> new Color(0, 123, 255)
    /// "button.settings" -> new Color(10, 151, 151)
    /// ```
    ///
    /// @param json the nested JSON object
    /// @param parser the parser function used for parsing the string values
    /// @param <T> the object type of the stored values
    /// @return the {@link HashMap} containing the key-value pairs
    public static <T> Map<String, T> flatten(JsonObject json, Function<String, T> parser) {
        // Initialize a new map
        Map<String, T> result = new HashMap<>(8);

        // Flatten the JSON recursively
        flatten("", json, parser, result);

        // Return the resulting map
        return result;
    }

    /// Recursively flattens the given {@link JsonObject} into the given map.
    /// @param prefix the prefix for the current scope (e.g., `main_menu.buttons`)
    /// @param json the nested JSON object
    /// @param parser the parser function used for parsing the string values
    /// @param result the map where the flattened key-value pairs are put
    /// @param <T> the object type of the stored values
    private static <T> void flatten(String prefix, JsonObject json, Function<String, T> parser, Map<String, T> result) {
        // For each entry in the current JSON scope
        json.entrySet().forEach(entry ->  {
            // Get the flattened key for the entry
            String key = prefix.isEmpty() ? entry.getKey() : (prefix + '.' + entry.getKey());

            // Get the value
            JsonElement value = entry.getValue();

            // If the value indicates a scope
            if (value.isJsonObject()) {
                // Flatten the scope recursively into the given map
                flatten(key, value.getAsJsonObject(), parser, result);

                // Return
                return;
            }

            // Parse the string value and put the key-value pair into the map
            result.put(key, parser.apply(value.getAsString()));
        });
    }
}