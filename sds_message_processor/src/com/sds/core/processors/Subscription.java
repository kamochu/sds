package com.sds.core.processors;

import com.sds.App;
import com.sds.core.OperationTypes;
import com.sds.core.SubcriptionUpdateTypes;
import com.sds.core.Subscriber;
import com.sds.core.SubscriptionMessage;
import com.sds.core.conf.TextConfigs;
import com.sds.core.exceptions.InvalidNodeException;
import com.sds.core.util.MessageUtils;
import com.sds.core.util.Response;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
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
                            String name = "customer";
                            if (subscriber.getName() != null) {
                                name = subscriber.getName();
                            }
                            responseText = TextConfigs.STOP_REGISTERED_PART1 + name + TextConfigs.STOP_REGISTERED_PART2;
                        } else {
                            responseText = TextConfigs.STOP_TECHNICAL_ERROR;
                        }
                    } else {
                        //customer does not exist on SDS, proceed to registration flow
                        responseText = TextConfigs.STOP_NON_REGISTERED;
                    }
                } else if (message.getUpdateType() == SubcriptionUpdateTypes.ADDITION || message.getUpdateType() == SubcriptionUpdateTypes.MODIFICATION) {
                    //craete a new sub or update existing profile
                    String name = "customer";
                    if (subscriber.getName() != null) {
                        name = subscriber.getName();
                    }
                    if (subscriber.isLoaded()) {
                        if (DataManager.updateSubscriptionParameters(connection, subscriber, message) == DataManager.EXECUTE_SUCCESS) {
                            responseText = TextConfigs.SUBSCRIPTION_UPDATE_SUCCESS_PART1 + name + TextConfigs.SUBSCRIPTION_UPDATE_SUCCESS_PART2;
                        } else {
                            responseText = TextConfigs.SUBSCRIPTION_UPDATE_TECHNICAL_ERROR;
                        }
                    } else {
                        subscriber.setLastNode(1); // initail the last node
                        try {
                            responseText = MessageUtils.getMessage(subscriber.getLastNode()); // get message for last node
                        } catch (InvalidNodeException ex) {
                            responseText = TextConfigs.SUBSCRIPTION_ADD_TECHNICAL_ERROR;
                            log.error("unable to resolve the response text ", ex);
                        }
                        if (DataManager.addSubscriber(connection, subscriber, message) == DataManager.EXECUTE_SUCCESS) {
                            //responseText = TextConfigs.SUBSCRIPTION_ADD_SUCCESS_PART1 + name + TextConfigs.SUBSCRIPTION_ADD_SUCCESS_PART2;
                        } else {
                            responseText = TextConfigs.SUBSCRIPTION_ADD_TECHNICAL_ERROR;
                        }
                    }
                } else {
                    log.error("subscription request type not supported: " + message.toString() + ", subscriber: " + subscriber);
                    //error - unsupportd update type 
                    responseText = TextConfigs.SUBSCRIPTION_UNSUPPORTED_REQUEST;
                }

            } catch (SQLException ex) {
                log.warn("error loading suscriber: ", ex);
                responseText = TextConfigs.STOP_TECHNICAL_ERROR;
            }
        } else {
            responseText = TextConfigs.STOP_TECHNICAL_ERROR;
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
