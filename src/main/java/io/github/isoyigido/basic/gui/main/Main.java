package io.github.isoyigido.basic.gui.main;

import io.github.isoyigido.basic.gui.window.BasicPanel;

/// Contains the method {@link #startMainLoop(BasicPanel, int, int)} to start the main rendering and update loop.
/// @see BasicPanel
public final class Main {
    /// Starts the main rendering and update loop on a new thread.
    /// @param panel the panel that is rendered on and updated
    /// @param fps number of frames (render calls) per second
    /// @param ups number of updates (update calls) per second
    public static void startMainLoop(BasicPanel panel, int fps, int ups) {
        // On a new thread
        new Thread(() -> {
            // Calculate the nanoseconds per frame and update
            double nanosecondsPerFrame = 1000000000.0 / fps;
            double nanosecondsPerUpdate = 1000000000.0 / ups;

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
                    panel.update();

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