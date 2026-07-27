package io.github.isoyigido.basic.gui.core.loader;

import io.github.isoyigido.basic.gui.core.Component;
import io.github.isoyigido.basic.gui.core.Widget;
import io.github.isoyigido.basic.gui.core.loader.parameters.AnchorParameter;
import io.github.isoyigido.basic.gui.core.loader.parameters.BooleanParameter;
import io.github.isoyigido.basic.gui.core.loader.parameters.numbers.IntegerParameter;

import java.util.Optional;

/// Represents a {@link Builder} for widgets.
///
/// **Required parameters:**
/// - `x`: the x-coordinate of the widget anchor point ({@linkplain IntegerParameter})
/// - `y`: the y-coordinate of the widget anchor point ({@linkplain IntegerParameter})
/// - `anchor`: the point on the widget anchored to the coordinates ({@linkplain AnchorParameter})
///
/// **Optional parameters:**
/// - `show`: whether the widget is initially visible ({@linkplain BooleanParameter})
/// - `layer`: the layer index of the widget ({@linkplain IntegerParameter})
///
/// @see Builder
/// @see Widget
public class WidgetBuilder extends Builder<Widget> {
    /// The component that is stored in the built widget
    private final Component component;

    // --- PARAMETERS ---
    /// Required: the x-coordinate of the widget anchor point ({@linkplain IntegerParameter})
    private final IntegerParameter x = new IntegerParameter();

    /// Required: the y-coordinate of the widget anchor point ({@linkplain IntegerParameter})
    private final IntegerParameter y = new IntegerParameter();

    /// Required: the point on the widget anchored to the coordinates ({@linkplain AnchorParameter})
    private final AnchorParameter anchor = new AnchorParameter();

    /// Optional: whether the widget is initially visible ({@linkplain BooleanParameter})
    private final BooleanParameter visibility = new BooleanParameter();

    /// Optional: the layer index of the widget ({@linkplain IntegerParameter})
    private final IntegerParameter layerIndex = new IntegerParameter();

    /// Constructs a widget builder.
    ///
    /// - `x`: the x-coordinate of the widget anchor point ({@linkplain IntegerParameter})
    /// - `y`: the y-coordinate of the widget anchor point ({@linkplain IntegerParameter})
    /// - `anchor`: the point on the widget anchored to the coordinates ({@linkplain AnchorParameter})
    ///
    /// **Optional parameters:**
    /// - `show`: whether the widget is initially visible ({@linkplain BooleanParameter})
    /// - `layer`: the layer index of the widget ({@linkplain IntegerParameter})
    ///
    /// @param component the component that is stored in the built widget
    public WidgetBuilder(Component component) {
        // Set the component
        this.component = component;

        // Add required parameters (x, y, anchor)
        super.addRequiredParameter("x", this.x);
        super.addRequiredParameter("y", this.y);
        super.addRequiredParameter("anchor", this.anchor);

        // Add optional parameters (show, layer)
        this.addOptionalParameter("show", this.visibility);
        this.addOptionalParameter("layer", this.layerIndex);
    }

    /// Builds a {@link Widget} using the stored parameter values.
    /// @return an {@link Optional} containing the built {@link Widget} instance
    /// @see Widget
    @Override
    protected Optional<Widget> build() {
        // Create the widget using the stored component, coordinates, and anchor point
        Widget widget = new Widget(this.component, this.x.get(), this.y.get(), this.anchor.get());

        // Set the visibility of the widget if the visibility parameter has a set value
        this.visibility.getOptional().ifPresent(widget::setVisible);

        // Set the layer index of the widget if the layer index parameter has a set value
        this.layerIndex.getOptional().ifPresent(widget::setLayerIndex);

        // Return an optional containing the widget
        return Optional.of(widget);
    }
}