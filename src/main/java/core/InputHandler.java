package core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private boolean left, right, jump;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT  -> left = true;
            case KeyEvent.VK_RIGHT -> right = true;
            case KeyEvent.VK_SPACE -> jump = true;
            case KeyEvent.VK_A     -> left = true;
            case KeyEvent.VK_D     -> right = true;
            case KeyEvent.VK_W     -> jump = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT  -> left = false;
            case KeyEvent.VK_RIGHT -> right = false;
            case KeyEvent.VK_SPACE -> jump = false;
            case KeyEvent.VK_A     -> left = false;
            case KeyEvent.VK_D     -> right = false;
            case KeyEvent.VK_W     -> jump = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public boolean isLeft()  { return left; }
    public boolean isRight() { return right; }
    public boolean isJump()  { return jump; }

    // Чтобы стартовый пробел не вызывал прыжок
    public void consumeJump() { jump = false; }
}