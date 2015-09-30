package com.sds.core.processors;

import com.sds.core.InboxMessage;
import com.sds.core.OperationTypes;
import com.sds.core.RegistrationStatus;
import com.sds.core.Subscriber;
import com.sds.core.conf.TextConfigs;
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
public class Pause implements Processor {

    private final static Logger log = Logger.getLogger(Pause.class.getName());

    @Override
    public void process(InboxMessage message) {
        log.info("PAUSE: " + message);

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
                    if (subscriber.getRegStatus() == RegistrationStatus.REG_CONFIRMED) {
                        if (subscriber.getStatus() == Subscriber.INACTIVE) {
                            responseText = TextConfigs.PAUSE_ALREADY_PAUSED;
                        } else {
                            if (DataManager.pauseSubscriber(connection, subscriber) == DataManager.EXECUTE_SUCCESS) {
                                responseText = TextConfigs.PAUSE_REGISTERED_PART1 + subscriber.getName() + TextConfigs.PAUSE_REGISTERED_PART2;
                            } else {
                                responseText = TextConfigs.PAUSE_TECHNICAL_ERROR;
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
                    responseText = TextConfigs.PAUSE_NON_REGISTERED;
                }
            } catch (SQLException ex) {
                log.warn("error loading suscriber: ", ex);
                responseText = TextConfigs.PAUSE_TECHNICAL_ERROR;
            }
        } else {
            responseText = TextConfigs.PAUSE_TECHNICAL_ERROR;
        }

        Response response = MessageUtils.sendMessage(responseText, message);
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        MessageUtils.addActivityLog(connection, OperationTypes.PAUSE, 0, subscriber, message, response);
        
        //close connection 
        DBConnectionPool.closeConnection(connection);

    }

}
