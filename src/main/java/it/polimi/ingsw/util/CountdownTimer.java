package it.polimi.ingsw.util;

import java.util.Observable;


public class CountdownTimer extends Observable implements Runnable {

    private String id;
    private int remainingTime;
    private Runnable task;
    private Thread timerThread;

    public CountdownTimer(String id, int timeout) {
        this.id = id;
        this.remainingTime = timeout;
    }

    public CountdownTimer(String id, int timeout, Runnable task) {
        this(id, timeout);
        this.schedule(task);
    }

    public int getRemainingTime() {
        return this.remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void schedule(Runnable task, int remainingTime) {
        this.remainingTime = remainingTime;
        this.task = task;
        this.timerThread = new Thread(this);
        this.start();
    }

    public void schedule(Runnable task) {
        this.schedule(task, this.remainingTime);
    }

    public void cancel() {
        if (this.timerThread != null) this.timerThread.interrupt();
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
                Thread.sleep(1000);
                this.remainingTime -= 1;
                this.setChanged();
                this.notifyObservers(this.id + " " + this.remainingTime);
            }
            if (this.task != null) this.task.run();
        } catch (InterruptedException e) {
            //System.out.println("Timer interrupted");
            Thread.currentThread().interrupt();
        }
    }

}
