package io.github.isoyigido.basic.gui.core.loader.parameters.methods;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/// This registry class statically registers {@link Method} instances for methods annotated with {@link RegisterMethod}
/// into a {@linkplain Map} using {@link ClassGraph}. The {@linkplain Map} maps each method name to the corresponding
/// {@link Method} instance based on the annotation parameter {@link RegisterMethod#name()}. The {@link Method} instances
/// can be accessed using {@link #get(String)}, which returns the corresponding {@link Method} instance for the given method name.
/// @see #get(String)
/// @see RegisterMethod
/// @see Method
/// @see ClassGraph
public final class MethodRegistry {
    /// Private constructor to prevent instantiation
    private MethodRegistry() {
        throw new UnsupportedOperationException("Registry class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(MethodRegistry.class);

    /// This registry maps each method name to the corresponding {@link Method} instance.
    private static final Map<String, Method> registry = new HashMap<>(8);

    static {
        // Use classgraph to scan for methods with annotations
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
            // Iterate over every method annotated with RegisterMethod
            scanResult.getClassesWithMethodAnnotation(RegisterMethod.class).forEach(classInfo -> classInfo.getDeclaredMethodInfo().forEach(methodInfo -> {
                if (!methodInfo.hasAnnotation(RegisterMethod.class)) return;

                try {
                    // Get the method instance
                    Method method = methodInfo.loadClassAndGetMethod();

                    // Make the method accessible
                    method.setAccessible(true);

                    // Get the RegisterMethod annotation and the annotated name
                    RegisterMethod annotation = method.getAnnotation(RegisterMethod.class);
                    String name = annotation.name();

                    // Put the name and the method in the registry map
                    boolean existing = registry.putIfAbsent(name, method) != null;

                    // If the method name has already been registered, log warning
                    if (existing) logger.warn("Encountered duplicate method name. value={}", name);

                } catch (Exception e) {
                    // Log error
                    logger.error("Encountered an error while registering method. class={} method={}", classInfo.getName(), methodInfo.getName(), e);
                }
            }));

        } catch (Exception e) {
            // Log error
            logger.error("Encountered an error while scanning and registering methods.", e);
        }
    }

    /// Returns an {@link Optional} containing the corresponding {@link Method} instance for the given method name,
    /// or an empty {@link Optional} if the given method name is not registered.
    /// @param name the method name (e.g., `login`)
    /// @return an {@link Optional} containing the corresponding {@link Method} instance for the given method name,
    ///         or an empty {@link Optional} if the given method name is not registered
    /// @throws NullPointerException if the input `name` is null
    /// @see Method
    /// @see Optional
    public static Optional<Method> get(String name) {
        Objects.requireNonNull(name, "Method name cannot be null.");

        // Get and return the method from the registry map
        return Optional.ofNullable(registry.get(name));
    }
}