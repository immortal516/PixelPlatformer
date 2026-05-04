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
    private InputHandler input;
    private Random random;
    private Camera camera;

    private int score = 0;
    private Font gameFont;
    private Font bigFont;

    private int worldWidth = 10000;
    private final int GROUND_Y = 550;
    private final int WORLD_H = 600;

    private ArrayList<Cloud> backClouds;
    private ArrayList<Cloud> midClouds;
    private ArrayList<Cloud> frontClouds;

    private Color skyColor = new Color(135, 206, 235);
    private int lastGeneratedX = 800;

    private final int PLAYER_W = 32;
    private final int PLAYER_H = 32;

    // Челлендж
    private boolean challengeStarted = false;
    private boolean gameOver = false;
    private long startTime;
    private long timeLeft = 60;
    private final long CHALLENGE_DURATION = 60;

    // Лидерборд
    private Leaderboard leaderboard;
    private boolean newRecord = false;

    // Салют
    private ArrayList<Firework> fireworks;
    private boolean showFireworks = false;
    private int fireworkTimer = 0;

    // Для рестарта
    private boolean restartRequested = false;
    private Color[] savedColors;  // сохраняем цвета для рестарта

    public GamePanel() {
        setPreferredSize(new Dimension(Game.WIDTH, Game.HEIGHT));
        setFocusable(true);

        input = new InputHandler();
        addKeyListener(input);

        random = new Random();
        platforms = new ArrayList<>();
        coins = new ArrayList<>();
        backClouds = new ArrayList<>();
        midClouds = new ArrayList<>();
        frontClouds = new ArrayList<>();
        fireworks = new ArrayList<>();

        camera = new Camera(Game.WIDTH, Game.HEIGHT);
        leaderboard = new Leaderboard();

        savedColors = generateRandomColors();
        BufferedImage playerSprite = generateRandomSprite(savedColors);

        player = new Player(100, GROUND_Y - PLAYER_H, playerSprite, savedColors);
        createInitialWorld();
        gameFont = new Font("Arial", Font.BOLD, 20);
        bigFont = new Font("Arial", Font.BOLD, 40);
    }

    // ===== ГЕНЕРАТОР СПРАЙТА =====
    private Color[] generateRandomColors() {
        return new Color[] {
                randomColor(255, 180, 140, 255, 220, 180),
                randomColor(50, 30, 20, 200, 150, 100),
                randomColor(100, 50, 50, 255, 100, 100),
                randomColor(30, 30, 80, 100, 100, 200)
        };
    }

    private BufferedImage generateRandomSprite(Color[] colors) {
        int size = 32;
        BufferedImage sprite = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sprite.createGraphics();
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, size, size);
        Color skinColor = colors[0];
        Color hairColor = colors[1];
        Color shirtColor = colors[2];
        Color pantsColor = colors[3];
        int cx = size / 2;
        int cy = size / 2;
        g.setColor(skinColor);
        g.fillRect(cx - 6, cy - 12, 12, 10);
        g.setColor(hairColor);
        g.fillRect(cx - 7, cy - 14, 14, 5);
        g.setColor(Color.WHITE);
        g.fillRect(cx - 4, cy - 10, 3, 3);
        g.fillRect(cx + 1, cy - 10, 3, 3);
        g.setColor(Color.BLACK);
        g.fillRect(cx - 3, cy - 9, 2, 2);
        g.fillRect(cx + 2, cy - 9, 2, 2);
        g.setColor(shirtColor);
        g.fillRect(cx - 5, cy - 2, 10, 8);
        g.setColor(skinColor);
        g.fillRect(cx - 8, cy - 1, 3, 6);
        g.fillRect(cx + 5, cy - 1, 3, 6);
        g.setColor(pantsColor);
        g.fillRect(cx - 4, cy + 6, 3, 6);
        g.fillRect(cx + 1, cy + 6, 3, 6);
        g.dispose();
        return sprite;
    }

    private Color randomColor(int rMin, int gMin, int bMin, int rMax, int gMax, int bMax) {
        if (rMin > rMax) { int t = rMin; rMin = rMax; rMax = t; }
        if (gMin > gMax) { int t = gMin; gMin = gMax; gMax = t; }
        if (bMin > bMax) { int t = bMin; bMin = bMax; bMax = t; }
        int r = rMin + random.nextInt(rMax - rMin + 1);
        int g = gMin + random.nextInt(gMax - gMin + 1);
        int b = bMin + random.nextInt(bMax - bMin + 1);
        return new Color(r, g, b);
    }

    // ===== МИР =====
    private void createInitialWorld() {
        platforms.add(new Platform(0, GROUND_Y, 10000, 50));
        int prevRightX = 300;
        int prevY = GROUND_Y;
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
        generateClouds();
    }

    private Platform placeNextPlatformFrom(int startX, int prevPlatY) {
        int platWidth = 50 + random.nextInt(60);
        int gap = 80 + random.nextInt(100);
        int platX = startX + gap;
        int platY = GROUND_Y - 38 - random.nextInt(64);
        if (platY > GROUND_Y - 55) platY = GROUND_Y - 55;
        if (platY < 100) platY = 100;
        if (platY > GROUND_Y - 20) platY = GROUND_Y - 55;
        for (int attempt = 0; attempt < 10; attempt++) {
            Platform p = new Platform(platX, platY, platWidth, 15);
            if (!platformOverlaps(p)) {
                platforms.add(p);
                return p;
            }
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
        int platTopY = plat.getBounds().y;
        int platLeft = plat.getX();
        int platWidth = plat.getWidth();
        for (int i = 0; i < count; i++) {
            int cx = platLeft + 8 + random.nextInt(Math.max(1, platWidth - 16));
            int cy = platTopY - 15 - random.nextInt(30);
            if (cy < 60) cy = 60;
            if (!isCoinColliding(cx, cy)) {
                coins.add(new Coin(cx, cy));
            }
        }
    }

    // ===== РЕСТАРТ =====
    private void restart() {
        score = 0;
        challengeStarted = false;
        gameOver = false;
        timeLeft = CHALLENGE_DURATION;
        newRecord = false;
        showFireworks = false;
        fireworks.clear();
        fireworkTimer = 0;
        worldWidth = 10000;
        lastGeneratedX = 800;

        platforms.clear();
        coins.clear();
        backClouds.clear();
        midClouds.clear();
        frontClouds.clear();

        // Генерируем новые цвета и спрайт
        savedColors = generateRandomColors();
        BufferedImage newSprite = generateRandomSprite(savedColors);
        player = new Player(100, GROUND_Y - PLAYER_H, newSprite, savedColors);
        player.respawn(100, GROUND_Y - PLAYER_H);

        createInitialWorld();
        restartRequested = false;
    }

    // =================================

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
        // Проверка рестарта
        if (gameOver && input.isJump()) {
            restart();
            return;
        }

        // Старт челленджа
        if (!challengeStarted && !gameOver) {
            if (input.isJump()) {
                challengeStarted = true;
                startTime = System.currentTimeMillis();
                timeLeft = CHALLENGE_DURATION;
                input.consumeJump();
            }
            return;
        }

        // Игра окончена
        if (gameOver) {
            if (showFireworks) {
                updateFireworks();
            }
            return;
        }

        // Таймер
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        timeLeft = CHALLENGE_DURATION - elapsed;
        if (timeLeft <= 0) {
            timeLeft = 0;
            endGame();
            return;
        }

        // Игровой процесс
        if (input.isLeft()) player.moveLeft();
        if (input.isRight()) player.moveRight();
        if (input.isJump() && player.isOnGround()) player.jump();

        player.update(platforms);

        int playerRightX = player.getX() + player.getWidth();
        if (playerRightX > worldWidth - 1500) {
            platforms.add(new Platform(worldWidth, GROUND_Y, 3000, 50));
            worldWidth += 3000;
        }
        if (playerRightX > lastGeneratedX - 700) {
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

        cleanUpWorld();
        camera.update(player.getX() + PLAYER_W/2, player.getY() + PLAYER_H/2);
        updateClouds(backClouds);
        updateClouds(midClouds);
        updateClouds(frontClouds);

        for (int i = coins.size() - 1; i >= 0; i--) {
            Coin coin = coins.get(i);
            coin.update();
            if (player.getBounds().intersects(coin.getBounds())) {
                coins.remove(i);
                score++;
            }
        }

        if (player.getY() > WORLD_H + 100) {
            player.respawn(100, GROUND_Y - PLAYER_H);
        }
    }

    private void endGame() {
        gameOver = true;
        challengeStarted = false;
        newRecord = leaderboard.addScore(score);
        if (newRecord) {
            showFireworks = true;
            fireworks.clear();
            for (int i = 0; i < 30; i++) {
                fireworks.add(new Firework(
                        random.nextInt(Game.WIDTH),
                        Game.HEIGHT - random.nextInt(200),
                        randomColor(100,100,100,255,255,255),
                        random
                ));
            }
        }
    }

    private void updateFireworks() {
        fireworkTimer++;
        for (Firework f : fireworks) {
            f.update();
        }
        fireworks.removeIf(f -> f.isDead());
        if (fireworkTimer % 20 == 0 && fireworks.size() < 50 && fireworkTimer < 300) {
            for (int i = 0; i < 5; i++) {
                fireworks.add(new Firework(
                        random.nextInt(Game.WIDTH),
                        Game.HEIGHT - random.nextInt(200),
                        randomColor(100,100,100,255,255,255),
                        random
                ));
            }
        }
    }

    private void cleanUpWorld() {
        int limit = camera.getX() - 600;
        platforms.removeIf(p -> p.getX() + p.getWidth() < limit && p.getWidth() < 500);
        coins.removeIf(c -> c.getX() < limit);
    }

    private void generateClouds() {
        for (int i = 0; i < 15; i++)
            backClouds.add(new Cloud(random.nextInt(2000), random.nextInt(150), 60+random.nextInt(40), 20+random.nextInt(20)));
        for (int i = 0; i < 10; i++)
            midClouds.add(new Cloud(random.nextInt(2000), random.nextInt(200), 80+random.nextInt(60), 20+random.nextInt(30)));
        for (int i = 0; i < 7; i++)
            frontClouds.add(new Cloud(random.nextInt(2000), random.nextInt(250), 100+random.nextInt(80), 30+random.nextInt(40)));
    }

    private void updateClouds(ArrayList<Cloud> clouds) {
        for (Cloud c : clouds) {
            if (c.x + c.width < camera.getX()) {
                c.x = camera.getX() + Game.WIDTH + random.nextInt(300);
                c.y = random.nextInt(250);
            }
        }
    }

    private boolean isCoinColliding(int x, int y) {
        Rectangle cr = new Rectangle(x-8, y-10, 16, 20);
        for (Platform p : platforms) {
            if (cr.intersects(p.getBounds())) return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(skyColor);
        g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);

        drawCloudLayer(g2d, backClouds, 0.2f);
        drawCloudLayer(g2d, midClouds, 0.5f);
        drawCloudLayer(g2d, frontClouds, 0.8f);

        for (Platform p : platforms)
            p.draw(g2d, camera.getX(), camera.getY());
        for (Coin c : coins)
            c.draw(g2d, camera.getX(), camera.getY());
        player.draw(g2d, camera.getX(), camera.getY());

        g2d.setColor(Color.BLACK);
        g2d.setFont(gameFont);
        if (!challengeStarted && !gameOver) {
            g2d.drawString("Нажми ПРОБЕЛ/W чтобы начать!", Game.WIDTH/2 - 170, Game.HEIGHT/2);
        }
        g2d.drawString("Монетки: " + score, 10, 30);
        g2d.drawString("Время: " + timeLeft + "с", Game.WIDTH - 150, 30);

        if (gameOver) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
            g2d.setColor(Color.WHITE);
            g2d.setFont(bigFont);
            g2d.drawString("ВРЕМЯ ВЫШЛО!", Game.WIDTH/2 - 180, Game.HEIGHT/2 - 60);
            g2d.setFont(gameFont);
            g2d.drawString("Собрано монет: " + score, Game.WIDTH/2 - 80, Game.HEIGHT/2 - 20);

            if (newRecord) {
                g2d.setColor(Color.YELLOW);
                g2d.drawString("НОВЫЙ РЕКОРД!", Game.WIDTH/2 - 90, Game.HEIGHT/2 + 20);
            }

            g2d.setColor(Color.WHITE);
            g2d.drawString("Лидеры:", Game.WIDTH/2 - 40, Game.HEIGHT/2 + 60);
            int[] top = leaderboard.getTop5();
            for (int i = 0; i < top.length; i++) {
                if (top[i] > 0) {
                    g2d.drawString((i+1) + ". " + top[i], Game.WIDTH/2 - 30, Game.HEIGHT/2 + 85 + i * 25);
                }
            }
            g2d.setColor(Color.GREEN);
            g2d.drawString("Нажми ПРОБЕЛ чтобы сыграть ещё", Game.WIDTH/2 - 150, Game.HEIGHT - 50);
        }

        if (showFireworks) {
            for (Firework f : fireworks) {
                f.draw(g2d);
            }
        }
    }

    private void drawCloudLayer(Graphics2D g, ArrayList<Cloud> clouds, float factor) {
        for (Cloud c : clouds) {
            int sx = (int)(c.x - camera.getX() * factor);
            g.setColor(new Color(255,255,255,180));
            g.fillOval(sx, c.y, c.width, c.height);
        }
    }

    class Cloud {
        int x, y, width, height;
        Cloud(int x, int y, int w, int h) { this.x=x; this.y=y; width=w; height=h; }
    }
}