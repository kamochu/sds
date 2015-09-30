package com.sds.core.processors;

import com.sds.core.InboxMessage;
import com.sds.core.OperationTypes;
import com.sds.core.RegistrationStatus;
import com.sds.core.Subscriber;
import com.sds.core.conf.TextConfigs;
import com.sds.core.util.MessageUtils;
import com.sds.core.util.Response;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
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
                    if (subscriber.getRegStatus() == RegistrationStatus.REG_CONFIRMED) {
                        responseText = TextConfigs.HELP_REGISTERED_PART1 + subscriber.getName() + TextConfigs.HELP_REGISTERED_PART2;
                    } else {
                        DBConnectionPool.closeConnection(connection); // not needed anymore
                        //process registration, status not okay
                        new Register().process(message);
                        return; // do not send any messages
                    }
                } else {
                    //customer does not exist on SDS, proceed to registration flow
                    responseText = TextConfigs.HELP_NON_REGISTERED;
                }
            } catch (SQLException ex) {
                log.warn("error loading suscriber: ", ex);
                responseText = TextConfigs.HELP_GENERAL;
            }
        } else {
            responseText = TextConfigs.HELP_GENERAL;
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
