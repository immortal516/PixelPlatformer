package entities;

import java.awt.*;

public class PowerUp {
    public static final int TIME_BONUS = 0;
    public static final int MAGNET = 1;
    public static final int JETPACK = 2;

    private int x, y;
    private int type;
    private int width = 16, height = 16;
    private boolean active = true;
    private float bobOffset;

    public PowerUp(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.bobOffset = (float)(Math.random() * Math.PI * 2);
    }

    public void update() {
        bobOffset += 0.05f;
    }

    public void draw(Graphics2D g, int camX) {
        int sx = x - camX;
        int bob = (int)(Math.sin(bobOffset) * 3);

        // Зелёная стрелочка-подсветка
        g.setColor(new Color(0, 255, 0, 150 + (int)(Math.sin(bobOffset * 3) * 100)));
        int ax = sx + width / 2;
        int ay = y - 12 + bob;
        int[] xp = {ax, ax - 5, ax + 5};
        int[] yp = {ay, ay + 6, ay + 6};
        g.fillPolygon(xp, yp, 3);

        switch (type) {
            case TIME_BONUS:
                g.setColor(new Color(100, 200, 255));
                g.fillOval(sx, y + bob, width, height);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 9));
                g.drawString("+5s", sx + 1, y + 13 + bob);
                break;
            case MAGNET:
                g.setColor(new Color(200, 50, 50));
                g.fillRect(sx, y + bob, width, height);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString("M", sx + 4, y + 13 + bob);
                break;
            case JETPACK:
                g.setColor(new Color(255, 150, 0));
                int[] xp2 = {sx, sx + width, sx + width / 2};
                int[] yp2 = {y + height + bob, y + height + bob, y + bob};
                g.fillPolygon(xp2, yp2, 3);
                g.setColor(Color.YELLOW);
                g.fillRect(sx + width/2 - 2, y + height + bob + 2, 4, 6);
                break;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getType() { return type; }
    public boolean isActive() { return active; }
    public void deactivate() { active = false; }
    public int getX() { return x; }
}