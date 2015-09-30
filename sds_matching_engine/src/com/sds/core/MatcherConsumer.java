package com.sds.core;

import com.sds.core.processors.Matcher;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class MatcherConsumer implements Runnable {

    private final static Logger log = Logger.getLogger(MatcherConsumer.class.getName());
    private final LinkedList<Subscriber> sharedQueue;
    private boolean waiting = true;

    public MatcherConsumer(LinkedList<Subscriber> sharedQueue) {
        this.sharedQueue = sharedQueue;
    }

    public boolean isWaiting() {
        return waiting;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //get message from queue
                Subscriber subscriber = consume();

                //log the message received before processing
                log.info(Thread.currentThread().getName() + " consumed: " + subscriber);

                //process the message
                new Matcher().process(subscriber);

            } catch (InterruptedException ex) {
                log.error("exception while consuming the message", ex);
            }
        }
    }

    private Subscriber consume() throws InterruptedException {
        synchronized (sharedQueue) {
            //wait if queue is empty
            while (sharedQueue.isEmpty()) {
                log.info("Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + sharedQueue.size());
                waiting = true;
                sharedQueue.wait();
            }
            waiting = false;
            sharedQueue.notifyAll();
            if (sharedQueue.isEmpty()) {
                return consume();
            } else {
                return sharedQueue.remove();
            }
        }
    }
}
