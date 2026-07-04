package io.github.isoyigido.basic.gui.main;

import io.github.isoyigido.basic.gui.core.GUIManager;
import io.github.isoyigido.basic.gui.window.BasicPanel;

import java.util.Objects;

/// Contains the method {@link #startMainLoop(BasicPanel, int, int)} to start the main rendering and update loop.
/// @see BasicPanel
public final class Main {
    /// Private constructor to prevent instantiation
    private Main() {
        throw new UnsupportedOperationException("Main class cannot be instantiated.");
    }

    /// Constant for the number of nanoseconds in a second (one billion)
    private static final double NANOSECONDS_PER_SECOND = 1_000_000_000;

    /// Starts the main rendering and update loop on a new thread.
    /// @param panel the panel that is rendered on and updated
    /// @param fps number of frames (render calls) per second
    /// @param ups number of updates (update calls) per second
    /// @throws NullPointerException if the input `panel` is null
    public static void startMainLoop(BasicPanel panel, int fps, int ups) {
        Objects.requireNonNull(panel, "Panel cannot be null.");

        // On a new thread
        new Thread(() -> {
            // Calculate the nanoseconds per frame and update
            double nanosecondsPerFrame = Main.NANOSECONDS_PER_SECOND / fps;
            double nanosecondsPerUpdate = Main.NANOSECONDS_PER_SECOND / ups;

            // Initialize the previous time in nanoseconds
            long previousTime = System.nanoTime();

            // Initialize the update and frame timers
            double deltaU = 0, deltaF = 0;

            // Until the user exits
            while (true) {
                // Get the current time in nanoseconds
                long currentTime = System.nanoTime();

                // Update the update and frame timers
                long elapsed = currentTime - previousTime;
                deltaU += elapsed;
                deltaF += elapsed;

                // Update the previous time
                previousTime = currentTime;

                // If enough time has passed for an update
                if (deltaU >= nanosecondsPerUpdate) {
                    // Do one update
                    GUIManager.update();

                    // Decrease update timer
                    deltaU -= nanosecondsPerUpdate;
                }

                // If enough time has passed for a new frame
                if (deltaF >= nanosecondsPerFrame) {
                    // Render one frame
                    panel.repaint();

                    // Decrease frame timer
                    deltaF -= nanosecondsPerFrame;
                }
            }
        }).start();
    }
}