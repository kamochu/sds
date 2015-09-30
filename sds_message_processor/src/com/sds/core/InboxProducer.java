/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sds.core;

import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class InboxProducer implements Runnable {

    private final static Logger log = Logger.getLogger(InboxProducer.class.getName());
    private final LinkedList<InboxMessage> sharedQueue;
    private Connection conn;
    private final static int BATCH_SIZE = 500;
    private final static int SLEEP_TIME = 100;
    private long lastMessageId;
    private final static String DB_HOST = "localhost";
    private final static String DB_PORT = "3306";
    private final static String DB_NAME = "ssg";
    private final static String DB_USER = "app";
    private final static String DB_PASSWORD = "!QAZ2wsx";

    public InboxProducer(LinkedList<InboxMessage> sharedQueue) {
        this.sharedQueue = sharedQueue;
        this.lastMessageId = 0;
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
                ArrayList<InboxMessage> list = DataManager.pollMessages(getConnection(), BATCH_SIZE, lastMessageId);

                //load everything into the queue
                int size = list.size();
                log.debug("producing messages loaded from the database" + size);
                for (int i = 0; i < size; i++) {
                    sharedQueue.add(list.get(i)); //get the last 
                    lastMessageId = list.get(i).getMessageId(); // populate message id added into the db
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
