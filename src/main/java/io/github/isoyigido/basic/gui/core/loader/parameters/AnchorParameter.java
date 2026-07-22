package io.github.isoyigido.basic.gui.core.loader.parameters;

import io.github.isoyigido.basic.gui.core.Component;
import io.github.isoyigido.basic.gui.core.Widget;
import io.github.isoyigido.basic.gui.core.loader.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/// Overrides the {@link #parse(String)} method to map the {@link String} representation
/// to a {@link Widget.Anchor} value and stores it. If the given {@link String} representation
/// is unrecognized, returns an empty {@link Optional}.
/// @see #parse(String)
public class AnchorParameter extends Parameter<Widget.Anchor> {
    private static final Logger logger = LoggerFactory.getLogger(AnchorParameter.class);

    /// Parses the given {@link String} representation of an anchor point.
    ///
    /// **Recognized values:**
    /// - `center`: {@link Widget.Anchor#CENTER}
    /// - `left`: {@link Widget.Anchor#LEFT}
    /// - `right`: {@link Widget.Anchor#RIGHT}
    /// - `top`: {@link Widget.Anchor#TOP}
    /// - `bottom`: {@link Widget.Anchor#BOTTOM}
    /// - `top-left`: {@link Widget.Anchor#TOP_LEFT}
    /// - `top-right`: {@link Widget.Anchor#TOP_RIGHT}
    /// - `bottom-left`: {@link Widget.Anchor#BOTTOM_LEFT}
    /// - `bottom-right`: {@link Widget.Anchor#BOTTOM_RIGHT}
    ///
    /// *Note: Uppercase variants are recognized as well.*
    ///
    /// **Special cases:**
    /// - Returns an empty {@link Optional} if the given {@link String} representation is unrecognized
    ///
    /// @param valueString the {@link String} representation of the anchor point
    /// @return an {@link Optional} containing the parsed {@link Widget.Anchor},
    ///         or an empty {@link Optional} if the given {@link String} representation is unrecognized
    @Override
    public Optional<Widget.Anchor> parse(String valueString) {
        // Map the stripped, lowercase value string to the correct anchor point
        return Optional.ofNullable(switch (valueString.strip().toLowerCase()) {
            case "center"       -> Widget.Anchor.CENTER;
            case "left"         -> Widget.Anchor.LEFT;
            case "right"        -> Widget.Anchor.RIGHT;
            case "top"          -> Widget.Anchor.TOP;
            case "bottom"       -> Widget.Anchor.BOTTOM;
            case "top-left"     -> Widget.Anchor.TOP_LEFT;
            case "top-right"    -> Widget.Anchor.TOP_RIGHT;
            case "bottom-left"  -> Widget.Anchor.BOTTOM_LEFT;
            case "bottom-right" -> Widget.Anchor.BOTTOM_RIGHT;
            default -> {
                // Log warning
                AnchorParameter.logger.warn("Anchor parameter has invalid value. value={}", valueString);

                // Yield null (returns an empty Optional)
                yield null;
            }
        });
    }

    /// Returns the right {@link Widget} based on the stored {@link Widget.Anchor} value.
    /// @param component the {@link Component} stored in the returned {@link Widget}
    /// @param x the x-coordinate of the anchor point
    /// @param y the y-coordinate of the anchor point
    /// @return an {@link Optional} containing the new {@link Widget} anchored to the stored {@link Widget.Anchor},
    ///         or an empty {@link Optional} if this parameter has no valid set value
    public Optional<Widget> getWidget(Component component, int x, int y) {
        // If this parameter has no set value, return empty optional
        if (super.isEmpty()) return Optional.empty();

        // Return an optional containing the new Widget instance
        return Optional.of(new Widget(component, x, y, super.get()));
    }
}