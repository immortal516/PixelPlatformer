package entities;

import utils.Assets;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Platform {
    private int x, y, width, height;
    private boolean isGround;

    public Platform(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isGround = (width >= 500);
    }

    public boolean isGround() { return isGround; }

    public void draw(Graphics2D g, int camX) {
        int screenX = x - camX;

        if (isGround) {
            BufferedImage tile = Assets.groundTile;
            int tw = tile.getWidth();
            int th = tile.getHeight();
            for (int tx = screenX; tx < screenX + width; tx += tw) {
                for (int ty = y; ty < y + height; ty += th) {
                    g.drawImage(tile, tx, ty,
                            Math.min(tw, screenX + width - tx),
                            Math.min(th, y + height - ty), null);
                }
            }
        } else {
            BufferedImage tile = Assets.platformTile;
            int tw = tile.getWidth();
            for (int tx = screenX; tx < screenX + width; tx += tw) {
                g.drawImage(tile, tx, y,
                        Math.min(tw, screenX + width - tx), height, null);
            }
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() { return x; }
    public int getWidth() { return width; }
}