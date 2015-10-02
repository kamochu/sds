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
                    if (subscriber.getRegStatus() == RegistrationStatus.BASIC) {
                        if (subscriber.getStatus() == Subscriber.ACTIVE) {
                            responseText = TextConfigs.RESUME_ALREADY_RESUMED;
                        } else {
                            if (DataManager.resumeSubscriber(connection, subscriber) == DataManager.EXECUTE_SUCCESS) {
                                responseText = TextConfigs.RESUME_REGISTERED_PART1 + subscriber.getName() + TextConfigs.RESUME_REGISTERED_PART2;

                            } else {
                                responseText = TextConfigs.RESUME_TECHNICAL_ERROR;
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
                    responseText = TextConfigs.RESUME_NON_REGISTERED;
                }
            } catch (SQLException ex) {
                log.warn("error loading suscriber: ", ex);
                responseText = TextConfigs.RESUME_TECHNICAL_ERROR;
            }
        } else {
            responseText = TextConfigs.RESUME_TECHNICAL_ERROR;
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
