import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Firework {
    private ArrayList<Particle> particles;
    private int life;
    private boolean dead;

    public Firework(int x, int y, Color color, Random random) {
        particles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = 1 + Math.random() * 3;
            particles.add(new Particle(
                    x, y,
                    Math.cos(angle) * speed,
                    Math.sin(angle) * speed,
                    color
            ));
        }
        life = 60;
        dead = false;
    }

    public void update() {
        for (Particle p : particles) {
            p.update();
        }
        life--;
        if (life <= 0) dead = true;
    }

    public void draw(Graphics2D g) {
        for (Particle p : particles) {
            p.draw(g);
        }
    }

    public boolean isDead() { return dead; }

    class Particle {
        double x, y, vx, vy;
        Color color;
        int alpha = 255;

        Particle(double x, double y, double vx, double vy, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.05;
            alpha -= 4;
            if (alpha < 0) alpha = 0;
        }

        void draw(Graphics2D g) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g.fillRect((int)x, (int)y, 4, 4);
        }
    }
}