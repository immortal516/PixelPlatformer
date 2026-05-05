package main;

import core.GamePanel;
import utils.Assets;

import javax.swing.*;

public class Game {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static void main(String[] args) {
        Assets.init();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pixel Platformer");
            GamePanel panel = new GamePanel();
            frame.add(panel);
            frame.setSize(WIDTH, HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.start();
        });
    }
}