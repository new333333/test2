package com.sitescape.ef.lucene.server;

import java.rmi.RemoteException;

/**
 * Title: WatchDog
 * Description: This class represents a watchdog timer which periodically
 *              wakes up, empties the batch queues, (commits the recent changes),
 *              resets it's timer and goes back to sleep.
 * Copyright:    Copyright (c) 2005
 * Company: SiteScape, Inc.
 * @author Roy Klein
 * @version 1.0
 */

public class WatchDog extends Thread {

    private IndexObject indexObject;
    final int WATCHDOG_INTERVAL = 1000;
    private boolean stop = false;
    private boolean reset = false;
    private int sleepInterval;

    /**
     * Constructor - Set up the time interval, and keep a pointer
     * to the indexobject that this thread will be checking on.
     */
    public WatchDog( IndexObject io, int sleepinterval) {
        indexObject = io;
        if (sleepinterval <= 0)
            sleepInterval = WATCHDOG_INTERVAL;
        else
            sleepInterval = sleepinterval;
    }

    /**
     * Main body of this watchdoc timer.  The basic operation is to sit in
     * a timer loop, checking for updates each time the timer runs down.
     */
    public void run() {
        while (true) {
            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException _) {
                // if we got interrupted, then either we need to stop,
                // or, someone else called commit(), and we need to reset
                // our timer (i.e. don't call commit this time)
                if (stop)
                    return;
            }
            // if someone else called commit(), then reset the timer
            if (reset) {
                reset = false;
            } else {
                try {
                    indexObject.commit();
                } catch (RemoteException re) {
                    // couldn't commit, something bad happened...
                    // this will get caught by the client the next
                    // time it calls commit. (There's nobody listening for
                    // exceptions thrown from this thread...)
                }
            }
        }
    }

    /**
     * Called when the process wants this thread to stop.
     */
    public void setStop() {
        stop = true;
    }

    /**
     * Called when another thread issues a commit(). Sets a boolean
     * so that this thread doesn't bother trying to commit() during
     * the next wake-up.
     */
    public void resetTimer() {
        reset = true;
    }
}