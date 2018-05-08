package it.polimi.ingsw.util;

import java.util.Observable;


public class CountdownTimer extends Observable implements Runnable {

    private int remainingTime;
    private Runnable task;
    private Thread timerThread;

    public CountdownTimer(int timeout) {
        this.remainingTime = timeout;
    }

    public CountdownTimer(Runnable task, int timeout) {
        this(timeout);
        this.schedule(task);
    }

    public void schedule(Runnable task) {
        this.task = task;
        this.timerThread = new Thread(this);
        this.start();
    }

    public int getRemainingTime() {
        return this.remainingTime;
    }

    public void cancel() {
        this.timerThread.interrupt();
        this.remainingTime = 0;
        this.task = null;
        this.timerThread = null;
    }

    public void start() {
        this.timerThread.start();
    }

    public void run() {
        try {
            while (this.remainingTime > 0) {
                this.remainingTime -= 1;
                this.setChanged();
                this.notifyObservers(this.remainingTime);
                Thread.sleep(1000);
            }
            this.task.run();
        } catch (InterruptedException e) {
            //System.out.println("Timer interrupted");
            Thread.currentThread().interrupt();
        }
    }

}
