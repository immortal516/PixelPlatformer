package entities;

import java.awt.*;

public class Coin {
    private int x, y;
    private float bobOffset;
    private float bobSpeed;

    public Coin(int x, int y) {
        this.x = x;
        this.y = y;
        this.bobOffset = (float) (Math.random() * Math.PI * 2);
        this.bobSpeed = 0.05f;
    }

    public void update() {
        bobOffset += bobSpeed;
    }

    public void draw(Graphics2D g, int camX, int camY) {
        int screenX = x - camX;
        int screenY = y - camY;
        int bob = (int)(Math.sin(bobOffset) * 3);

        g.setColor(new Color(200, 170, 0));
        g.fillOval(screenX - 8, screenY - 8 + bob, 16, 20);
        g.setColor(new Color(255, 215, 0));
        g.fillOval(screenX - 8, screenY - 10 + bob, 16, 20);
        g.setColor(new Color(255, 255, 150));
        g.fillOval(screenX - 4, screenY - 6 + bob, 8, 12);
        g.setColor(new Color(200, 170, 0));
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("$", screenX - 3, screenY + 3 + bob);
    }

    public Rectangle getBounds() {
        return new Rectangle(x - 8, y - 10, 16, 20);
    }

    public int getX() { return x; }
}