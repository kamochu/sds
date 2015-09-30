package com.sds.core.processors;

import com.sds.core.DeliveryMessage;
import com.sds.core.DeliveryMessageTypes;
import com.sds.core.RequestTypes;
import static com.sds.core.processors.Help.pool;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class Delivery {

    private final static Logger log = Logger.getLogger(Delivery.class.getName());

    public void process(DeliveryMessage message) {
        log.info("processing delivery message: " + message);

        //get connection 
        Connection connection = null;
        try {
            connection = pool.getConnection();
        } catch (SQLException ex) {
            log.error("error getting connection from pool: " + ex);
        }

        //use connection to process the registration request
        if (connection != null) {

            //update the activity log table
            if (DataManager.updateActivityLogDeliveryStatus(
                    connection, message.getDeliveryStatus(),
                    message.getAddress(),
                    message.getCorrelator()) == DataManager.EXECUTE_FAIL) {
                log.error("error updating " + message.toString());
            } else {
                log.info("updated activitity log successfully");
            }

            //update scheduled messages table
            if (message.getCorrelator() != null && RequestTypes.SYSTEM_PREFIX.equalsIgnoreCase(
                    message.getCorrelator().substring(0, 3))) {

                if (DeliveryMessageTypes.DELIVERED_TO_TERMINAL.equalsIgnoreCase(message.getDeliveryStatus())) {
                    //update the record in the scheduled messages table
                    try {
                        long recordId = Integer.parseInt(message.getCorrelator().substring(3));
                        if (DataManager.EXECUTE_FAIL == DataManager.updateDeliveredScheduledMessage(
                                connection, message.getDeliveryStatus(), recordId, message.getAddress())) {
                            log.error("error updating scheduled messages table " + message.toString());
                        } else {
                            log.info("updated scheduled messages successully log successfully");
                        }
                    } catch (SQLException | NumberFormatException ex) {
                        log.error("error updating scheduled messages table " + message.toString(), ex);
                    }
                } else {
                    //delete from the scheduled messages table to allow user to receive same record again
                    try {
                        long recordId = Integer.parseInt(message.getCorrelator().substring(3));
                        if (DataManager.EXECUTE_FAIL == DataManager.deleteScheduledMessageNotDelivered(
                                connection, recordId, message.getAddress())) {
                            log.error("error deleting record from scheduled messages table " + message.toString());
                        } else {
                            log.info("deleted scheduled messages record successfully" + message);
                        }
                    } catch (SQLException | NumberFormatException ex) {
                        log.error("error deleting record from scheduled messages table " + message.toString(), ex);
                    }
                }
            } else {
                log.info("The request is not a scheduled request, no need to update scheduled messages table" + message);
            }

        } else {
            log.error("connection is null, messsage could not processed: " + message);
        }
        //close connection 
        DBConnectionPool.closeConnection(connection);

    }

}
