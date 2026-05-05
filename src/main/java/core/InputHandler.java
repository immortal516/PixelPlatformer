package core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private boolean left, right, jump, h, shift, down;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT  -> left = true;
            case KeyEvent.VK_RIGHT -> right = true;
            case KeyEvent.VK_SPACE -> jump = true;
            case KeyEvent.VK_A     -> left = true;
            case KeyEvent.VK_D     -> right = true;
            case KeyEvent.VK_W     -> jump = true;
            case KeyEvent.VK_H     -> h = true;
            case KeyEvent.VK_SHIFT -> shift = true;
            case KeyEvent.VK_S     -> down = true;
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
            case KeyEvent.VK_H     -> h = false;
            case KeyEvent.VK_SHIFT -> shift = false;
            case KeyEvent.VK_S     -> down = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public boolean isLeft()  { return left; }
    public boolean isRight() { return right; }
    public boolean isJump()  { return jump; }
    public boolean isH()     { return h; }
    public boolean isShift() { return shift; }
    public boolean isDown()  { return down; }
    public void consumeJump() { jump = false; }
    public void consumeH()    { h = false; }
}