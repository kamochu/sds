package com.sds.core;

import com.sds.core.processors.Subscription;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class SubscriptionsConsumer implements Runnable {

    private final static Logger log = Logger.getLogger(SubscriptionsConsumer.class.getName());
    private final LinkedList<SubscriptionMessage> sharedQueue;

    private final static String DB_HOST = "localhost";
    private final static String DB_PORT = "3306";
    private final static String DB_NAME = "ssg";
    private final static String DB_USER = "app";
    private final static String DB_PASSWORD = "!QAZ2wsx";
    private Connection conn;

    public SubscriptionsConsumer(LinkedList<SubscriptionMessage> sharedQueue) {
        this.sharedQueue = sharedQueue;
        try {
            this.conn = getConnection();
        } catch (SQLException ex) {
            log.error("error initializing connection ", ex);
        }
    }

    private Connection getConnection() throws SQLException {

        if (conn == null || conn.isClosed() || !conn.isValid(50)) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                log.error("Error loading drivers", ex);
            }

        }
        return conn;

    }

    @Override
    public void run() {
        while (true) {
            try {
                //get message from queue
                SubscriptionMessage message = consume();

                //log the message received before processing
                log.info(Thread.currentThread().getName() + " consumed: " + message);

                //process the message
                new Subscription().process(message);

                //ack - update database record to avoid futue processing on system restart
                if (DataManager.acknowledgeSubscription(getConnection(), message) == DataManager.EXECUTE_FAIL) {
                    log.error("acknowledge message failed: " + message);
                }
            } catch (InterruptedException ex) {
                log.error("exception while consuming the message", ex);
            } catch (SQLException ex) {
                log.error("exception while acknowledging the message", ex);
            }
        }
    }

    private SubscriptionMessage consume() throws InterruptedException {
        synchronized (sharedQueue) {
            //wait if queue is empty
            while (sharedQueue.isEmpty()) {
                log.info("Queue is empty " + Thread.currentThread().getName() + " is waiting , size: " + sharedQueue.size());
                sharedQueue.wait();
            }

            sharedQueue.notifyAll();
            if (sharedQueue.isEmpty()) {
                return consume();
            } else {
                return sharedQueue.remove();
            }
        }
    }
}
