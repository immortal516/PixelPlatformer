package Generator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class PixelSpriteGenerator extends JFrame {
    private BufferedImage sprite;
    private int size = 32;
    private Random random;
    private JLabel imageLabel;
    private JComboBox<String> typeSelector;

    public PixelSpriteGenerator() {
        random = new Random();
        sprite = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        setTitle("Генератор пиксельных спрайтов");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Панель с картинкой
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        // Выбор типа спрайта
        String[] types = {"Персонаж", "Меч", "Зелье", "Щит", "Монстр", "Сердце", "Звезда", "Монета"};
        typeSelector = new JComboBox<>(types);
        controlPanel.add(typeSelector);

        // Кнопка генерации
        JButton generateButton = new JButton("Сгенерировать");
        generateButton.addActionListener(e -> generateSprite());
        controlPanel.add(generateButton);

        // Кнопка сохранения
        JButton saveButton = new JButton("Сохранить PNG");
        saveButton.addActionListener(e -> saveSprite());
        controlPanel.add(saveButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Генерируем первый спрайт
        generateSprite();
    }

    private void generateSprite() {
        String type = (String) typeSelector.getSelectedItem();
        sprite = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = sprite.createGraphics();

        // Прозрачный фон
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, size, size);

        switch (type) {
            case "Персонаж":
                drawCharacter(g);
                break;
            case "Меч":
                drawSword(g);
                break;
            case "Зелье":
                drawPotion(g);
                break;
            case "Щит":
                drawShield(g);
                break;
            case "Монстр":
                drawMonster(g);
                break;
            case "Сердце":
                drawHeart(g);
                break;
            case "Звезда":
                drawStar(g);
                break;
            case "Монета":
                drawCoin(g);
                break;
        }

        g.dispose();

        // Показываем
        ImageIcon icon = new ImageIcon(sprite.getScaledInstance(256, 256, Image.SCALE_REPLICATE));
        imageLabel.setIcon(icon);
    }

    private void drawCharacter(Graphics2D g) {
        // Цвета
        Color skinColor = randomColor(255, 180, 140, 255, 220, 180);
        Color hairColor = randomColor(50, 30, 20, 200, 150, 100);
        Color shirtColor = randomColor(100, 50, 50, 255, 100, 100);
        Color pantsColor = randomColor(30, 30, 80, 100, 100, 200);

        int cx = size / 2;  // Центр
        int cy = size / 2;

        // Голова
        g.setColor(skinColor);
        g.fillRect(cx - 6, cy - 12, 12, 10);

        // Волосы
        g.setColor(hairColor);
        g.fillRect(cx - 7, cy - 14, 14, 5);

        // Глаза (белки)
        g.setColor(Color.WHITE);
        g.fillRect(cx - 4, cy - 10, 3, 3);
        g.fillRect(cx + 1, cy - 10, 3, 3);

        // Зрачки
        g.setColor(Color.BLACK);
        g.fillRect(cx - 3, cy - 9, 2, 2);
        g.fillRect(cx + 2, cy - 9, 2, 2);

        // Тело
        g.setColor(shirtColor);
        g.fillRect(cx - 5, cy - 2, 10, 8);

        // Руки
        g.setColor(skinColor);
        g.fillRect(cx - 8, cy - 1, 3, 6);
        g.fillRect(cx + 5, cy - 1, 3, 6);

        // Ноги
        g.setColor(pantsColor);
        g.fillRect(cx - 4, cy + 6, 3, 6);
        g.fillRect(cx + 1, cy + 6, 3, 6);
    }

    private void drawSword(Graphics2D g) {
        int cx = size / 2;

        // Лезвие
        Color bladeColor = randomColor(180, 180, 200, 255, 255, 255);
        g.setColor(bladeColor);
        g.fillRect(cx - 2, 2, 4, 20);

        // Острие
        int[] xPoints = {cx, cx - 3, cx + 3};
        int[] yPoints = {0, 4, 4};
        g.fillPolygon(xPoints, yPoints, 3);

        // Гарда
        g.setColor(new Color(139, 69, 19));
        g.fillRect(cx - 6, 20, 12, 3);

        // Рукоятка
        g.setColor(new Color(101, 67, 33));
        g.fillRect(cx - 2, 23, 4, 8);

        // Навершие
        g.setColor(new Color(218, 165, 32));
        g.fillOval(cx - 3, 28, 6, 4);
    }

    private void drawPotion(Graphics2D g) {
        int cx = size / 2;
        Color liquidColor = randomColor(100, 50, 50, 200, 50, 200);
        // Пробка
        g.setColor(new Color(139, 69, 19));
        g.fillRect(cx - 4, 2, 8, 4);

        // Горлышко
        g.setColor(new Color(200, 200, 200, 150));
        g.fillRect(cx - 2, 6, 4, 3);

        // Колба
        g.setColor(liquidColor);
        g.fillRect(cx - 6, 9, 12, 14);
        g.fillOval(cx - 7, 9, 14, 16);

        // Блик
        g.setColor(new Color(255, 255, 255, 150));
        g.fillRect(cx - 4, 11, 3, 6);

        // Пузырьки
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRect(cx + 1, 15, 2, 2);
        g.fillRect(cx - 2, 18, 2, 2);
    }

    private void drawShield(Graphics2D g) {
        int cx = size / 2;
        Color mainColor = randomColor(100, 100, 150, 200, 150, 100);

        g.setColor(mainColor);
        // Форма щита
        g.fillRect(cx - 8, 4, 16, 10);
        g.fillRect(cx - 10, 10, 20, 8);
        g.fillRect(cx - 6, 18, 12, 12);
        // Треугольный низ
        int[] xPoints = {cx - 6, cx + 6, cx};
        int[] yPoints = {20, 20, 30};
        g.fillPolygon(xPoints, yPoints, 3);

        // Окантовка
        g.setColor(new Color(218, 165, 32));
        g.drawRect(cx - 8, 4, 16, 10);
        g.drawRect(cx - 10, 10, 20, 8);

        // Эмблема
        g.setColor(Color.YELLOW);
        g.fillOval(cx - 4, 8, 8, 8);
    }

    private void drawMonster(Graphics2D g) {
        int cx = size / 2;
        Color bodyColor = randomColor(50, 50, 50, 150, 150, 150);
        // Тело
        g.setColor(bodyColor);
        g.fillOval(cx - 8, 6, 16, 18);

        // Глаза (большие)
        g.setColor(Color.WHITE);
        g.fillOval(cx - 5, 10, 6, 8);
        g.fillOval(cx + 1, 10, 6, 8);

        // Зрачки
        g.setColor(Color.BLACK);
        g.fillOval(cx - 2, 12, 3, 5);
        g.fillOval(cx + 3, 12, 3, 5);

        // Рот (зубастый)
        g.setColor(Color.BLACK);
        g.fillRect(cx - 6, 20, 12, 3);
        // Зубы
        g.setColor(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            g.fillRect(cx - 5 + i * 3, 19, 2, 2);
            g.fillRect(cx - 5 + i * 3, 23, 2, 2);
        }

        // Рожки
        g.setColor(new Color(100, 70, 70));
        g.fillRect(cx - 8, 2, 3, 6);
        g.fillRect(cx + 5, 2, 3, 6);
    }

    private void drawHeart(Graphics2D g) {
        int cx = size / 2;

        g.setColor(new Color(220, 50, 50));
        // Левая половинка
        g.fillOval(cx - 9, 4, 9, 9);
        // Правая половинка
        g.fillOval(cx, 4, 9, 9);
        // Низ
        int[] xPoints = {cx - 9, cx + 9, cx};
        int[] yPoints = {10, 10, 28};
        g.fillPolygon(xPoints, yPoints, 3);

        // Блик
        g.setColor(new Color(255, 150, 150, 180));
        g.fillRect(cx - 5, 6, 4, 3);
    }

    private void drawStar(Graphics2D g) {
        int cx = size / 2;
        int cy = size / 2;

        g.setColor(new Color(255, 215, 0));
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];

        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + i * Math.PI / 5;
            int r = (i % 2 == 0) ? 14 : 6;
            xPoints[i] = cx + (int)(Math.cos(angle) * r);
            yPoints[i] = cy - (int)(Math.sin(angle) * r);
        }
        g.fillPolygon(xPoints, yPoints, 10);

        // Блик
        g.setColor(new Color(255, 255, 200, 150));
        g.fillRect(cx - 3, cy - 8, 6, 4);
    }

    private void drawCoin(Graphics2D g) {
        int cx = size / 2;
        int cy = size / 2;

        // Внешний круг
        g.setColor(new Color(255, 215, 0));
        g.fillOval(cx - 12, cy - 12, 24, 24);

        // Внутренний круг
        g.setColor(new Color(255, 240, 100));
        g.fillOval(cx - 8, cy - 8, 16, 16);

        // Знак $
        g.setColor(new Color(200, 160, 0));
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("$", cx - 3, cy + 4);
    }

    private Color randomColor(int rMin, int gMin, int bMin, int rMax, int gMax, int bMax) {
        // Меняем местами если минимум больше максимума
        if (rMin > rMax) { int t = rMin; rMin = rMax; rMax = t; }
        if (gMin > gMax) { int t = gMin; gMin = gMax; gMax = t; }
        if (bMin > bMax) { int t = bMin; bMin = bMax; bMax = t; }

        int r = rMin + random.nextInt(rMax - rMin + 1);
        int g = gMin + random.nextInt(gMax - gMin + 1);
        int b = bMin + random.nextInt(bMax - bMin + 1);
        return new Color(r, g, b);
    }

    private void saveSprite() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("player_sprite.png"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                ImageIO.write(sprite, "PNG", file);
                JOptionPane.showMessageDialog(this, "Сохранено: " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PixelSpriteGenerator().setVisible(true);
        });
    }
}