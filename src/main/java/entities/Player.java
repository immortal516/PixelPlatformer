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
    private boolean speedBoost = false;
    private boolean jetpack = false;
    private boolean magnet = false;

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

    public void setSpeedBoost(boolean boost) { this.speedBoost = boost; }
    public void setJetpack(boolean jp) { this.jetpack = jp; }
    public void setMagnet(boolean m) { this.magnet = m; }

    public boolean hasMagnet() { return magnet; }

    public void moveLeft() {
        speedX = speedBoost ? -(int)(MOVE_SPEED * 1.4) : -MOVE_SPEED;
        facingRight = false;
    }

    public void moveRight() {
        speedX = speedBoost ? (int)(MOVE_SPEED * 1.4) : MOVE_SPEED;
        facingRight = true;
    }

    public void jump() {
        if (onGround) {
            speedY = speedBoost ? (int)(JUMP_STRENGTH * 1.2) : JUMP_STRENGTH;
            isJumping = true;
        }
    }

    public void jetpackFly(boolean up, boolean down) {
        if (jetpack && !onGround) {
            if (up) speedY = -8;
            else if (down) speedY = 5;
            else speedY = 0;
        }
    }

    public void update(ArrayList<Platform> platforms) {
        speedY += jetpack ? 0.3 : GRAVITY;
        if (speedY > 8) speedY = 8;

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

    public void draw(Graphics2D g, int camX) {
        int screenX = x - camX;
        if (sprite != null && !isJumping && !jetpack) {
            int drawX = facingRight ? screenX : screenX + width;
            int drawWidth = facingRight ? width : -width;
            g.drawImage(sprite, drawX, y, drawWidth, height, null);
        } else if (jetpack) {
            drawJetpackCharacter(g, screenX, y);
        } else {
            drawJumpCharacter(g, screenX, y);
        }
    }

    private void drawJetpackCharacter(Graphics2D g, int sx, int sy) {
        g.setColor(Color.ORANGE);
        for (int i = 0; i < 3; i++)
            g.fillRect(sx + 10 + i*6, sy + height, 4, 6 + (animationFrame % 4));
        g.setColor(Color.YELLOW);
        g.fillRect(sx + 14, sy + height + 2, 4, 4);
        drawJumpCharacter(g, sx, sy);
    }

    private void drawJumpCharacter(Graphics2D g, int sx, int sy) {
        g.setColor(shirtColor);
        g.fillRect(sx + 2, sy - 6, 28, 10);
        g.setColor(skinColor);
        g.fillRect(sx + 2, sy + 2, 28, 16);
        g.setColor(Color.WHITE);
        g.fillOval(sx + 6, sy + 4, 10, 10);
        g.fillOval(sx + 16, sy + 4, 10, 10);
        g.setColor(Color.BLACK);
        g.fillOval(sx + 9, sy + 7, 4, 4);
        g.fillOval(sx + 19, sy + 7, 4, 4);
        g.setColor(Color.BLACK);
        g.fillOval(sx + 12, sy + 12, 8, 6);
        g.setColor(new Color(255, 100, 100));
        g.fillOval(sx + 13, sy + 13, 6, 4);
        g.setColor(hairColor);
        g.fillRect(sx + 4, sy + 10, 24, 2);
        g.fillRect(sx, sy + 9, 4, 4);
        g.fillRect(sx + 28, sy + 9, 4, 4);
        g.setColor(shirtColor);
        g.fillRect(sx + 4, sy + 18, 24, 10);
        g.setColor(skinColor);
        g.fillRect(sx - 6, sy + 18, 8, 5);
        g.fillRect(sx + width - 2, sy + 18, 8, 5);
        g.setColor(skinColor.brighter());
        g.fillRect(sx - 8, sy + 17, 5, 7);
        g.fillRect(sx + width + 3, sy + 17, 5, 7);
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