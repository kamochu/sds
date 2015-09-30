package com.sds.core;

import com.sds.core.processors.Delivery;
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
public class DeliveryConsumer implements Runnable {

    private final static Logger log = Logger.getLogger(SubscriptionsConsumer.class.getName());
    private final LinkedList<DeliveryMessage> sharedQueue;

    private final static String DB_HOST = "localhost";
    private final static String DB_PORT = "3306";
    private final static String DB_NAME = "ssg";
    private final static String DB_USER = "app";
    private final static String DB_PASSWORD = "!QAZ2wsx";
    private Connection conn;

    public DeliveryConsumer(LinkedList<DeliveryMessage> sharedQueue) {
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
                DeliveryMessage message = consume();
                
                //log the message received before processing
                log.info(Thread.currentThread().getName() + " consumed: " + message);
                
                //process the message
                new Delivery().process(message);

                //ack - update database record to avoid futue processing on system restart
                if (DataManager.acknowledgeDeliveryReceipt(getConnection(), message) == DataManager.EXECUTE_FAIL) {
                    log.error("acknowledge message failed: " + message);
                }
            } catch (InterruptedException ex) {
                log.error("exception while consuming the message", ex);
            } catch (SQLException ex) {
                log.error("exception while acknowledging the message", ex);
            }
        }
    }

    private DeliveryMessage consume() throws InterruptedException {
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
