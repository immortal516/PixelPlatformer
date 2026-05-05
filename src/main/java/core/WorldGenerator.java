package core;

import entities.Coin;
import entities.Danger;
import entities.Platform;
import entities.PowerUp;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class WorldGenerator {
    private final ArrayList<Platform> platforms;
    private final ArrayList<Coin> coins;
    private final ArrayList<Danger> dangers;
    private final ArrayList<PowerUp> powerUps;
    private final Random random;
    private static final int GROUND_Y = 550;
    private int worldWidth = 10000;
    private int lastGeneratedX = 800;
    private boolean jetpackSpawned = false;
    private boolean hardcoreMode = false;

    public WorldGenerator(ArrayList<Platform> platforms, ArrayList<Coin> coins,
                          ArrayList<Danger> dangers, ArrayList<PowerUp> powerUps, Random random) {
        this.platforms = platforms;
        this.coins = coins;
        this.dangers = dangers;
        this.powerUps = powerUps;
        this.random = random;
    }

    public void setHardcoreMode(boolean mode) { this.hardcoreMode = mode; }

    public void createInitialWorld() {
        platforms.clear();
        platforms.add(new Platform(0, GROUND_Y, 10000, 50));
        int prevRightX = 200;
        int prevY = GROUND_Y;
        lastGeneratedX = 800;
        jetpackSpawned = false;
        for (int i = 0; i < 12; i++) {
            Platform p = placeNextPlatformFrom(prevRightX, prevY);
            if (p != null) {
                prevRightX = p.getX() + p.getWidth() + 50;
                prevY = p.getBounds().y;
                placeCoinsAbovePlatform(p);
            } else {
                prevRightX += 150;
                prevY = GROUND_Y;
            }
        }
    }

    public void generateMoreWorld(int playerRightX) {
        if (playerRightX <= lastGeneratedX - 700) return;
        int lastY = GROUND_Y;
        for (int i = platforms.size() - 1; i >= 0; i--) {
            Platform p = platforms.get(i);
            if (p.getWidth() < 500 && p.getX() > lastGeneratedX - 200) {
                lastY = p.getBounds().y;
                break;
            }
        }
        int nextX = lastGeneratedX + 100;
        for (int i = 0; i < 4; i++) {
            Platform p = placeNextPlatformFrom(nextX, lastY);
            if (p != null) {
                placeCoinsAbovePlatform(p);
                nextX = p.getX() + p.getWidth() + 50;
                lastY = p.getBounds().y;
            } else {
                nextX += 150;
                lastY = GROUND_Y;
            }
        }
        lastGeneratedX += 700;
    }

    public void generateDangers() {
        for (Platform p : platforms) {
            if (p.getWidth() < 500 && random.nextInt(100) < 2) {
                dangers.add(new Danger(
                        p.getX() + random.nextInt(Math.max(1, p.getWidth())),
                        p.getBounds().y + p.getBounds().height + 2
                ));
            }
        }
    }

    public void generatePowerUps(int score) {
        int required = hardcoreMode ? 70 : 30;
        if (!jetpackSpawned && score >= required) {
            // Спавним джетпак на первой подходящей платформе
            for (Platform p : platforms) {
                if (p.getWidth() < 500 && p.getX() > 200 && p.getX() < 2000) {
                    powerUps.add(new PowerUp(p.getX() + p.getWidth()/2, p.getBounds().y - 25, PowerUp.JETPACK));
                    jetpackSpawned = true;
                    break;
                }
            }
        }
        if (random.nextInt(400) < 1) {
            for (Platform p : platforms) {
                if (p.getWidth() < 500 && random.nextInt(10) < 3) {
                    powerUps.add(new PowerUp(p.getX() + p.getWidth()/2, p.getBounds().y - 25, random.nextInt(3)));
                    break;
                }
            }
        }
    }

    public void updateDangers() {
        for (int i = dangers.size() - 1; i >= 0; i--) {
            Danger d = dangers.get(i);
            d.update();
            if (!d.isActive()) dangers.remove(i);
        }
    }

    public boolean needsGround(int playerRightX) { return playerRightX > worldWidth - 1500; }

    public void addGround() {
        platforms.add(new Platform(worldWidth, GROUND_Y, 3000, 50));
        worldWidth += 3000;
    }

    public void cleanUp(int limit) {
        platforms.removeIf(p -> p.getX() + p.getWidth() < limit && p.getWidth() < 500);
        coins.removeIf(c -> c.getX() < limit);
        dangers.removeIf(d -> d.getX() < limit);
        powerUps.removeIf(pu -> pu.getX() < limit);
    }

    public void reset() {
        worldWidth = 10000;
        lastGeneratedX = 800;
        jetpackSpawned = false;
    }

    private Platform placeNextPlatformFrom(int startX, int prevPlatY) {
        int platWidth = 50 + random.nextInt(60);
        int gap = 80 + random.nextInt(100);
        int platX = startX + gap;
        int platY = GROUND_Y - 38 - random.nextInt(64);
        if (platY > GROUND_Y - 55) platY = GROUND_Y - 55;
        if (platY < 100) platY = 100;
        for (int attempt = 0; attempt < 10; attempt++) {
            Platform p = new Platform(platX, platY, platWidth, 15);
            if (!platformOverlaps(p)) { platforms.add(p); return p; }
            platX += 20;
            platY = prevPlatY - 30 - random.nextInt(70);
            if (platY > GROUND_Y - 55) platY = GROUND_Y - 55;
            if (platY < 80) platY = 80;
        }
        return null;
    }

    private boolean platformOverlaps(Platform newPlat) {
        Rectangle nr = newPlat.getBounds();
        Rectangle nrWithGap = new Rectangle(nr.x - 10, nr.y - 55, nr.width + 20, nr.height + 110);
        for (Platform p : platforms) {
            if (p.getWidth() >= 500) continue;
            if (nrWithGap.intersects(p.getBounds())) return true;
        }
        return false;
    }

    private void placeCoinsAbovePlatform(Platform plat) {
        int count = 1 + random.nextInt(2);
        int topY = plat.getBounds().y;
        int left = plat.getX();
        int w = plat.getWidth();
        for (int i = 0; i < count; i++) {
            int cx = left + 8 + random.nextInt(Math.max(1, w - 16));
            int cy = topY - 15 - random.nextInt(30);
            if (cy < 60) cy = 60;
            if (!isCoinColliding(cx, cy)) coins.add(new Coin(cx, cy));
        }
    }

    private boolean isCoinColliding(int x, int y) {
        Rectangle cr = new Rectangle(x - 8, y - 10, 16, 20);
        for (Platform p : platforms) if (cr.intersects(p.getBounds())) return true;
        return false;
    }
}