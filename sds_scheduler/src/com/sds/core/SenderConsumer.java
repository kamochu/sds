package com.sds.core;

import com.sds.core.processors.Sender;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class SenderConsumer implements Runnable {

    private final static Logger log = Logger.getLogger(SenderConsumer.class.getName());
    private final LinkedList<ScheduledMessage> sharedQueue;
    private boolean waiting = true;

    public SenderConsumer(LinkedList<ScheduledMessage> sharedQueue) {
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
                ScheduledMessage message = consume();

                //log the message received before processing
                log.info(Thread.currentThread().getName() + " consumed: " + message);

                //process the message
                new Sender().process(message);

            } catch (InterruptedException ex) {
                log.error("exception while consuming the message", ex);
            }
        }
    }

    private ScheduledMessage consume() throws InterruptedException {
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
