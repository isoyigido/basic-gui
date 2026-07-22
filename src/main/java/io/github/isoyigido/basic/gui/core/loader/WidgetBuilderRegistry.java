package io.github.isoyigido.basic.gui.core.loader;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/// This registry class statically registers new instance suppliers for {@link WidgetBuilder} subclasses
/// annotated with {@link RegisterWidgetBuilder} into a {@linkplain Map} using {@link ClassGraph}.
/// The {@linkplain Map} maps each component type to the corresponding {@link WidgetBuilder} instance supplier
/// based on the annotation parameter {@link RegisterWidgetBuilder#type()}. New {@link WidgetBuilder} instances
/// can be obtained using {@link #get(String)}, which returns the corresponding new instance for the given component type.
/// @see #get(String)
/// @see RegisterWidgetBuilder
/// @see WidgetBuilder
/// @see GUILoader
/// @see ClassGraph
public final class WidgetBuilderRegistry {
    /// Private constructor to prevent instantiation
    private WidgetBuilderRegistry() {
        throw new UnsupportedOperationException("Registry class cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(WidgetBuilderRegistry.class);

    /// This registry maps each component type to its {@link WidgetBuilder} instance supplier.
    private static final Map<String, Supplier<Optional<WidgetBuilder>>> registry = new HashMap<>(8);

    static {
        // Use classgraph to scan for classes
        try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
            // Get the list of classes annotated with RegisterWidgetBuilder
            List<Class<?>> annotatedClasses = scanResult
                    .getClassesWithAnnotation(RegisterWidgetBuilder.class)
                    .loadClasses();

            // For each class annotated with RegisterWidgetBuilder
            for (Class<?> annotatedClass : annotatedClasses) {
                // If the class is not a subclass of WidgetBuilder
                if (!WidgetBuilder.class.isAssignableFrom(annotatedClass)) {
                    // Log warning
                    logger.warn("Class is annotated with @RegisterWidgetBuilder but does not extend WidgetBuilder. Skipping it. class={}", annotatedClass.getSimpleName());

                    // Skip the class
                    continue;
                }


                // Safely cast the annotated class to a subclass of WidgetBuilder
                Class<? extends WidgetBuilder> widgetBuilderClass = annotatedClass.asSubclass(WidgetBuilder.class);

                // If the annotated subclass of WidgetBuilder is abstract
                if (Modifier.isAbstract(widgetBuilderClass.getModifiers())) {
                    // Log warning
                    logger.warn("Abstract class is annotated with @RegisterWidgetBuilder. Skipping it. class={}", annotatedClass.getSimpleName());

                    // Skip the class
                    continue;
                }

                // Get the RegisterWidgetBuilder annotation
                RegisterWidgetBuilder annotation = widgetBuilderClass.getAnnotation(RegisterWidgetBuilder.class);

                // Get the component type from the annotation
                final String type = annotation.type();

                try {
                    // Get the parameterless constructor of the WidgetBuilder subclass
                    Constructor<? extends WidgetBuilder> constructor = widgetBuilderClass.getDeclaredConstructor();

                    // Make the constructor accessible
                    constructor.setAccessible(true);

                    // Create a new instance to proactively test for exceptions
                    constructor.newInstance();

                    // Put the component type and the corresponding new instance supplier in the map
                    registry.put(type, () -> {
                        try {
                            // Return an optional containing the new instance
                            return Optional.of(constructor.newInstance());

                        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                            // Log error
                            logger.error("Encountered an error while creating new widget builder instance. type={}", type, e);

                            // Return an empty optional
                            return Optional.empty();
                        }
                    });

                } catch (NoSuchMethodException e) {
                    // Log error
                    logger.error("Widget builder has no declared parameterless constructor. Skipping it. type={} class={}", type, widgetBuilderClass.getSimpleName(), e);

                } catch (Exception e) {
                    // Log error
                    logger.error("Encountered broken widget builder constructor. Skipping it. type={} class={}", type, widgetBuilderClass.getSimpleName(), e);
                }
            }

        } catch (Exception e) {
            // Log error
            logger.error("Encountered an error while registering widget builders.", e);
        }
    }

    /// Returns an {@link Optional} containing a new {@link WidgetBuilder} instance for the given component type,
    /// or an empty {@link Optional} if the given component type is not registered, or if the corresponding supplier
    /// returns an empty {@link Optional}.
    /// @param type the component type (e.g., `button`)
    /// @return an {@link Optional} containing a new {@link WidgetBuilder} instance for the given component type,
    ///         or an empty {@link Optional} if the given component type is not registered, or if the corresponding
    ///         supplier returns an empty {@link Optional}
    /// @see WidgetBuilder
    /// @see Optional
    public static Optional<WidgetBuilder> get(String type) {
        // Get the supplier from the map and return the widget builder from the supplier
        return Optional.ofNullable(registry.get(type)).flatMap(Supplier::get);
    }
}