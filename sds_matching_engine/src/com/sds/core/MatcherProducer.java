package com.sds.core;

import com.sds.App;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class MatcherProducer implements Runnable {

    private final static Logger log = Logger.getLogger(MatcherProducer.class.getName());
    private final LinkedList<Subscriber> sharedQueue;
    private Connection conn;
    private final static int BATCH_SIZE = 500;
    private final static int SLEEP_TIME = 100;
    private long lastRecordId;
    private final static String DB_HOST = "localhost";
    private final static String DB_PORT = "3306";
    private final static String DB_NAME = "sds";
    private final static String DB_USER = "app";
    private final static String DB_PASSWORD = "!QAZ2wsx";
    private boolean done = false;

    public MatcherProducer(LinkedList<Subscriber> sharedQueue) {
        this.sharedQueue = sharedQueue;
        this.lastRecordId = 0;
        try {
            this.conn = getConnection();
        } catch (SQLException ex) {
            log.error("error initializing connection ", ex);
        }
    }

    public boolean isDone() {
        return done;
    }

    public boolean init() {
        ArrayList<Job> jobs;
        String jobDate = App.dateFormat.format(new Date());
        String batchId = App.dateFormat.format(new Date());

        //check other jobs run for the day
        try {
            jobs = DataManager.getJobs(getConnection(), jobDate);
            batchId = jobDate + "_" + (jobs.size() + 1);
        } catch (SQLException ex) {
            log.error("error loading batch job", ex);
        }

        //initialize the job
        Job job = new Job();
        job.setBatchId(batchId);
        job.setJobDate(jobDate);
        job.setInitiator(App.getJobInitiator());
        job.setInitiatorComments(App.getJobInitiatorComments());
        try {
            if (DataManager.addJob(getConnection(), job) == DataManager.EXECUTE_SUCCESS) {
                App.setBatchId(batchId);
                App.setJobDate(jobDate);
                return true;
            }
        } catch (SQLException ex) {
            log.error("error saving the job", ex);
        }

        return false;
    }

    public boolean deinit() {
        //update the jobs record in the database
        Job job = new Job();
        job.setBatchId(App.getBatchId());
        job.setJobDate(App.getJobDate());
        job.setTotalProduced(App.getNoProduced());
        job.setNoOfDateMatches(App.getNoOfDateMatches());
        job.setNoOfDatingTips(App.getNoOfDatingTips());
        job.setNoOfInfoSMS(App.getNoOfInfoSMS());
        job.setStatus(App.getJobStatus());
        job.setReason(App.getJobReason());
        job.setInitiator(App.getJobInitiator());
        job.setInitiatorComments(App.getJobInitiatorComments());

        try {
            if (DataManager.updateJob(getConnection(), job) == DataManager.EXECUTE_SUCCESS) {
                return true;
            }
        } catch (SQLException ex) {
            log.error("error saving the job", ex);
        }
        return false;
    }

    @Override
    public void run() {
        while (!done) {
            try {
                produce();
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ex) {
                log.error("exception while producing messages", ex);
            }
        }
    }

    private Connection getConnection() throws SQLException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException ex) {
            log.error("Error loading drivers", ex);
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
                log.info("loading-next-batch: batch_size: " + BATCH_SIZE + ", from: " + lastRecordId);
                ArrayList<Subscriber> list = DataManager.pollActiveSubscribers(getConnection(), BATCH_SIZE, lastRecordId);

                try {
                    conn.close();
                } catch (Exception ex) {

                }
                //load everything into the queue
                int size = list.size();
                log.info("fetched " + size + " records from the database");
                for (int i = 0; i < size; i++) {
                    sharedQueue.add(list.get(i)); //get the last 
                    log.info("Added: " + list.get(i));
                    lastRecordId = list.get(i).getId(); // populate message id added into the db
                }
                App.incrementNoProduced(size);// increment the batch loaded

                //notify if something has been added to the queue
                if (size > 0) {
                    sharedQueue.notifyAll();
                } else {
                    log.info("no more records from the database - preparing to exit");
                    done = true;
                }
            } catch (SQLException ex) {
                log.error("Error getting database connection: ", ex);
            }

        }
    }
}
