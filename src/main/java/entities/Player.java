package entities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player {
    private int x, y;
    private int width = 32;
    private int height = 32;
    private int speedX, speedY;
    private boolean onGround;
    private boolean facingRight;
    private boolean isJumping;

    private final int GRAVITY = 1;
    private final int JUMP_STRENGTH = -22;
    private final int MOVE_SPEED = 5;

    private BufferedImage sprite;
    private Color skinColor, hairColor, shirtColor, pantsColor;
    private int animationFrame;

    public Player(int x, int y, BufferedImage sprite, Color[] colors) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.skinColor = colors[0];
        this.hairColor = colors[1];
        this.shirtColor = colors[2];
        this.pantsColor = colors[3];
        this.facingRight = true;
        speedX = 0;
        speedY = 0;
        onGround = true;
        isJumping = false;
    }

    public void moveLeft() {
        speedX = -MOVE_SPEED;
        facingRight = false;
    }

    public void moveRight() {
        speedX = MOVE_SPEED;
        facingRight = true;
    }

    public void jump() {
        if (onGround) {
            speedY = JUMP_STRENGTH;
            isJumping = true;
        }
    }

    public void update(ArrayList<Platform> platforms) {
        speedY += GRAVITY;
        x += speedX;
        y += speedY;

        if (x < 0) x = 0;

        onGround = false;
        Rectangle bounds = new Rectangle(x, y, width, height);

        for (Platform platform : platforms) {
            Rectangle platBounds = platform.getBounds();
            if (bounds.intersects(platBounds)) {
                if (speedY > 0 && y + height - speedY <= platBounds.y) {
                    y = platBounds.y - height;
                    speedY = 0;
                    onGround = true;
                    isJumping = false;
                } else if (speedY < 0 && y - speedY >= platBounds.y + platBounds.height) {
                    y = platBounds.y + platBounds.height;
                    speedY = 0;
                } else if (speedX > 0 && x + width - speedX <= platBounds.x) {
                    if (platBounds.width < 500) x = platBounds.x - width;
                } else if (speedX < 0 && x - speedX >= platBounds.x + platBounds.width) {
                    if (platBounds.width < 500) x = platBounds.x + platBounds.width;
                }
            }
        }

        speedX = 0;
        animationFrame = (animationFrame + 1) % 60;
    }

    public void draw(Graphics2D g, int camX, int camY) {
        int screenX = x - camX;
        int screenY = y - camY;

        if (sprite != null && !isJumping) {
            int drawX = facingRight ? screenX : screenX + width;
            int drawWidth = facingRight ? width : -width;
            g.drawImage(sprite, drawX, screenY, drawWidth, height, null);
        } else {
            drawJumpCharacter(g, screenX, screenY);
        }
    }

    private void drawJumpCharacter(Graphics2D g, int sx, int sy) {
        // Кепка
        g.setColor(shirtColor);
        g.fillRect(sx + 2, sy - 6, 28, 10);

        // Голова
        g.setColor(skinColor);
        g.fillRect(sx + 2, sy + 2, 28, 16);

        // Глаза выпученные
        g.setColor(Color.WHITE);
        g.fillOval(sx + 6, sy + 4, 10, 10);
        g.fillOval(sx + 16, sy + 4, 10, 10);
        g.setColor(Color.BLACK);
        g.fillOval(sx + 9, sy + 7, 4, 4);
        g.fillOval(sx + 19, sy + 7, 4, 4);

        // Рот
        g.setColor(Color.BLACK);
        g.fillOval(sx + 12, sy + 12, 8, 6);
        g.setColor(new Color(255, 100, 100));
        g.fillOval(sx + 13, sy + 13, 6, 4);

        // Усы
        g.setColor(hairColor);
        g.fillRect(sx + 4, sy + 10, 24, 2);
        g.fillRect(sx, sy + 9, 4, 4);
        g.fillRect(sx + 28, sy + 9, 4, 4);

        // Рубашка
        g.setColor(shirtColor);
        g.fillRect(sx + 4, sy + 18, 24, 10);

        // Руки в стороны
        g.setColor(skinColor);
        g.fillRect(sx - 6, sy + 18, 8, 5);
        g.fillRect(sx + width - 2, sy + 18, 8, 5);

        // Ладошки
        g.setColor(skinColor.brighter());
        g.fillRect(sx - 8, sy + 17, 5, 7);
        g.fillRect(sx + width + 3, sy + 17, 5, 7);

        // Штаны
        g.setColor(pantsColor);
        g.fillRect(sx + 4, sy + 24, 10, 8);
        g.fillRect(sx + 18, sy + 24, 10, 8);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void respawn(int x, int y) {
        this.x = x;
        this.y = y;
        speedX = 0;
        speedY = 0;
        isJumping = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isOnGround() { return onGround; }
}