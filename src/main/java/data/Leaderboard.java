package data;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Leaderboard {
    private ArrayList<Integer> scores;
    private final String FILE_NAME = "leaders.dat";
    private final int MAX_SIZE = 50;

    public Leaderboard() {
        scores = new ArrayList<>();
        load();
    }

    public boolean addScore(int score) {
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());
        if (scores.size() > MAX_SIZE) {
            scores.remove(scores.size() - 1);
        }
        save();
        // Новый рекорд, если это первое место
        return !scores.isEmpty() && scores.get(0) == score;
    }

    public int[] getTop5() {
        int[] top = new int[Math.min(5, scores.size())];
        for (int i = 0; i < top.length; i++) {
            top[i] = scores.get(i);
        }
        return top;
    }

    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            scores = (ArrayList<Integer>) ois.readObject();
        } catch (Exception e) {
            scores = new ArrayList<>();
        }
    }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(scores);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}