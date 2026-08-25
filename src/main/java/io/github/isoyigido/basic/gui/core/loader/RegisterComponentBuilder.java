package io.github.isoyigido.basic.gui.core.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// This annotation should only be used to annotate concrete subclasses of {@link ComponentBuilder}.
///
/// Marks the annotated {@link ComponentBuilder} subclass for static registration in {@link ComponentBuilderRegistry}.
/// The {@link #type()} indicates the component type in the GUI file which links to the annotated
/// {@link ComponentBuilder} subclass. The {@link GUILoader} class uses the {@link ComponentBuilderRegistry}
/// to get the right {@link ComponentBuilder} instance for a given component type.
///
/// A component type name can consist of lowercase letters, uppercase letters, digits, underscores (`_`),
/// and hyphens (`-`).
///
/// **Example usage:** \
/// In the example below, components of type `button` in the GUI file will use a new instance of `ButtonBuilder` for creation.
/// ```java
/// @RegisterComponentBuilder(type = "button")
/// public class ButtonBuilder extends ComponentBuilder {
///     // ...
/// }
/// ```
///
/// @see ComponentBuilderRegistry
/// @see ComponentBuilder
/// @see GUILoader
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterComponentBuilder {
    /// Returns the component type in the GUI file which links to the annotated {@link ComponentBuilder} subclass.
    ///
    /// A component type name can consist of lowercase letters, uppercase letters, digits, underscores (`_`),
    /// and hyphens (`-`).
    ///
    /// @return the component type in the GUI file which links to the annotated {@link ComponentBuilder} subclass
    String type();
}