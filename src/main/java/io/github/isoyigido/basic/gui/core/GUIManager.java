package io.github.isoyigido.basic.gui.core;

import io.github.isoyigido.basic.gui.constants.Cursors;
import io.github.isoyigido.basic.gui.window.BasicPanel;
import io.github.isoyigido.basic.gui.window.ScreenConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/// Manages switching GUIs, global overlays, alerts, and global key binds.
/// Provides methods for changing the current GUI, changing the mouse cursor,
/// moving the mouse cursor, taking a screenshot, setting global key binds,
/// setting global overlays, and making alerts.
/// @see GUI
/// @see Overlay
/// @see Alert
public final class GUIManager {
    /// Private constructor to prevent instantiation
    private GUIManager() {
        throw new UnsupportedOperationException("Utility cannot be instantiated.");
    }

    private static final Logger logger = LoggerFactory.getLogger(GUIManager.class);

    /// The panel linked to the displayed window
    private static BasicPanel panel = null;

    /// Empty GUI instance
    private static final GUI EMPTY_GUI = new GUI(){};

    /// The current GUI
    private static GUI currentGUI = EMPTY_GUI;

    /// The current overlay
    private static Overlay overlay = null;

    /// The thread-safe list of active alerts
    private static final List<Alert> activeAlerts = new CopyOnWriteArrayList<>();

    /// The map of global key binds (key, action)
    private static final Map<Character, Runnable> globalKeyBinds = new HashMap<>(4);

    /// The robot responsible for automated inputs
    private static Robot robot = null;

    static {
        try {
            // Create new robot
            robot = new Robot();
        } catch (AWTException e) {
            // Log error
            logger.error("Robot could not be initialized.", e);
        }

        // Set the GUI to the empty GUI
        setGUI(EMPTY_GUI);
    }

    /// Sets the panel that holds the GUI manager.
    /// @param panel the {@link BasicPanel} object
    /// @throws NullPointerException if the input `panel` is null
    public static void setPanel(BasicPanel panel) {
        Objects.requireNonNull(panel, "Panel to set cannot be null.");

        GUIManager.panel = panel;
    }

    /// Sets the given GUI as the current GUI.
    /// @param gui the new GUI that will be set
    /// @throws NullPointerException if the input `gui` is null
    public static void setGUI(GUI gui) {
        Objects.requireNonNull(gui, "GUI to set is null.");

        // Compile the given GUI
        gui.compile();

        // Set the cursor to the default cursor
        setCursor(Cursors.DEFAULT);

        // Set the current GUI
        currentGUI = gui;
    }

    /// Uses the given supplier to set the current GUI.
    /// @param guiSupplier the supplier for the new GUI that will be set
    /// @throws NullPointerException if the input `guiSupplier` is null
    public static void setGUI(Supplier<GUI> guiSupplier) {
        Objects.requireNonNull(guiSupplier, "GUI supplier cannot be null.");

        setGUI(guiSupplier.get());
    }

    /// Forwards the render call to the current GUI, the global overlay, and active alerts.
    /// @param g the graphics context to render on
    public static void render(Graphics2D g) {
        // Render the current GUI
        currentGUI.render(g);

        // Render active alerts and the global overlay
        activeAlerts.forEach(alert -> alert.render(g));
        if (overlay != null) overlay.render(g);
    }

    /// Forwards the update call to the current GUI, the global overlay, and active alerts.
    public static void update() {
        // Update the current GUI
        currentGUI.update();

        // Update active alerts and the global overlay
        activeAlerts.forEach(Alert::update);
        if (overlay != null) overlay.update();
    }

    /// Returns the current mouse cursor.
    ///
    /// **Special cases:**
    /// - Returns {@link Cursors#DEFAULT} if the panel linked to the displayed window is null
    ///
    /// @return the current mouse cursor, or {@link Cursors#DEFAULT} if the panel linked to the displayed window is null
    public static Cursor getCursor() {
        // If a panel is linked, return the current mouse cursor
        // Else, return the default mouse cursor
        return Optional.ofNullable(panel).map(Component::getCursor).orElse(Cursors.DEFAULT);
    }

    /// Sets the mouse cursor.
    /// @param cursor the new mouse cursor
    /// @throws IllegalArgumentException if the input `cursor` is null
    public static void setCursor(Cursor cursor) {
        Objects.requireNonNull(cursor, "Cursor to be set cannot be null.");

        // If a panel is linked and the given cursor to be set
        // is different from the current cursor, set the cursor
        if ((panel != null) && !cursor.equals(getCursor())) panel.setCursor(cursor);
    }

    /// Moves the mouse cursor to the given location on the screen.
    /// @param x the x-coordinate on the virtual screen
    /// @param y the y-coordinate on the virtual screen
    public static void moveCursorTo(int x, int y) {
        // If the robot is null, return
        if (robot == null) return;

        // Calculate corresponding window coordinates and move the mouse cursor using the robot
        robot.mouseMove(
                Math.round(x * ScreenConfig.windowToVirtualRatioX),
                Math.round(y * ScreenConfig.windowToVirtualRatioY)
        );
    }

