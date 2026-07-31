package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.Anchor;
import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/// Overrides the {@link #parse(String)} method to map the {@link String} representation
/// to an {@link Anchor} value and stores it. If the given {@link String} representation
/// is unrecognized, returns an empty {@link Optional}.
/// @see #parse(String)
public class AnchorParameter extends Parameter<Anchor> {
    private static final Logger logger = LoggerFactory.getLogger(AnchorParameter.class);

    /// Parses the given {@link String} representation of an anchor point.
    ///
    /// **Recognized values:**
    /// - `center`: {@link Anchor#CENTER}
    /// - `left`: {@link Anchor#LEFT}
    /// - `right`: {@link Anchor#RIGHT}
    /// - `top`: {@link Anchor#TOP}
    /// - `bottom`: {@link Anchor#BOTTOM}
    /// - `top-left`: {@link Anchor#TOP_LEFT}
    /// - `top-right`: {@link Anchor#TOP_RIGHT}
    /// - `bottom-left`: {@link Anchor#BOTTOM_LEFT}
    /// - `bottom-right`: {@link Anchor#BOTTOM_RIGHT}
    ///
    /// *Note: Uppercase variants are recognized as well.*
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if the given {@link String} representation is unrecognized
    ///
    /// @param valueString the {@link String} representation of the anchor point
    /// @return an {@link Optional} containing the parsed {@link Anchor},
    ///         or an empty {@link Optional} if the given {@link String} representation is unrecognized
    @Override
    public Optional<Anchor> parse(String valueString) {
        // Map the stripped, lowercase value string to the correct anchor point
        return Optional.ofNullable(switch (valueString.strip().toLowerCase()) {
            case "center"       -> Anchor.CENTER;
            case "left"         -> Anchor.LEFT;
            case "right"        -> Anchor.RIGHT;
            case "top"          -> Anchor.TOP;
            case "bottom"       -> Anchor.BOTTOM;
            case "top-left"     -> Anchor.TOP_LEFT;
            case "top-right"    -> Anchor.TOP_RIGHT;
            case "bottom-left"  -> Anchor.BOTTOM_LEFT;
            case "bottom-right" -> Anchor.BOTTOM_RIGHT;
            default -> {
                // Log warning
                AnchorParameter.logger.warn("Anchor parameter has invalid value. value={}", valueString);

                // Yield null (returns an empty Optional)
                yield null;
            }
        });
    }
}