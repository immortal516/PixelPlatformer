package core;

public class ChallengeManager {
    private boolean started = false;
    private boolean over = false;
    private long startTime;
    private long timeLeft = 60;
    private long duration = 60;

    public void start() {
        started = true;
        over = false;
        startTime = System.currentTimeMillis();
        timeLeft = duration;
    }

    public void update() {
        if (!started || over) return;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        timeLeft = duration - elapsed;
        if (timeLeft <= 0) {
            timeLeft = 0;
            over = true;
            started = false;
        }
    }

    public void addTime(int seconds) {
        duration += seconds;
        timeLeft += seconds;
    }

    public boolean isStarted()  { return started; }
    public boolean isOver()     { return over; }
    public long getTimeLeft()   { return timeLeft; }

    public void reset() {
        started = false;
        over = false;
        timeLeft = 60;
        duration = 60;
    }

    public void forceEnd() {
        over = true;
        started = false;
        timeLeft = 0;
    }
}