    /// Renders a frame of the displayed window on an image with matching dimensions.
    /// @return the rendered {@link BufferedImage} object
    public static BufferedImage takeScreenshot() {
        // --- Render the image ---
        // Initialize a new BufferedImage with dimensions matching the displayed window
        BufferedImage windowImage = new BufferedImage(ScreenConfig.windowWidth, ScreenConfig.windowHeight, BufferedImage.TYPE_INT_ARGB);

        // Create its graphics
        Graphics2D g = windowImage.createGraphics();

        // Set rendering configurations
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // AA is on

        // Render a frame on the image
        if (panel != null) panel.paintComponent(g);

        // Dispose of the graphics
        g.dispose();

        // Return the image
        return windowImage;
    }

    /// Returns the current GUI.
    /// @return the current GUI
    public static GUI getCurrentGUI() {
        return currentGUI;
    }

    /// Returns an {@link Optional} containing the current overlay,
    /// or an empty {@link Optional} if there is currently no overlay.
    /// @return an {@link Optional} containing the current overlay,
    ///         or an empty {@link Optional} if there is currently no overlay
    public static Optional<Overlay> getOverlay() {
        return Optional.ofNullable(overlay);
    }

    /// Sets the overlay to the given overlay.
    /// @param overlay the new overlay, or null for no overlay
    public static void setOverlay(Overlay overlay) {
        GUIManager.overlay = overlay;
    }

    /// Removes the current overlay.
    /// @apiNote This method is functionally the same as `GUIManager.setOverlay(null)`.
    ///          Use this method to improve readability.
    public static void removeOverlay() {
        // Set the overlay to null
        GUIManager.setOverlay(null);
    }

    /// Adds the given alert to the list of active alerts.
    /// @param alert the alert to add to the list
    /// @throws NullPointerException if the input `alert` is null
    public static void addAlert(Alert alert) {
        Objects.requireNonNull(alert, "Alert cannot be null.");

        // Set the parent list of the alert
        alert.setParentList(activeAlerts);

        // Add the alert to the list
        activeAlerts.add(alert);
    }

    /// Sets a global key bind. Can be called multiple times with
    /// @param key the character of the bound key
    /// @param action the action that is run when the bound key is pressed
    /// @throws NullPointerException if the input `action` is null
    public static void setGlobalKeyBind(char key, Runnable action) {
        Objects.requireNonNull(action, "Key action cannot be null.");

        // Put the key-action pair in the map
        globalKeyBinds.put(key, action);
    }

    // --- INPUT EVENT METHODS ---
    /// Forwards the mouse click event to the current GUI.
    /// @param x the x-coordinate of the click
    /// @param y the y-coordinate of the click
    /// @param mouseButton the mouse button that is clicked
    static void onMouseClicked(int x, int y, MouseButton mouseButton) {
        if (overlay == null) currentGUI.onMouseClicked(x, y, mouseButton);
    }

    /// Forwards the mouse press event to the current GUI.
    /// @param x the x-coordinate of the press
    /// @param y the y-coordinate of the press
    /// @param mouseButton the mouse button that is pressed
    static void onMousePressed(int x, int y, MouseButton mouseButton) {
        if (overlay == null) currentGUI.onMousePressed(x, y, mouseButton);
    }

    /// Forwards the mouse release event to the current GUI.
    /// @param mouseButton the mouse button that is released
    static void onMouseReleased(MouseButton mouseButton) {
        currentGUI.onMouseReleased(mouseButton);
    }

    /// Forwards the mouse move event to the current GUI.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    static void onMouseMoved(int x, int y) {
        if (overlay == null) currentGUI.onMouseMoved(x, y);
    }

    /// Forwards the mouse drag event to the current GUI.
    /// @param x the x-coordinate of the mouse
    /// @param y the y-coordinate of the mouse
    /// @param mouseButton the mouse button that is dragged
    static void onMouseDragged(int x, int y, MouseButton mouseButton) {
        if (overlay == null) currentGUI.onMouseDragged(x, y, mouseButton);
    }

    /// Forwards the mouse wheel move event to the current GUI.
    /// @param e the mouse wheel event
    static void onMouseWheelMoved(MouseWheelEvent e) {
        if (overlay == null) currentGUI.onMouseWheelMoved(e);
    }

    /// Forwards the key typing event to the current GUI.
    /// @param e the key event
    static void onKeyTyped(KeyEvent e) {
        if (overlay == null) currentGUI.onKeyTyped(e);

        // If the key typing event has not been consumed
        if (!e.isConsumed()) {
            // Get the character of the typed key
            char keyChar = e.getKeyChar();

            // Check each global key bind
            globalKeyBinds.forEach((key, action) -> {
                // If the bound key is pressed, run the action
                if (key == keyChar) action.run();
            });
        }
    }

    /// Forwards the key pressing event to the current GUI.
    /// @param e the key event
    static void onKeyPressed(KeyEvent e) {
        if (overlay == null) currentGUI.onKeyPressed(e);
    }

    /// Forwards the key releasing event to the current GUI.
    /// @param e the key event
    static void onKeyReleased(KeyEvent e) {
        currentGUI.onKeyReleased(e);
    }
}