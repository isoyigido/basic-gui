package io.github.isoyigido.basic.gui.core.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// This annotation should only be used to annotate concrete subclasses of {@link WidgetBuilder}.
///
/// Marks the annotated {@link WidgetBuilder} subclass for static registry in {@link WidgetBuilderRegistry}.
/// The {@link #type()} indicates the component type in the GUI file which links to the annotated
/// {@link WidgetBuilder} subclass. The {@link GUILoader} class uses the {@link WidgetBuilderRegistry}
/// to get the right {@link WidgetBuilder} instance for a given component type.
///
/// **Example usage:** \
/// In the example below, the widgets of type `button` in the GUI file
/// will use a new instance of `ButtonBuilder` for creation.
/// ```java
/// @RegisterWidgetBuilder(type = "button")
/// public class ButtonBuilder extends WidgetBuilder {
///     // ...
/// }
/// ```
///
/// @see WidgetBuilderRegistry
/// @see WidgetBuilder
/// @see GUILoader
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterWidgetBuilder {
    /// Returns the component type in the GUI file which links to the annotated {@link WidgetBuilder} subclass.
    /// @return the component type in the GUI file which links to the annotated {@link WidgetBuilder} subclass
    String type();
}