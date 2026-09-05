package io.github.isoyigido.basic.gui.core;

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
        char keyChar = e.getKeyChar();
        GUIManager.onKeyTyped(keyChar);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Register a key press event
        int keyCode = e.getKeyCode();
        GUIManager.onKeyPressed(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Register a key release event
        int keyCode = e.getKeyCode();
        GUIManager.onKeyReleased(keyCode);
    }
}