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

    /**
     * @author Fabio Codiglioni
     * @param id the id used in the observers notifications.
     */
    public CountdownTimer(String id) {
        this.id = id;
    }

    /**
     * @author Fabio Codiglioni
     * @return the amount of seconds left before the method invocation
     */
    public int getRemainingTime() {
        return this.remainingTime;
    }

    /**
     * This methods makes the timer start counting.
     * When the countdown expires, the task's <code>run</code> method is called.
     *
     * @author Fabio Codiglioni
     * @param task the task to execute when the countdown expires.
     * @param remainingTime the amount of time after which the task's <code>run</code> method is invoked.
     */
    public void schedule(Runnable task, int remainingTime) {
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
    public void cancel(boolean withUpdate) {
        if (this.timerThread != null) this.timerThread.interrupt();
        this.remainingTime = 0;
        this.task = null;
        this.timerThread = null;
        if (withUpdate) {
            this.setChanged();
            this.notifyObservers(this.id + " ∞");
        }
    }

    public void cancel() {
        cancel(false);
    }

    /**
     * This method is used to actually start the timer's countdown.
     *
     * @author Fabio Codiglioni
     */
    public void start() {
        this.timerThread.start();
    }

    /**
     * This is the actual countdown method, executed in a separate thread.
     *
     * @author Fabio Codiglioni
     */
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
