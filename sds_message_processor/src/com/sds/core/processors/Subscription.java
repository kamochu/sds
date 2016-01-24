package com.sds.core.processors;

import com.sds.core.conf.OperationTypes;
import com.sds.core.conf.SubcriptionUpdateTypes;
import com.sds.core.Subscriber;
import com.sds.core.SubscriptionMessage;
import com.sds.core.conf.Notifications;
import com.sds.core.conf.TextConfigs;
import com.sds.core.exceptions.InvalidNodeException;
import com.sds.core.util.MessageUtils;
import com.sds.core.util.Response;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class Subscription {

    private final static Logger log = Logger.getLogger(Subscription.class.getName());
    public final static DBConnectionPool pool = DBConnectionPool.getInstance();

    public void process(SubscriptionMessage message) {
        log.info("start processing: " + message);

        String responseText;
        int operationType = OperationTypes.REGISTER;
        Subscriber subscriber = null;

        //get connection 
        Connection connection = null;
        try {
            connection = pool.getConnection();
        } catch (SQLException ex) {
            log.error("error getting connection from pool: " + ex);
        }

        //use connection to process the registration request
        if (connection != null) {
            try {
                subscriber = DataManager.getSubscriber(connection, message.getAddress());
                subscriber.setMsisdn(message.getAddress());
                //closeConnection(connection); // not needed anymore
                log.error("subscriber loaded: " + subscriber);

                if (message.getUpdateType() == SubcriptionUpdateTypes.DELETION) {
                    operationType = OperationTypes.DE_REGISTER;
                    //delete existing profile
                    if (subscriber.isLoaded()) {
                        if (DataManager.deleteSubscriber(connection, subscriber) == DataManager.EXECUTE_SUCCESS) {
                            try {
                                responseText = MessageUtils.resolveMessage(Notifications.STOP_SUCCESS, subscriber);
                            } catch (InvalidNodeException ex) {
                                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                log.error("error loading node id " + Notifications.STOP_SUCCESS, ex);
                            }
                        } else {
                            responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                        }
                    } else {
                        //customer does not exist on SDS, proceed to registration flow
                        try {
                            responseText = MessageUtils.resolveMessage(Notifications.STOP_NON_REGISTERED, subscriber);
                        } catch (InvalidNodeException ex) {
                            responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                            log.error("error loading node id ", ex);
                        }
                    }
                } else if (message.getUpdateType() == SubcriptionUpdateTypes.ADDITION
                        || message.getUpdateType() == SubcriptionUpdateTypes.MODIFICATION) {

                    if (subscriber.isLoaded()) {
                        //already registered
                        try {
                            responseText = MessageUtils.resolveMessage(Notifications.SUBSCRIPTION_ALREADY_REGISTERED, subscriber);
                        } catch (InvalidNodeException ex) {
                            responseText = TextConfigs.SUBSCRIPTION_TECHNICAL_ERROR;
                            log.error("error resolving last node", ex);
                        }
                    } else {
                        subscriber.setLastNode(Notifications.SUBSCRIPTION_INITIAL_NODE_ID); // initailize the last node
                        if (DataManager.addSubscriber(connection, subscriber, message) == DataManager.EXECUTE_SUCCESS) {
                            //added the subscriber successfully
                            try {
                                responseText = MessageUtils.resolveMessage(subscriber.getLastNode(), subscriber); // get message for last node
                            } catch (InvalidNodeException ex) {
                                responseText = TextConfigs.SUBSCRIPTION_TECHNICAL_ERROR;
                                log.error("unable resolving last node", ex);
                            }
                        } else {
                            //failed to add subscriber
                            responseText = TextConfigs.SUBSCRIPTION_TECHNICAL_ERROR;
                        }
                    }
                } else {
                    log.error("subscription request type not supported: " + message.toString() + ", subscriber: " + subscriber);
                    try {
                        responseText = MessageUtils.resolveMessage(Notifications.SUBSCRIPTION_UNSUPPORTED_REQUEST, subscriber); // get message for last node
                    } catch (InvalidNodeException ex) {
                        responseText = TextConfigs.SUBSCRIPTION_TECHNICAL_ERROR;
                        log.error("unable resolving last node", ex);
                    }
                }

            } catch (SQLException ex) {
                log.warn("error loading suscriber: ", ex);
                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
            }
        } else {
            responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
        }

        Response response = MessageUtils.sendMessage(responseText, message);
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        MessageUtils.addActivityLog(connection, operationType, 0, subscriber, message, response);

        //close connection 
        DBConnectionPool.closeConnection(connection);

    }

}
