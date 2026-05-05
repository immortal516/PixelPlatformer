package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Assets {
    public static BufferedImage groundTile;
    public static BufferedImage platformTile;
    public static BufferedImage mountainBg;
    public static BufferedImage treeBg;
    public static BufferedImage cloudImage;
    public static BufferedImage mountainDarkBg;
    public static BufferedImage treeDeadBg;

    private static final Random random = new Random();

    public static void init() {
        groundTile = generateGroundTile();
        platformTile = generatePlatformTile();
        mountainBg = generateMountains();
        treeBg = generateTrees();
        cloudImage = generateCloud();
        mountainDarkBg = generateDarkMountains();
        treeDeadBg = generateDeadTrees();
    }

    public static BufferedImage generateCloud() {
        BufferedImage cloud = new BufferedImage(120, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = cloud.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g.setColor(new Color(255, 255, 255, 220));
        g.fillOval(0, 20, 40, 35);
        g.fillOval(25, 10, 50, 45);
        g.fillOval(60, 15, 50, 40);
        g.fillOval(85, 20, 35, 30);

        g.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 30; i++) {
            int px = 10 + random.nextInt(100);
            int py = 15 + random.nextInt(35);
            g.fillRect(px, py, 3, 2);
        }

        g.dispose();
        return cloud;
    }

    private static BufferedImage generateGroundTile() {
        BufferedImage tile = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tile.createGraphics();

        g.setColor(new Color(101, 67, 33));
        g.fillRect(0, 0, 64, 64);

        for (int y = 0; y < 64; y += 4) {
            int shade = random.nextInt(16) - 8;
            g.setColor(new Color(clamp(101 + shade), clamp(67 + shade), clamp(33 + shade)));
            g.fillRect(0, y, 64, 3);
        }

        g.setColor(new Color(80, 50, 20));
        for (int i = 0; i < 8; i++) {
            int px = random.nextInt(58);
            int py = 10 + random.nextInt(50);
            g.fillRect(px, py, 3 + random.nextInt(3), 2 + random.nextInt(3));
        }

        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 0, 64, 8);
        g.setColor(new Color(0, 180, 0));
        for (int x = 0; x < 64; x += 4) {
            int h = 3 + random.nextInt(5);
            g.fillRect(x, 0, 3, h);
        }

        g.dispose();
        return tile;
    }

    private static BufferedImage generatePlatformTile() {
        BufferedImage tile = new BufferedImage(64, 15, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tile.createGraphics();

        g.setColor(new Color(139, 90, 43));
        g.fillRect(0, 0, 64, 15);

        g.setColor(new Color(120, 75, 35));
        g.drawLine(0, 5, 64, 5);
        g.drawLine(0, 10, 64, 10);

        for (int x = 16; x < 64; x += 16) {
            g.setColor(new Color(100, 60, 30));
            g.fillRect(x, 0, 2, 15);
        }

        g.setColor(new Color(160, 110, 60));
        for (int i = 0; i < 15; i++) {
            g.fillRect(random.nextInt(62), random.nextInt(13), 2, 1);
        }

        g.setColor(new Color(180, 180, 180));
        g.fillRect(4, 4, 4, 4);
        g.fillRect(56, 4, 4, 4);
        g.setColor(new Color(220, 220, 220));
        g.fillRect(5, 5, 2, 2);
        g.fillRect(57, 5, 2, 2);

        g.dispose();
        return tile;
    }

    private static BufferedImage generateMountains() {
        BufferedImage img = new BufferedImage(800, 350, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setColor(new Color(160, 180, 200, 200));
        int[] x1 = {0, 80, 160, 260, 380, 480, 580, 700, 800};
        int[] y1 = {350, 220, 260, 150, 240, 170, 230, 190, 350};
        g.fillPolygon(x1, y1, 9);

        g.setColor(new Color(100, 120, 150, 220));
        int[] x2 = {0, 60, 150, 280, 400, 500, 620, 750, 800};
        int[] y2 = {350, 240, 280, 180, 260, 200, 250, 210, 350};
        g.fillPolygon(x2, y2, 9);

        g.setColor(new Color(255, 255, 255, 180));
        g.fillPolygon(new int[]{250, 260, 270}, new int[]{165, 145, 165}, 3);
        g.fillPolygon(new int[]{490, 500, 510}, new int[]{195, 175, 195}, 3);

        g.dispose();
        return img;
    }

    private static BufferedImage generateTrees() {
        BufferedImage img = new BufferedImage(800, 280, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        for (int i = 0; i < 15; i++) {
            int tx = i * 55 + random.nextInt(20);
            int treeH = 50 + random.nextInt(70);

            g.setColor(new Color(70, 45, 25, 200));
            g.fillRect(tx + 14, 280 - treeH + 15, 8, treeH - 15);

            g.setColor(new Color(25, 100, 25, 200));
            int[] cx1 = {tx, tx + 18, tx + 36};
            int[] cy1 = {280 - treeH + 25, 280 - treeH - 5, 280 - treeH + 25};
            g.fillPolygon(cx1, cy1, 3);

            g.setColor(new Color(20, 80, 20, 200));
            int[] cx2 = {tx + 5, tx + 18, tx + 31};
            int[] cy2 = {280 - treeH + 45, 280 - treeH + 15, 280 - treeH + 45};
            g.fillPolygon(cx2, cy2, 3);
        }

        g.dispose();
        return img;
    }

    private static BufferedImage generateDarkMountains() {
        BufferedImage img = new BufferedImage(800, 350, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setColor(new Color(60, 30, 40, 200));
        int[] x1 = {0, 100, 200, 320, 450, 550, 680, 800};
        int[] y1 = {350, 220, 260, 150, 240, 170, 230, 350};  // убрал один элемент
        g.fillPolygon(x1, y1, 8);  // 8 точек

        g.setColor(new Color(40, 20, 30, 230));
        int[] x2 = {0, 80, 180, 300, 420, 520, 650, 800};
        int[] y2 = {350, 240, 280, 180, 260, 200, 250, 350};  // убрал один элемент
        g.fillPolygon(x2, y2, 8);  // 8 точек

        g.setColor(new Color(100, 30, 30, 180));
        g.fillPolygon(new int[]{250, 260, 270}, new int[]{165, 145, 165}, 3);

        g.dispose();
        return img;
    }

    private static BufferedImage generateDeadTrees() {
        BufferedImage img = new BufferedImage(800, 280, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        for (int i = 0; i < 12; i++) {
            int tx = i * 70 + random.nextInt(30);
            int treeH = 40 + random.nextInt(50);

            g.setColor(new Color(40, 25, 25, 200));
            g.fillRect(tx + 14, 280 - treeH + 15, 6, treeH - 15);

            g.setColor(new Color(50, 30, 30, 180));
            g.fillRect(tx + 8, 280 - treeH + 10, 20, 3);
            g.fillRect(tx + 12, 280 - treeH + 25, 16, 2);
        }

        g.dispose();
        return img;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}