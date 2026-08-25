package io.github.isoyigido.basic.gui.core.loader.parameters.methods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// This annotation should only be used to annotate methods.
///
/// Marks the annotated method for static registration in {@link MethodRegistry}. The {@link #name()} indicates the name
/// under which the annotated method is registered.
///
/// **Example usage:** \
/// The method below is registered under the name `login`, not `handleLoginRequest`.
/// ```java
/// @RegisterMethod(name = "login")
/// private static void handleLoginRequest(String username, String password) {
///     // ...
/// }
/// ```
///
/// @see MethodRegistry
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterMethod {
    /// Returns the name under which the annotated method is registered.
    /// @return the name under which the annotated method is registered
    String name();
}