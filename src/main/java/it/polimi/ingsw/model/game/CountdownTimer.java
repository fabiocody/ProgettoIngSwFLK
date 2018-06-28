package it.polimi.ingsw.model.game;

import java.util.Observable;


/**
 * This class represents a customized Timer, i.e. an object that updates its observers every second, and can optionally
 * call a method when the countdown expires.
 *
 * @author Fabio Codiglioni
 */
public class CountdownTimer extends Observable implements Runnable {

    private String id;
    private int remainingTime;
    private Runnable task;
    private Thread timerThread;
    private boolean timerRun = false;

    /**
     * @author Fabio Codiglioni
     * @param id the id used in the observers notifications.
     */
    CountdownTimer(String id) {
        this.id = id;
    }

    /**
     * This methods makes the timer start counting.
     * When the countdown expires, the task's <code>run</code> method is called.
     *
     * @author Fabio Codiglioni
     * @param task the task to execute when the countdown expires.
     * @param remainingTime the amount of time after which the task's <code>run</code> method is invoked.
     */
    void schedule(Runnable task, int remainingTime) {
        this.cancel();
        this.remainingTime = remainingTime;
        this.task = task;
        this.timerThread = new Thread(this);
        this.start();
        this.setChanged();
        this.notifyObservers(this.id + " " + this.remainingTime);
    }

    /**
     * This method is used to stop the timer and to reset all its parameters.
     *
     * @author Fabio Codiglioni
     */
    void cancel(boolean withUpdate) {
        if (this.timerThread != null) this.timerThread.interrupt();
        this.timerRun = false;
        this.remainingTime = 0;
        this.task = null;
        this.timerThread = null;
        if (withUpdate) {
            this.setChanged();
            this.notifyObservers(this.id + " ∞");
        }
    }

    void cancel() {
        cancel(false);
    }

    /**
     * This method is used to actually start the timer's countdown.
     *
     * @author Fabio Codiglioni
     */
    private void start() {
        this.timerRun = true;
        this.timerThread.start();
    }

    /**
     * This is the actual countdown method, executed in a separate thread.
     *
     * @author Fabio Codiglioni
     */
    public void run() {
        try {
            while (this.remainingTime > 0 && timerRun) {
                Thread.sleep(1000);
                this.remainingTime -= 1;
                this.setChanged();
                this.notifyObservers(this.id + " " + this.remainingTime);
            }
            if (this.task != null && timerRun) this.task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
