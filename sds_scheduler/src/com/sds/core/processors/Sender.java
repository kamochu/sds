package com.sds.core.processors;

import com.google.common.base.Splitter;
import com.sds.core.OperationTypes;
import com.sds.core.ScheduledMessage;
import static com.sds.core.processors.Help.pool;
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
public class Sender {

    private final static Logger log = Logger.getLogger(Sender.class.getName());

    private final static String SPLIT_PATTERN = "|";
    private final static Splitter RESPONSE_SPLITTER = Splitter.on(SPLIT_PATTERN);
    private final static String SHORT_CODE = "60010";
    private final static String SERVICE_ID = "6016262000114196";

    public void process(ScheduledMessage message) {
        log.info("PAUSE: " + message);

        //get connection 
        Connection connection = null;
        Response response = null;
        try {
            connection = pool.getConnection();
        } catch (SQLException ex) {
            log.error("error getting connection from pool: " + ex);
        }

        //use connection to process the registration request
        if (connection != null) {
            //send the message
            response = MessageUtils.sendMessage(SHORT_CODE, SERVICE_ID, message);
            if (response.getStatus() == MessageUtils.SEND_SUCCESS) {
                String str = response.getResponse();

                Iterator<String> iterator = RESPONSE_SPLITTER.split(str).iterator();
                int i = 0;
                int sendStatus = 0;
                String refId = "";
                while (iterator.hasNext()) {
                    i++;
                    String part = iterator.next();
                    if (i == 1) {
                        try {
                            sendStatus = Integer.parseInt(part);
                        } catch (NumberFormatException ex) {
                            log.warn("unable to get send status from " + str, ex);
                        }
                    } else if (i == 4) {
                        refId = part;
                    }
                }
                message.setSendStatus(sendStatus);
                message.setSendRefId(refId);
                message.setSendLog(str);

            } else if (response.getStatus() == MessageUtils.SEND_FAIL_CONNECTION) {
                message.setSendStatus(MessageUtils.SEND_FAIL_CONNECTION);
                message.setSendRefId(response.getResponse());
                message.setSendLog("Failed to connect to the SSG to send SMS.");

            } else if (response.getStatus() == MessageUtils.SEND_FAIL_ENCODING) {
                message.setSendStatus(MessageUtils.SEND_FAIL_ENCODING);
                message.setSendRefId(response.getResponse());
                message.setSendLog("failed to encode the request message. check all parameters");
            } else {
                message.setSendStatus(MessageUtils.SEND_GENERAL);
                message.setSendRefId(response.getResponse());
                message.setSendLog("general error sending message");
            }
            try {
                if (DataManager.updateProcessedScheduledMessage(connection, message) == DataManager.EXECUTE_FAIL) {
                    log.error("unable to update the scheduled message " + message);
                }
            } catch (SQLException ex) {
                log.error("unable to update the scheduled message " + message, ex);
            }

        } else {
            log.error("the database connection cannot be established, message not sent: " + message);
        }

        //update activity log
        MessageUtils.addActivityLog(connection, OperationTypes.SEND_DAILY_SMS, 0, message, response);

        //close connection 
        DBConnectionPool.closeConnection(connection);

    }

}
