package io.github.isoyigido.basic.gui.window;

import java.awt.*;

/// Provides a simplified constructor for a full-screen window.
/// @see BasicWindow
/// @see ScreenConfig
public class FullScreenWindow extends BasicWindow {
    /// Constructs a basic window to be displayed.
    /// @param title the title of the window (null for default title)
    /// @param iconImage the icon image of the window (null for default icon)
    /// @param desiredScreenWidth the desired width for the virtual screen (in pixels)
    /// @param desiredScreenHeight the desired height for the virtual screen (in pixels)
    /// @apiNote The desired screen width and height should adhere to a common aspect ratio.
    ///          Choosing desired virtual screen dimensions that do not follow this will lead to the actual dimensions
    ///          being set differing significantly from the desired dimensions. This is because the native aspect ratio
    ///          is preserved by default for full screen windows to prevent stretching or squishing artifacts.
    public FullScreenWindow(String title, Image iconImage, int desiredScreenWidth, int desiredScreenHeight) {
        super(
                title,
                iconImage,
                true,
                false,
                ScreenConfig.actualScreenWidth, ScreenConfig.actualScreenHeight,
                desiredScreenWidth, desiredScreenHeight,
                true
        );
    }
}