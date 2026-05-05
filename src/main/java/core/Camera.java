package core;

public class Camera {
    private int x;
    private int viewWidth;

    public Camera(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.x = 0;
    }

    public void update(int targetX) {
        x = targetX - viewWidth / 2;
        if (x < 0) x = 0;
    }

    public int getX() { return x; }
}