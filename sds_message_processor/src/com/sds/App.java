package com.sds;

import com.sds.core.DeliveryConsumer;
import com.sds.core.DeliveryMessage;
import com.sds.core.DeliveryProducer;
import com.sds.core.InboxConsumer;
import com.sds.core.InboxMessage;
import com.sds.core.InboxProducer;
import com.sds.core.Node;
import com.sds.core.SubscriptionMessage;
import com.sds.core.SubscriptionsConsumer;
import com.sds.core.SubscriptionsProducer;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class App {
    
    private final static Logger log = Logger.getLogger(App.class.getName());
    private static Map<Integer, Node> nodesMap = null;

    /**
     * initialize the application nodes map to be used in other classes. the
     * service exits if nodes map loading failure occurs
     *
     * @throws SQLException
     */
    private static void initNodesMap() throws SQLException {
        Connection con = DBConnectionPool.getInstance().getConnection();
        nodesMap = DataManager.getNodes(con);
        log.info("nodes loaded: " + nodesMap);
    }

    /**
     * gets the nodes map used at the of initialization
     *
     * @return nodes map
     */
    public static Map<Integer, Node> getNodesMap() {
        return nodesMap;
    }
    
    public static void main(String args[]) {
        log.info("Service is starting...");
        try {
            //initialize the node map
            initNodesMap();
        } catch (SQLException ex) {
            System.out.println("Error laoding node map: " + ex);
            log.error("error loading nodes maps", ex);
            log.info("service exiting....");
            System.exit(-1); //errroneous exit
        }

        //shared lists
        LinkedList<InboxMessage> inboxSharedQueue = new LinkedList<InboxMessage>();
        LinkedList<SubscriptionMessage> subscriptionsSharedQueue = new LinkedList<SubscriptionMessage>();
        LinkedList<DeliveryMessage> deliverySharedQueue = new LinkedList<DeliveryMessage>();

        //create inbox threads
        Thread inboxProducerThread = new Thread(new InboxProducer(inboxSharedQueue), "InboxProducer");
        Thread inboxConsumerThread1 = new Thread(new InboxConsumer(inboxSharedQueue), "InboxConsumer 1");
        Thread inboxConsumerThread2 = new Thread(new InboxConsumer(inboxSharedQueue), "InboxConsumer 2");
        Thread inboxConsumerThread3 = new Thread(new InboxConsumer(inboxSharedQueue), "InboxConsumer 3");
        Thread inboxConsumerThread4 = new Thread(new InboxConsumer(inboxSharedQueue), "InboxConsumer 4");
        
        inboxProducerThread.start();
        inboxConsumerThread1.start();
        inboxConsumerThread2.start();
        inboxConsumerThread3.start();
        inboxConsumerThread4.start();

        //create subscriptions thread
        Thread subscriptionsProducerThread = new Thread(new SubscriptionsProducer(subscriptionsSharedQueue), "SubscriptionsProducer");
        Thread subscriptionsConsumerThread1 = new Thread(new SubscriptionsConsumer(subscriptionsSharedQueue), "SubscriptionsConsumer 1");
        Thread subscriptionsConsumerThread2 = new Thread(new SubscriptionsConsumer(subscriptionsSharedQueue), "SubscriptionsConsumer 2");
        Thread subscriptionsConsumerThread3 = new Thread(new SubscriptionsConsumer(subscriptionsSharedQueue), "SubscriptionsConsumer 3");
        Thread subscriptionsConsumerThread4 = new Thread(new SubscriptionsConsumer(subscriptionsSharedQueue), "SubscriptionsConsumer 4");
        
        subscriptionsProducerThread.start();
        subscriptionsConsumerThread1.start();
        subscriptionsConsumerThread2.start();
        subscriptionsConsumerThread3.start();
        subscriptionsConsumerThread4.start();

        //create deleivery threads
        Thread deliveryProducerThread = new Thread(new DeliveryProducer(deliverySharedQueue), "DeliveryProducer");
        Thread deliveryConsumerThread1 = new Thread(new DeliveryConsumer(deliverySharedQueue), "DeliveryConsumer 1");
        Thread deliveryConsumerThread2 = new Thread(new DeliveryConsumer(deliverySharedQueue), "DeliveryConsumer 2");
        Thread deliveryConsumerThread3 = new Thread(new DeliveryConsumer(deliverySharedQueue), "DeliveryConsumer 3");
        Thread deliveryConsumerThread4 = new Thread(new DeliveryConsumer(deliverySharedQueue), "DeliveryConsumer 4");
        
        deliveryProducerThread.start();
        deliveryConsumerThread1.start();
        deliveryConsumerThread2.start();
        deliveryConsumerThread3.start();
        deliveryConsumerThread4.start();
    }
    
}
