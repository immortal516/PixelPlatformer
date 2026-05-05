package entities;

import java.awt.*;

public class Danger {
    private int x, y;
    private int width = 10;
    private int height = 10;
    private double speedY = 1.5;
    private boolean active = true;
    private Color color = new Color(100, 100, 110);

    public Danger(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y += speedY;
        speedY += 0.15;
        if (y > 650) active = false;
    }

    public void draw(Graphics2D g, int camX) {
        int sx = x - camX;
        g.setColor(color);
        int[] xp = {sx, sx + width, sx + width / 2};
        int[] yp = {y + height, y + height, y};
        g.fillPolygon(xp, yp, 3);
        g.setColor(color.brighter());
        g.drawLine(sx + width / 2, y + 3, sx + width / 2, y + height - 2);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isActive() { return active; }
    public int getX() { return x; }
}