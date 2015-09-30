package com.sds;

import com.sds.core.ScheduledMessage;
import com.sds.core.SenderConsumer;
import com.sds.core.SenderProducer;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class App {

    private final static Logger log = Logger.getLogger(App.class.getName());

    public static void main(String args[]) {
        log.info("Service is starting...");
        //shared lists
        LinkedList<ScheduledMessage> scheduledMessageQueue = new LinkedList<ScheduledMessage>();

        //create threads
        Thread sendProducerThread = new Thread(new SenderProducer(scheduledMessageQueue), "SenderProducer");
        Thread senderConsumerThread1 = new Thread(new SenderConsumer(scheduledMessageQueue), "SenderConsumer 1");
        Thread senderConsumerThread2 = new Thread(new SenderConsumer(scheduledMessageQueue), "SenderConsumer 2");
        Thread senderConsumerThread3 = new Thread(new SenderConsumer(scheduledMessageQueue), "SenderConsumer 3");
        Thread senderConsumerThread4 = new Thread(new SenderConsumer(scheduledMessageQueue), "SenderConsumer 4");

        sendProducerThread.start();
        senderConsumerThread1.start();
        senderConsumerThread2.start();
        senderConsumerThread3.start();
        senderConsumerThread4.start();

    }

}
