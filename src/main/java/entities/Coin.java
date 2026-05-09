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
        // Если действует магнит — не гасим скорость
        if (magnetVX != 0 || magnetVY != 0) {
            x += magnetVX;
            y += magnetVY;
            // Ускоряемся к игроку, а не замедляемся
            magnetVX *= 1.05;
            magnetVY *= 1.05;
            // Ограничение максимальной скорости
            double speed = Math.sqrt(magnetVX * magnetVX + magnetVY * magnetVY);
            if (speed > 8) {
                magnetVX = magnetVX / speed * 8;
                magnetVY = magnetVY / speed * 8;
            }
        }
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