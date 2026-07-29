package io.github.isoyigido.basic.gui.window;

/// Provides a parameterless constructor for a full-screen window.
/// @see BasicWindow
/// @see ScreenConfig
public class FullScreenWindow extends BasicWindow {
    /// Constructs a full-screen window to be displayed. The full-screen window has dimensions
    /// matching the dimensions of the physical screen displaying it, and is undecorated.
    public FullScreenWindow() {
        // Initialize a window with dimensions matching the actual screen dimensions
        super(ScreenConfig.actualScreenWidth, ScreenConfig.actualScreenHeight);

        // Make the window undecorated
        super.makeUndecorated();
    }

    @Override
    public BasicWindow setVirtualScreenDimensions(int desiredScreenWidth, int desiredScreenHeight) {
        // Enable preservation of native aspect ratio
        super.preserveNativeAspectRatio();

        // Set the virtual screen dimensions as usual
        return super.setVirtualScreenDimensions(desiredScreenWidth, desiredScreenHeight);
    }
}