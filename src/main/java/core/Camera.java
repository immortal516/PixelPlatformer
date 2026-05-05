package core;

public class Camera {
    private int x, y;
    private int viewWidth, viewHeight;

    public Camera(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.x = 0;
        this.y = 0;
    }

    public void update(int targetX, int targetY) {
        // Центрируем камеру на цели (игроке)
        x = targetX - viewWidth / 2;
        y = targetY - viewHeight / 2;

        // Не даём камере уйти левее начала мира
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        // По вертикали ограничивать не будем, мир бесконечен вправо
    }

    public int getX() { return x; }
    public int getY() { return y; }
}