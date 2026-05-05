package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class SpriteGenerator {

    public static Color[] generateRandomColors(Random random) {
        return new Color[] {
                randomColor(255, 180, 140, 255, 220, 180, random),
                randomColor(50, 30, 20, 200, 150, 100, random),
                randomColor(100, 50, 50, 255, 100, 100, random),
                randomColor(30, 30, 80, 100, 100, 200, random)
        };
    }

    public static BufferedImage generateRandomSprite(Color[] colors) {
        int size = 32;
        BufferedImage sprite = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sprite.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, size, size);

        Color skin = colors[0], hair = colors[1], shirt = colors[2], pants = colors[3];
        int cx = size / 2, cy = size / 2;

        g.setColor(skin);
        g.fillRect(cx - 6, cy - 12, 12, 10);
        g.setColor(hair);
        g.fillRect(cx - 7, cy - 14, 14, 5);
        g.setColor(Color.WHITE);
        g.fillRect(cx - 4, cy - 10, 3, 3);
        g.fillRect(cx + 1, cy - 10, 3, 3);
        g.setColor(Color.BLACK);
        g.fillRect(cx - 3, cy - 9, 2, 2);
        g.fillRect(cx + 2, cy - 9, 2, 2);
        g.setColor(shirt);
        g.fillRect(cx - 5, cy - 2, 10, 8);
        g.setColor(skin);
        g.fillRect(cx - 8, cy - 1, 3, 6);
        g.fillRect(cx + 5, cy - 1, 3, 6);
        g.setColor(pants);
        g.fillRect(cx - 4, cy + 6, 3, 6);
        g.fillRect(cx + 1, cy + 6, 3, 6);

        g.dispose();
        return sprite;
    }

    public static Color randomColor(int rMin, int gMin, int bMin, int rMax, int gMax, int bMax, Random random) {
        if (rMin > rMax) { int t = rMin; rMin = rMax; rMax = t; }
        if (gMin > gMax) { int t = gMin; gMin = gMax; gMax = t; }
        if (bMin > bMax) { int t = bMin; bMin = bMax; bMax = t; }
        return new Color(
                rMin + random.nextInt(rMax - rMin + 1),
                gMin + random.nextInt(gMax - gMin + 1),
                bMin + random.nextInt(bMax - bMin + 1)
        );
    }
}