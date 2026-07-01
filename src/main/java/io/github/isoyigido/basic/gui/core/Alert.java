package io.github.isoyigido.basic.gui.core;

import java.awt.*;
import java.util.List;

/// Defines the contract for an alert that can be rendered and updated within a GUI context.
/// @see GUIManager
public abstract class Alert {
    /// The list of alerts that contains this alert
    private List<Alert> parentList = null;

    /// Renders the alert onto the given graphics context.
    /// @param g the graphics context to render on
    public abstract void render(Graphics2D g);

    /// Updates the internal state of the alert.
    public abstract void update();

    /// Sets the list of alerts that contains this alert.
    /// @param parentList the list of alerts that contains this alert
    public void setParentList(List<Alert> parentList) {
        this.parentList = parentList;
    }

    /// Removes this alert from its parent list if it has one.
    public void destroy() {
        // If this alert has a parent list, remove this alert from it
        if (this.parentList != null) this.parentList.remove(this);
    }
}