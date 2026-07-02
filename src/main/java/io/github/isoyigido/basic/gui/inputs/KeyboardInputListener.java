package io.github.isoyigido.basic.gui.inputs;

import io.github.isoyigido.basic.gui.core.GUIManager;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/// Forwards the key events to {@link GUIManager}.
/// @see KeyListener
/// @see GUIManager
public final class KeyboardInputListener extends KeyAdapter {
    @Override
    public void keyTyped(KeyEvent e) {
        // Register a key typing event
        GUIManager.onKeyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Register a key press event
        GUIManager.onKeyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Register a key release event
        GUIManager.onKeyReleased(e);
    }
}