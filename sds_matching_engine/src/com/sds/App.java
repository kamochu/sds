package com.sds;

import com.sds.core.JobStatus;
import com.sds.core.MatcherConsumer;
import com.sds.core.MatcherProducer;
import com.sds.core.Subscriber;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class App {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final static Logger log = Logger.getLogger(App.class.getName());
    private final static int NUMBER_OF_CONSUMERS = 4;
    public static boolean isProducerDone = false;

    private static int noOfDateMatches;
    private static int noOfDatingTips;
    private static int noOfInfoSMS;
    private static int noProduced;
    private static int noFailed;
    private static int noNothing;

    private static String jobDate;
    private static String batchId;
    private static int jobStatus;
    private static String jobReason;

    private static int jobInitiator = -1;
    private static String jobInitiatorComments = "no comments";
    private final static String HELP_STRING = "Expected two command line arguments; initiator_user_id initiators_comments e.g. 1 \"Re-run the job after failures.\"";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (args.length == 2) {
            try {
                jobInitiator = Integer.parseInt(args[0]);//first argument
            } catch (NumberFormatException ex) {
                log.warn("unable to resolve job initiator cmmand line argument", ex);
            }

            jobInitiatorComments = args[1];//second argument 

        } else {
            log.error(HELP_STRING);
            System.out.println("Error starting service: " + HELP_STRING);
            System.exit(-1);
        }

        log.info("Service is starting...");

        //setup the shared queue and producer thread
        LinkedList<Subscriber> subscribersSharedQueue = new LinkedList<Subscriber>();
        MatcherProducer producer = new MatcherProducer(subscribersSharedQueue);
        Thread producerThread = new Thread(producer, "MatcherProducer");

        //producer init - update jobs table
        if (producer.init()) {
            //init the producer thread
            producerThread.start();

            //setup the consumers and consumerthreads
            ArrayList<MatcherConsumer> consumersList = new ArrayList<>();
            ArrayList<Thread> consumerThreadsList = new ArrayList<>();
            for (int i = 0; i < NUMBER_OF_CONSUMERS; i++) {
                MatcherConsumer consumer = new MatcherConsumer(subscribersSharedQueue);
                Thread consumerThread = new Thread(consumer, "MatcherConsumer " + (i + 1));
                consumersList.add(consumer);
                consumerThreadsList.add(consumerThread);
                consumerThread.start();
            }

            //poller to check when consumer is done
            while (!producer.isDone()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    log.warn("main thread unable to sleep: " + ex);
                }
            }
            log.info("producer is done - proceeding to checking the consumer threads");

            try {
                boolean processing = false;
                while (true) {
                    //sleep for 2 seconds to allow consumers to  finish processing the messages received
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        log.warn("the main thread sleep exception waiting for consumer threads to finish", ex);
                    }

                    //check all consumer threads - if at least one thread is not waiting, then processing is ongoing 
                    for (int i = 0; i < consumersList.size(); i++) {
                        if (!consumersList.get(i).isWaiting()) {
                            log.info("thread " + consumerThreadsList.get(i).getName() + " is still running");
                            processing = true;
                            break;
                        }
                    }

                    //if no thread is processing, break the loop and proceed
                    if (!processing) {
                        break;
                    }
                }
            } catch (Exception ex) {
                log.warn("" + ex);
            }

            //stop the consumer threads
            for (int i = 0; i < consumerThreadsList.size(); i++) {
                try {
                    log.info("stopping thread: " + consumerThreadsList.get(i).getName());
                    consumerThreadsList.get(i).stop();
                } catch (Exception ex) {
                    log.warn("error stopping thread " + consumerThreadsList.get(i).getName(), ex);
                }
            }

            //stop the producer thread
            try {
                log.info("stopping thread: " + producerThread.getName());
                producerThread.stop();
            } catch (Exception ex) {
                log.warn("error stopping thread " + producerThread.getName(), ex);
            }

            //set the job as completed
            jobStatus = JobStatus.COMPLETED;
            jobReason = "completed";

            //de init  - update the jobs table
            if (producer.deinit()) {
                log.info("updated the jobs table");
            } else {
                log.error("unable to update the jobs table");
            }

            //log stats 
            log.info("job_summary_stats|"
                    + "batch_id: " + batchId
                    + ", job_date: " + jobDate
                    + ", total_produced: " + noProduced
                    + ", date_matches: " + noOfDateMatches
                    + ", dating_tips: " + noOfDatingTips
                    + ", info_sms: " + noOfInfoSMS
                    + ", no_failed: " + noFailed
                    + ", no_nothing: " + noNothing
                    + ", job_status: " + jobStatus
            );

            log.info("service stopped");

        } else {
            log.error("service stopping - job initialization failed");
        }

        System.exit(0);
    }

    public static String getJobReason() {
        return jobReason;
    }

    public static void setJobReason(String reason) {
        jobReason = reason;
    }

    public static int getJobStatus() {
        return jobStatus;
    }

    public static void setJobStatus(int status) {
        App.jobStatus = status;
    }

    public static String getBatchId() {
        return batchId;
    }

    public static void setBatchId(String bId) {
        App.batchId = bId;
    }

    public static String getJobDate() {
        return jobDate;
    }

    public static void setJobDate(String jDate) {
        App.jobDate = jDate;
    }

    public synchronized static void incrementDateMatches() {
        noOfDateMatches++;
    }

    public synchronized static void incrementDatingTips() {
        noOfDatingTips++;
    }

    public synchronized static void incrementInfoSMS() {
        noOfInfoSMS++;
    }

    public synchronized static void incrementNoFailed() {
        noFailed++;
    }

    public synchronized static void incrementNoNothing() {
        noNothing++;
    }

    public static int getNoOfDateMatches() {
        return noOfDateMatches;
    }

    public static int getNoOfDatingTips() {
        return noOfDatingTips;
    }

    public static int getNoOfInfoSMS() {
        return noOfInfoSMS;
    }

    public static int getNoFailed() {
        return noFailed;
    }

    public static int getNoNothing() {
        return noNothing;
    }

    public static int getJobInitiator() {
        return jobInitiator;
    }

    public static String getJobInitiatorComments() {
        return jobInitiatorComments;
    }

    public static void incrementNoProduced(int size) {
        noProduced += size;
    }

    public static int getNoProduced() {
        return noProduced;
    }
}
