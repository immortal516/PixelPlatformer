package core;

import data.Leaderboard;
import effects.Firework;
import entities.Coin;
import entities.Danger;
import entities.Platform;
import entities.Player;
import entities.PowerUp;
import main.Game;
import utils.Assets;
import utils.SpriteGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private boolean running = false;

    private Player player;
    private ArrayList<Platform> platforms;
    private ArrayList<Coin> coins;
    private ArrayList<Coin> jetpackCoins;
    private ArrayList<Danger> dangers;
    private ArrayList<PowerUp> powerUps;
    private InputHandler input;
    private Random random;
    private Camera camera;
    private WorldGenerator worldGen;
    private ChallengeManager challenge;

    private int score = 0;
    private Font gameFont, bigFont, smallFont;

    private final int GROUND_Y = 550;
    private final int WORLD_H = 600;
    private final int PLAYER_W = 32;
    private final int PLAYER_H = 32;

    private ArrayList<Cloud> backClouds, midClouds, frontClouds;
    private Color skyColor = new Color(135, 206, 235);
    private Color hardcoreSkyColor = new Color(80, 20, 30);

    private Leaderboard leaderboard;
    private boolean newRecord = false;

    private ArrayList<Firework> fireworks;
    private boolean showFireworks = false;
    private int fireworkTimer = 0;

    private boolean hardcoreMode = false;
    private boolean hardcoreToggle = false;
    private boolean killedByDanger = false;

    private boolean magnetActive = false;
    private int magnetTimer = 0;
    private boolean jetpackActive = false;
    private int jetpackTimer = 0;
    private final int POWER_DURATION = 300;
    private double bobOffset = 0;
    private int jetpackCoinSpawnTimer = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(Game.WIDTH, Game.HEIGHT));
        setFocusable(true);

        input = new InputHandler();
        addKeyListener(input);

        random = new Random();
        platforms = new ArrayList<>();
        coins = new ArrayList<>();
        jetpackCoins = new ArrayList<>();
        dangers = new ArrayList<>();
        powerUps = new ArrayList<>();
        backClouds = new ArrayList<>();
        midClouds = new ArrayList<>();
        frontClouds = new ArrayList<>();
        fireworks = new ArrayList<>();

        camera = new Camera(Game.WIDTH, Game.HEIGHT);
        leaderboard = new Leaderboard();
        worldGen = new WorldGenerator(platforms, coins, dangers, powerUps, random);
        challenge = new ChallengeManager();

        initPlayer();
        worldGen.createInitialWorld();
        generateClouds();

        gameFont = new Font("Arial", Font.BOLD, 20);
        bigFont = new Font("Arial", Font.BOLD, 40);
        smallFont = new Font("Arial", Font.PLAIN, 14);
    }

    private void initPlayer() {
        Color[] colors = SpriteGenerator.generateRandomColors(random);
        BufferedImage sprite = SpriteGenerator.generateRandomSprite(colors);
        player = new Player(100, GROUND_Y - PLAYER_H, sprite, colors);
    }

    private void restart() {
        score = 0;
        newRecord = false;
        showFireworks = false;
        fireworks.clear();
        fireworkTimer = 0;
        killedByDanger = false;
        magnetActive = false;
        magnetTimer = 0;
        jetpackActive = false;
        jetpackTimer = 0;
        bobOffset = 0;
        jetpackCoinSpawnTimer = 0;
        hardcoreMode = hardcoreToggle;

        platforms.clear();
        coins.clear();
        jetpackCoins.clear();
        dangers.clear();
        powerUps.clear();
        backClouds.clear();
        midClouds.clear();
        frontClouds.clear();

        worldGen.reset();
        challenge.reset();
        initPlayer();
        worldGen.createInitialWorld();
        generateClouds();
    }

    public void start() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000.0 / 60.0;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            while (delta >= 1) {
                update();
                delta--;
            }
            repaint();
            try { Thread.sleep(2); } catch (InterruptedException e) {}
        }
    }

    private void update() {
        if (!challenge.isStarted()) {
            if (input.isH()) {
                hardcoreToggle = !hardcoreToggle;
                input.consumeH();
            }
        }

        if (challenge.isOver() && input.isJump()) {
            restart();
            return;
        }

        if (!challenge.isStarted() && !challenge.isOver()) {
            if (input.isJump()) {
                hardcoreMode = hardcoreToggle;
                worldGen.setHardcoreMode(hardcoreMode);
                challenge.start();
                input.consumeJump();
            }
            return;
        }

        if (challenge.isOver()) {
            if (showFireworks) updateFireworks();
            return;
        }

        challenge.update();
        if (challenge.isOver()) {
            endGame();
            return;
        }

        bobOffset += 0.05;
        player.setSpeedBoost(input.isShift());

        if (input.isLeft()) player.moveLeft();
        if (input.isRight()) player.moveRight();
        if (input.isJump() && player.isOnGround()) player.jump();

        // Джетпак
        if (jetpackActive) {
            if (!player.isOnGround()) {
                player.jetpackFly(input.isJump(), input.isDown());
            }
            player.setJetpack(true);
            jetpackTimer--;
            jetpackCoinSpawnTimer++;

            // Спавн небесных монет по всей верхней части экрана
            if (jetpackCoinSpawnTimer % 30 == 0) {
                for (int i = 0; i < 5; i++) {
                    jetpackCoins.add(new Coin(
                            player.getX() + random.nextInt(400) - 200,
                            50 + random.nextInt(200)
                    ));
                }
            }

            if (jetpackTimer <= 0) {
                jetpackActive = false;
                player.setJetpack(false);
                jetpackCoins.clear(); // удаляем все небесные монеты
                jetpackCoinSpawnTimer = 0;
            }
        }

        player.update(platforms);

        int playerRightX = player.getX() + player.getWidth();
        if (worldGen.needsGround(playerRightX)) worldGen.addGround();
        worldGen.generateMoreWorld(playerRightX);
        worldGen.generatePowerUps(score);

        // Магнит (радиус 250)
        if (magnetActive) {
            magnetTimer--;
            if (magnetTimer <= 0) {
                magnetActive = false;
                player.setMagnet(false);
            }
            // Магнитим обычные монеты
            for (int i = 0; i < coins.size(); i++) {
                Coin coin = coins.get(i);
                pullCoin(coin, 250);
            }
            // Магнитим джетпак-монеты
            for (int i = 0; i < jetpackCoins.size(); i++) {
                Coin coin = jetpackCoins.get(i);
                pullCoin(coin, 250);
            }
        }

        if (hardcoreMode) {
            worldGen.generateDangers();
            worldGen.updateDangers();
            for (int i = dangers.size() - 1; i >= 0; i--) {
                if (player.getBounds().intersects(dangers.get(i).getBounds())) {
                    killedByDanger = true;
                    challenge.forceEnd();
                    endGame();
                    return;
                }
            }
        }

        worldGen.cleanUp(camera.getX() - 600);
        camera.update(player.getX() + PLAYER_W / 2);
        updateClouds();

        // Сбор обычных монет
        for (int i = coins.size() - 1; i >= 0; i--) {
            Coin coin = coins.get(i);
            coin.update();
            if (player.getBounds().intersects(coin.getBounds())) {
                coins.remove(i);
                score += hardcoreMode ? 2 : 1;
            }
        }

        // Сбор джетпак-монет
        for (int i = jetpackCoins.size() - 1; i >= 0; i--) {
            Coin coin = jetpackCoins.get(i);
            coin.update();
            if (player.getBounds().intersects(coin.getBounds())) {
                jetpackCoins.remove(i);
                score += hardcoreMode ? 4 : 2;
            }
        }

        // Сбор бонусов
        for (int i = powerUps.size() - 1; i >= 0; i--) {
            PowerUp pu = powerUps.get(i);
            pu.update();
            if (player.getBounds().intersects(pu.getBounds())) {
                applyPowerUp(pu);
                powerUps.remove(i);
            }
        }

        if (player.getY() > WORLD_H + 100) {
            player.respawn(100, GROUND_Y - PLAYER_H);
        }
    }

    private void pullCoin(Coin coin, int radius) {
        int dx = player.getX() + PLAYER_W/2 - coin.getX();
        int dy = player.getY() + PLAYER_H/2 - (int)coin.getBounds().getCenterY();
        double dist = Math.sqrt(dx*dx + dy*dy);
        if (dist < radius && dist > 5) {
            coin.magnetPull(dx / dist * 6, dy / dist * 6); // увеличено с 4 до 6
        }
    }

    private void applyPowerUp(PowerUp pu) {
        switch (pu.getType()) {
            case PowerUp.TIME_BONUS -> challenge.addTime(5);
            case PowerUp.MAGNET -> {
                magnetActive = true;
                magnetTimer = POWER_DURATION;
                player.setMagnet(true);
            }
            case PowerUp.JETPACK -> {
                jetpackActive = true;
                jetpackTimer = POWER_DURATION;
                player.setJetpack(true);
                jetpackCoinSpawnTimer = 0;
                jetpackCoins.clear();
            }
        }
    }

    private void endGame() {
        challenge.forceEnd();
        jetpackCoins.clear();
        jetpackActive = false;
        player.setJetpack(false);
        magnetActive = false;
        player.setMagnet(false);
        newRecord = leaderboard.addScore(score);
        if (newRecord) {
            showFireworks = true;
            fireworks.clear();
            for (int i = 0; i < 30; i++) {
                fireworks.add(new Firework(
                        random.nextInt(Game.WIDTH), Game.HEIGHT - random.nextInt(200),
                        SpriteGenerator.randomColor(100, 100, 100, 255, 255, 255, random), random
                ));
            }
        }
    }

    private void updateFireworks() {
        fireworkTimer++;
        for (int i = 0; i < fireworks.size(); i++) fireworks.get(i).update();
        for (int i = fireworks.size() - 1; i >= 0; i--) {
            if (fireworks.get(i).isDead()) fireworks.remove(i);
        }
        if (fireworkTimer % 20 == 0 && fireworks.size() < 50 && fireworkTimer < 300) {
            for (int i = 0; i < 5; i++) {
                fireworks.add(new Firework(
                        random.nextInt(Game.WIDTH), Game.HEIGHT - random.nextInt(200),
                        SpriteGenerator.randomColor(100, 100, 100, 255, 255, 255, random), random
                ));
            }
        }
    }

    private void generateClouds() {
        for (int i = 0; i < 8; i++)
            backClouds.add(new Cloud(random.nextInt(Game.WIDTH + 400) - 200, random.nextInt(150), 120, 60));
        for (int i = 0; i < 6; i++)
            midClouds.add(new Cloud(random.nextInt(Game.WIDTH + 400) - 200, random.nextInt(180), 120, 60));
        for (int i = 0; i < 4; i++)
            frontClouds.add(new Cloud(random.nextInt(Game.WIDTH + 400) - 200, random.nextInt(200), 120, 60));
    }

    private void updateClouds() {
        updateCloudLayer(backClouds);
        updateCloudLayer(midClouds);
        updateCloudLayer(frontClouds);
    }

    private void updateCloudLayer(ArrayList<Cloud> clouds) {
        for (int i = clouds.size() - 1; i >= 0; i--) {
            Cloud c = clouds.get(i);
            c.x -= 0.3;
            if (c.x + c.width < -200) {
                c.x = camera.getX() + Game.WIDTH + random.nextInt(300);
                c.y = random.nextInt(200);
            }
            if (c.x > camera.getX() + Game.WIDTH + 400) {
                c.x = camera.getX() - random.nextInt(300);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(hardcoreMode ? hardcoreSkyColor : skyColor);
        g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        int camX = camera.getX();
        BufferedImage mountains = hardcoreMode ? Assets.mountainDarkBg : Assets.mountainBg;
        BufferedImage trees = hardcoreMode ? Assets.treeDeadBg : Assets.treeBg;

        int mountainOffset = -(int)(camX * 0.1f) % 800;
        for (int i = -800; i < Game.WIDTH + 800; i += 800)
            g2d.drawImage(mountains, mountainOffset + i, 200, null);

        int treeOffset = -(int)(camX * 0.3f) % 800;
        for (int i = -800; i < Game.WIDTH + 800; i += 800)
            g2d.drawImage(trees, treeOffset + i, 300, null);

        drawCloudLayer(g2d, backClouds, 0.2f);
        drawCloudLayer(g2d, midClouds, 0.5f);
        drawCloudLayer(g2d, frontClouds, 0.8f);

        if (!challenge.isStarted() && !challenge.isOver()) {
            int signX = 180 - camX;
            if (signX > -100 && signX < Game.WIDTH + 100) {
                int signY = GROUND_Y - 70;
                g2d.setColor(new Color(101, 67, 33));
                g2d.fillRect(signX + 8, signY + 20, 4, 50);
                g2d.setColor(new Color(160, 120, 60));
                g2d.fillRect(signX, signY, 55, 22);
                g2d.setColor(new Color(100, 70, 30));
                g2d.drawRect(signX, signY, 55, 22);
                g2d.setColor(Color.WHITE);
                g2d.setFont(smallFont);
                g2d.drawString("RUN →", signX + 6, signY + 16);
            }
        }

        for (int i = 0; i < platforms.size(); i++) platforms.get(i).draw(g2d, camX);
        for (int i = 0; i < coins.size(); i++) coins.get(i).draw(g2d, camX);
        for (int i = 0; i < jetpackCoins.size(); i++) jetpackCoins.get(i).draw(g2d, camX);
        for (int i = 0; i < dangers.size(); i++) dangers.get(i).draw(g2d, camX);
        for (int i = 0; i < powerUps.size(); i++) powerUps.get(i).draw(g2d, camX);
        player.draw(g2d, camX);

        g2d.setColor(Color.BLACK);
        g2d.setFont(gameFont);
        if (!challenge.isStarted() && !challenge.isOver()) {
            g2d.drawString("Нажми ПРОБЕЛ/W чтобы начать!", Game.WIDTH / 2 - 170, Game.HEIGHT / 2);
            g2d.setFont(smallFont);
            g2d.drawString("H - хардкор (шипы + x2): " + (hardcoreToggle ? "ВКЛ" : "ВЫКЛ"),
                    Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 30);
            g2d.drawString("SHIFT - ускорение | S - вниз (джетпак)", Game.WIDTH / 2 - 120, Game.HEIGHT / 2 + 50);
        }
        g2d.setFont(gameFont);
        g2d.drawString("Монетки: " + score, 10, 30);
        g2d.drawString("Время: " + challenge.getTimeLeft() + "с", Game.WIDTH - 150, 30);
        if (hardcoreMode && challenge.isStarted() && !challenge.isOver()) {
            g2d.setColor(Color.RED);
            g2d.setFont(smallFont);
            g2d.drawString("HARDCORE", Game.WIDTH - 100, 50);
        }
        if (magnetActive) {
            g2d.setColor(Color.RED);
            g2d.setFont(smallFont);
            g2d.drawString("MAGNET " + (magnetTimer/60) + "s", 10, 50);
            g2d.setColor(new Color(255, 0, 0, 40));
            g2d.fillOval(player.getX() - camX - 125, player.getY() - 125, 282, 282);
        }
        if (jetpackActive) {
            g2d.setColor(Color.ORANGE);
            g2d.setFont(smallFont);
            g2d.drawString("JETPACK " + (jetpackTimer/60) + "s", 10, 70);
        }

        if (challenge.isOver()) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            g2d.setFont(bigFont);
            if (killedByDanger) {
                g2d.setColor(Color.RED);
                g2d.drawString("ВЫ ПОГИБЛИ!", Game.WIDTH / 2 - 170, Game.HEIGHT / 2 - 80);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.drawString("ВРЕМЯ ВЫШЛО!", Game.WIDTH / 2 - 180, Game.HEIGHT / 2 - 80);
            }
            g2d.setColor(Color.WHITE);
            g2d.setFont(gameFont);
            g2d.drawString("Собрано монет: " + score, Game.WIDTH / 2 - 80, Game.HEIGHT / 2 - 40);
            if (newRecord) {
                g2d.setColor(Color.YELLOW);
                g2d.drawString("НОВЫЙ РЕКОРД!", Game.WIDTH / 2 - 90, Game.HEIGHT / 2 - 10);
            }
            g2d.setColor(Color.WHITE);
            g2d.drawString("Лидеры:", Game.WIDTH / 2 - 40, Game.HEIGHT / 2 + 25);
            int[] top = leaderboard.getTop5();
            for (int i = 0; i < top.length; i++) {
                if (top[i] > 0) {
                    g2d.drawString((i + 1) + ". " + top[i], Game.WIDTH / 2 - 30, Game.HEIGHT / 2 + 50 + i * 25);
                }
            }
            g2d.setColor(Color.GREEN);
            g2d.drawString("Нажми ПРОБЕЛ чтобы сыграть ещё", Game.WIDTH / 2 - 150, Game.HEIGHT - 70);
            g2d.setColor(Color.YELLOW);
            g2d.setFont(smallFont);
            g2d.drawString("H - сменить режим: " + (hardcoreToggle ? "HARDCORE" : "обычный"),
                    Game.WIDTH / 2 - 100, Game.HEIGHT - 45);
        }

        if (showFireworks) {
            for (int i = 0; i < fireworks.size(); i++) fireworks.get(i).draw(g2d);
        }
    }

    private void drawCloudLayer(Graphics2D g, ArrayList<Cloud> clouds, float factor) {
        for (int i = 0; i < clouds.size(); i++) {
            Cloud c = clouds.get(i);
            int sx = (int)(c.x - camera.getX() * factor);
            if (sx > -200 && sx < Game.WIDTH + 200) {
                if (hardcoreMode) {
                    g.setColor(new Color(60, 20, 20, 180));
                    g.fillOval(sx, c.y, c.width, c.height);
                } else {
                    g.drawImage(Assets.cloudImage, sx, c.y, null);
                }
            }
        }
    }

    class Cloud {
        int x, y, width, height;
        Cloud(int x, int y, int w, int h) { this.x = x; this.y = y; this.width = w; this.height = h; }
    }
}