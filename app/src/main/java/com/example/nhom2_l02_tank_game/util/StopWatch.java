package com.example.nhom2_l02_tank_game.util;

public class StopWatch {
    private long startTime;
    private long previousElapsedTime;
    private boolean isRunning;

    public void start() {
        synchronized (this) {
            startTime = System.currentTimeMillis();
            isRunning = true;
        }
    }

    public void pause() {
        synchronized (this) {
            previousElapsedTime += System.currentTimeMillis() - startTime;
            isRunning = false;
        }
    }

    public void reset() {
        synchronized (this) {
            startTime = 0;
            previousElapsedTime = 0;
            isRunning = false;
        }
    }

    /**
     * @return the total elapsed time in milliseconds
     */
    public final long getElapsedTime() {
        synchronized (this) {
            long currentElapsedTime = 0;
            if (isRunning) {
                currentElapsedTime = System.currentTimeMillis() - startTime;
            }
            return previousElapsedTime + currentElapsedTime;
        }
    }


    @Override
    public String toString() {
        return String.format("%d millis", getElapsedTime());
    }

}
