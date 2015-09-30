package com.sds.core;

import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class SenderProducer implements Runnable {

    private final static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat HOUR_AND_MINUTE_FORMAT = new SimpleDateFormat("HHmm");

    private final static Logger log = Logger.getLogger(SenderProducer.class.getName());
    private final LinkedList<ScheduledMessage> sharedQueue;
    private Connection conn;
    private final static int BATCH_SIZE = 500;
    private final static int SLEEP_TIME = 5000;
    private long lastRecordId;
    private final static String DB_HOST = "localhost";
    private final static String DB_PORT = "3306";
    private final static String DB_NAME = "sds";
    private final static String DB_USER = "app";
    private final static String DB_PASSWORD = "!QAZ2wsx";

    public SenderProducer(LinkedList<ScheduledMessage> sharedQueue) {
        this.sharedQueue = sharedQueue;
        this.lastRecordId = 0;
        try {
            this.conn = getConnection();
        } catch (SQLException ex) {
            log.error("error initializing connection ", ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                produce();
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                log.error("exception while producing messages", ex);
            }
        }
    }

    private Connection getConnection() throws SQLException {

        if (conn == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME, DB_USER, DB_PASSWORD);
            } catch (ClassNotFoundException ex) {
                log.error("Error loading drivers", ex);
            }

        }
        return conn;

    }

    private void produce() throws InterruptedException {

        //wait if queue has messages
        synchronized (sharedQueue) {
            while (sharedQueue.size() > 0) {
                log.info("Queue is not empty " + Thread.currentThread().getName() + " is waiting , size: " + sharedQueue.size());
                sharedQueue.wait();
            }

            try {
                log.debug("loading-next-batch: batch_size: " + BATCH_SIZE + ", from: " + lastRecordId);

                ArrayList<ScheduledMessage> list = DataManager.pollScheduledMessages(
                        getConnection(),
                        BATCH_SIZE, HOUR_AND_MINUTE_FORMAT.format(new Date()),
                        DAY_FORMAT.format(new Date()),
                        lastRecordId
                );

                log.debug("loading records - paramters:" + BATCH_SIZE + ", " + HOUR_AND_MINUTE_FORMAT.format(new Date()) + ", "
                        + DAY_FORMAT.format(new Date()) + ", "
                        + lastRecordId);

                int size = list.size();
                log.debug("fetched " + size + " records from the database");
                for (int i = 0; i < size; i++) {
                    sharedQueue.add(list.get(i)); //get the last 
                    log.info("Added: " + list.get(i));
                    lastRecordId = list.get(i).getId(); // populate message id added into the db
                }

                //notify if something has been added to the queue
                if (size > 0) {
                    sharedQueue.notifyAll();
                }

            } catch (SQLException ex) {
                log.error("Error getting database connection: ", ex);
            }

        }
    }
}
