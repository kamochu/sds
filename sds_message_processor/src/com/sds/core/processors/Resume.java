package com.sds.core.processors;

import com.sds.core.InboxMessage;
import com.sds.core.conf.OperationTypes;
import com.sds.core.conf.RegistrationStatus;
import com.sds.core.Subscriber;
import com.sds.core.conf.Notifications;
import com.sds.core.conf.TextConfigs;
import com.sds.core.exceptions.InvalidNodeException;
import static com.sds.core.processors.Help.pool;
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
public class Resume implements Processor {

    private final static Logger log = Logger.getLogger(Resume.class.getName());

    @Override
    public void process(InboxMessage message) {
        log.info("UNPAUSE: " + message);

        String responseText;
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
                subscriber = DataManager.getSubscriber(connection, message.getSenderAddress());
                subscriber.setMsisdn(message.getSenderAddress());
                //closeConnection(connection); // not needed anymore

                log.error("subscriber loaded: " + subscriber);
                if (subscriber.isLoaded()) {
                    if (subscriber.getRegStatus() >= RegistrationStatus.BASIC) {
                        if (subscriber.getStatus() == Subscriber.ACTIVE) {
                            try {
                                responseText = MessageUtils.resolveMessage(Notifications.RESUME_ALREADY_RESUMED, subscriber);
                            } catch (InvalidNodeException ex) {
                                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                log.error("error resolving message", ex);
                            }
                        } else {
                            if (DataManager.resumeSubscriber(connection, subscriber) == DataManager.EXECUTE_SUCCESS) {
                                try {
                                    responseText = MessageUtils.resolveMessage(Notifications.PAUSE_SUCCESS, subscriber);
                                } catch (InvalidNodeException ex) {
                                    responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                    log.error("error resolving message", ex);
                                }
                            } else {
                                try {
                                    responseText = MessageUtils.resolveMessage(Notifications.RESUME_TECHNICAL_ERROR, subscriber);
                                } catch (InvalidNodeException ex) {
                                    responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                    log.error("error resolving message", ex);
                                }
                            }
                        }
                    } else {
                        DBConnectionPool.closeConnection(connection);
                        //process registration, status not okay
                        new Register().process(message);
                        return; // do not send any messages
                    }
                } else {
                    //customer does not exist on SDS, proceed to registration flow
                    try {
                        responseText = MessageUtils.resolveMessage(Notifications.RESUME_NON_REGISTERED, subscriber);
                    } catch (InvalidNodeException ex) {
                        responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                        log.error("error resolving message", ex);
                    }
                }
            } catch (SQLException ex) {
                log.warn("error loading suscriber: ", ex);
                try {
                    responseText = MessageUtils.resolveMessage(Notifications.RESUME_TECHNICAL_ERROR, subscriber);
                } catch (InvalidNodeException ex1) {
                    responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                    log.error("error resolving message", ex1);
                }
            }
        } else {
            try {
                responseText = MessageUtils.resolveMessage(Notifications.RESUME_TECHNICAL_ERROR, subscriber);
            } catch (InvalidNodeException ex) {
                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                log.error("error resolving message", ex);
            }
        }

        Response response = MessageUtils.sendMessage(responseText, message);
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        MessageUtils.addActivityLog(connection, OperationTypes.RESUME, 0, subscriber, message, response);

        //close connection 
        DBConnectionPool.closeConnection(connection);
    }

}
