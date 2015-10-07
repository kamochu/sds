package com.sds.core.processors;

import com.sds.core.InboxMessage;
import com.sds.core.conf.OperationTypes;
import com.sds.core.conf.RegistrationStatus;
import com.sds.core.Subscriber;
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
public class Help implements Processor {

    private final static Logger log = Logger.getLogger(Help.class.getName());

    public final static DBConnectionPool pool = DBConnectionPool.getInstance();

    @Override
    public void process(InboxMessage message) {
        log.info("HELP: " + message);

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

                log.error("subscriber loaded: " + subscriber);
                if (subscriber.isLoaded()) {
                    if (subscriber.getRegStatus() >= RegistrationStatus.BASIC) {
                        //help for registered subscribers
                        try {
                            responseText = MessageUtils.resolveMessage(Notifications.HELP_REGISTERED, subscriber);
                        } catch (InvalidNodeException ex) {
                            responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                            log.error("error resolving message", ex);
                        }
                    } else {
                        DBConnectionPool.closeConnection(connection); // not needed anymore
                        //process registration, status not okay
                        new Register().process(message);
                        return; // do not send any messages
                    }
                } else {
                    //customer does not exist on the system
                    try {
                        responseText = MessageUtils.resolveMessage(Notifications.HELP_NON_REGISTERED, subscriber);
                    } catch (InvalidNodeException ex) {
                        responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                        log.error("error resolving message", ex);
                    }
                }
            } catch (SQLException ex) {
                log.warn("error loading suscriber: ", ex);
                try {
                    responseText = MessageUtils.resolveMessage(Notifications.HELP_GENERAL, subscriber);
                } catch (InvalidNodeException ex1) {
                    responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                    log.error("error resolving message", ex1);
                }
            }
        } else {
            try {
                responseText = MessageUtils.resolveMessage(Notifications.HELP_GENERAL, subscriber);
            } catch (InvalidNodeException ex) {
                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                log.error("error resolving message", ex);
            }
        }

        //send message and updated activity log
        Response response = MessageUtils.sendMessage(responseText, message);
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        MessageUtils.addActivityLog(connection, OperationTypes.INFORMATION_QUERY, 0, subscriber, message, response);

        DBConnectionPool.closeConnection(connection); // not needed anymore
    }
}
