import java.awt.*;

public class Platform {
    private int x, y, width, height;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics2D g, int camX, int camY) {
        int screenX = x - camX;
        int screenY = y - camY;
        g.setColor(new Color(34, 139, 34));
        g.fillRect(screenX, screenY, width, height);
        g.setColor(new Color(0, 100, 0));
        g.fillRect(screenX, screenY, width, 5);
        g.setColor(new Color(0, 80, 0));
        for (int i = 0; i < width; i += 20) {
            g.fillRect(screenX + i, screenY + 5, 10, 3);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getWidth() { return width; }
}