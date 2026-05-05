package entities;

import java.awt.*;

public class Coin {
    private double x, y;
    private float bobOffset;
    private float bobSpeed;
    private double magnetVX, magnetVY;

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
        this.bobOffset = (float) (Math.random() * Math.PI * 2);
        this.bobSpeed = 0.05f;
    }

    public void update() {
        bobOffset += bobSpeed;
        x += magnetVX;
        y += magnetVY;
        magnetVX *= 0.9;
        magnetVY *= 0.9;
    }

    public void magnetPull(double vx, double vy) {
        magnetVX = vx;
        magnetVY = vy;
    }

    public void draw(Graphics2D g, int camX) {
        int screenX = (int)x - camX;
        int bob = (int)(Math.sin(bobOffset) * 3);

        g.setColor(new Color(200, 170, 0));
        g.fillOval(screenX - 8, (int)y - 8 + bob, 16, 20);
        g.setColor(new Color(255, 215, 0));
        g.fillOval(screenX - 8, (int)y - 10 + bob, 16, 20);
        g.setColor(new Color(255, 255, 150));
        g.fillOval(screenX - 4, (int)y - 6 + bob, 8, 12);
        g.setColor(new Color(200, 170, 0));
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("$", screenX - 3, (int)y + 3 + bob);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x - 8, (int)y - 10, 16, 20);
    }

    public int getX() { return (int)x; }
}