package io.github.isoyigido.basic.gui.core;

import io.github.isoyigido.basic.gui.core.loader.GUILoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/// This registry class holds suppliers for {@link GUI} instances. The registry {@linkplain Map} maps each registered GUI name
/// to the corresponding {@link GUI} instance supplier. GUI suppliers can be registered using {@link #register(String, Supplier)},
/// {@link #register(String, String)}, and {@link #register(String)}. New {@link GUI} instances can be obtained using {@link #get(String)},
/// which returns the corresponding new instance for the given registered GUI name.
/// @see GUI
/// @see GUIManager
/// @see GUILoader
public final class GUIRegistry {
    /// Private constructor to prevent instantiation
    private GUIRegistry() {
        throw new UnsupportedOperationException("Registry class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(GUIRegistry.class);

    /// This registry maps each registered GUI name to its {@link GUI} instance supplier.
    private static final Map<String, Supplier<GUI>> registry = new HashMap<>(8);

    /// Registers the given GUI supplier under the given name.
    /// @param name the GUI name to be registered
    /// @param supplier the GUI supplier to be registered
    /// @throws NullPointerException if the input `name` or `supplier` is null
    public static void register(String name, Supplier<GUI> supplier) {
        Objects.requireNonNull(name, "GUI name to register cannot be null.");
        Objects.requireNonNull(supplier, "GUI supplier to register cannot be null.");

        // Put the name-supplier pair in the map
        registry.put(name, supplier);
    }

    /// Loads the given GUI file using {@link GUILoader#load(String)} and registers the loaded GUI supplier
    /// under the given name using {@link #register(String, Supplier)}.
    ///
    /// **Special cases:**
    /// - Logs a warning and does nothing if {@link GUILoader#load(String)} returns an empty {@link Optional}
    /// 
    /// @param name the GUI name to be registered
    /// @param path the path to the GUI file relative to the resources folder (e.g., `/gui/menu.gui`)
    /// @throws NullPointerException if the input `name` or `path` is null
    /// @see #register(String, Supplier)
    public static void register(String name, String path) {
        Objects.requireNonNull(name, "GUI name to register cannot be null.");
        Objects.requireNonNull(path, "Path to GUI file to register cannot be null.");

        // Load the GUI file at the given path
        GUILoader.load(path).ifPresentOrElse(
                // Register the loaded GUI supplier
                supplier -> GUIRegistry.register(name, supplier),
                // Log warning
                () -> logger.warn("Unable to register GUI file. path={}", path)
        );
    }

    /// Uses {@link #register(String, String)} to load and register the GUI file at the given path
    /// under its file name without the extension (e.g., `/gui/menu.gui` is registered under `menu`).
    /// @param path the path to the GUI file relative to the resources folder (e.g., `/gui/menu.gui`)
    /// @throws NullPointerException if the input `path` is null
    /// @see #register(String, String) 
    public static void register(String path) {
        Objects.requireNonNull(path, "Path to GUI file to register cannot be null.");

        // Get the index of the last slash and dot
        int lastSlash = path.lastIndexOf('/');
        int lastDot = path.lastIndexOf('.');

        // If the last dot appears before the last slash (no extension),
        // set the last dot index to the length of the path
        if (lastDot < lastSlash) lastDot = path.length();

        // Take the substring of the path to get the file name without the extension
        // and register the GUI file
        register(path.substring(lastSlash + 1, lastDot), path);
    }

    /// Returns an {@link Optional} containing a new {@link GUI} instance based on the given registered GUI name,
    /// or an empty {@link Optional} if the given GUI name is not registered.
    /// @param name the registered GUI name
    /// @return an {@link Optional} containing a new {@link GUI} instance based on the given registered GUI name,
    ///         or an empty {@link Optional} if the given GUI name is not registered
    /// @see GUI
    /// @see Optional
    public static Optional<GUI> get(String name) {
        // Get the supplier from the map and return the GUI from the supplier
        return Optional.ofNullable(registry.get(name)).map(Supplier::get);
    }
}