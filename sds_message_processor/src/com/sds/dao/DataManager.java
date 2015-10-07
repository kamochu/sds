package com.sds.dao;

import com.sds.core.ActivityLog;
import com.sds.core.DatingTip;
import com.sds.core.DeliveryMessage;
import com.sds.core.InboxMessage;
import com.sds.core.Job;
import com.sds.core.Node;
import com.sds.core.conf.RegistrationStatus;
import com.sds.core.ScheduledMessage;
import com.sds.core.Subscriber;
import com.sds.core.SubscriptionMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class DataManager {

    private final static Logger log = Logger.getLogger(DataManager.class.getName());
    public final static int EXECUTE_SUCCESS = 0;
    public final static int EXECUTE_FAIL = -1;

    /**
     * Polls for incoming messages from SSG database
     *
     * @param conn connection instance (ensure the connection is checked)
     * @param limit number of records to query
     * @param lastMessageId last message queried last
     * @return map of messages retrieved from the database
     */
    public static ArrayList<InboxMessage> pollMessages(Connection conn, int limit, long lastMessageId) {

        ArrayList<InboxMessage> list = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT * FROM tbl_inbound_messages WHERE processing_status=0 AND message_id>?  ORDER BY message_id ASC  LIMIT 0," + limit;

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, lastMessageId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new InboxMessage(
                        rs.getLong("message_id"),
                        rs.getString("service_id"),
                        rs.getString("link_id"),
                        rs.getString("trace_unique_id"),
                        rs.getString("correlator"),
                        rs.getString("message"),
                        rs.getString("sender_address"),
                        rs.getString("dest_address"))
                );

            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query (" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }

        return list;
    }

    public static ArrayList<SubscriptionMessage> pollSubscriptions(Connection conn, int limit, long lastMessageId) {

        ArrayList<SubscriptionMessage> list = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT * FROM tbl_subscription_messages WHERE processing_status=0 AND id>?  ORDER BY id ASC  LIMIT 0," + limit;

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, lastMessageId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new SubscriptionMessage(
                        rs.getLong("id"),
                        rs.getString("subscriber_id"),
                        rs.getString("product_id"),
                        rs.getString("service_id"),
                        rs.getInt("update_type"),
                        rs.getString("update_desc"),
                        rs.getString("effective_time"),
                        rs.getString("expiry_time"))
                );
            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query (" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }

        return list;
    }

    public static ArrayList<DeliveryMessage> pollDeliveryReceipts(Connection conn, int limit, long lastMessageId) {

        ArrayList<DeliveryMessage> list = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT * FROM  tbl_delivery_receipts WHERE processing_status=0 AND id>?  ORDER BY id ASC  LIMIT 0," + limit;

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, lastMessageId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new DeliveryMessage(
                        rs.getLong("id"),
                        rs.getString("dest_address"),
                        rs.getString("correlator"),
                        rs.getString("delivery_status"))
                );

            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query (" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }

        return list;
    }

    /**
     * polls for active subscribers whose who have received a daily broadcast
     *
     * @param conn connection instance (ensure the connection is checked)
     * @param limit number of records to query
     * @param lastRecordId last message queried last
     * @return map of messages retrieved from the database
     */
    public static ArrayList<Subscriber> pollActiveSubscribers(Connection conn, int limit, long lastRecordId) {

        ArrayList<Subscriber> list = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT * FROM tbl_subscribers WHERE status=1 AND last_shared_on < CURRENT_DATE AND id>?  ORDER BY id ASC  LIMIT 0," + limit;

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, lastRecordId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Subscriber sub = new Subscriber(
                        rs.getLong("id"),
                        rs.getString("msisdn"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("sex"),
                        rs.getString("location"),
                        rs.getInt("reg_status"),
                        rs.getInt("status"),
                        rs.getString("status_reason"),
                        rs.getInt("preference"),
                        rs.getInt("sdp_status"),
                        true);

                //get data for last node and other generic data parameters
                sub.setLastNode(rs.getInt("last_node"));
                sub.setData0(rs.getString("data0"));
                sub.setData1(rs.getString("data1"));
                sub.setData2(rs.getString("data2"));
                sub.setData3(rs.getString("data3"));
                sub.setData4(rs.getString("data4"));
                sub.setData5(rs.getString("data5"));
                sub.setData6(rs.getString("data6"));
                sub.setData7(rs.getString("data7"));
                sub.setData8(rs.getString("data8"));
                sub.setData9(rs.getString("data9"));
                sub.setPref0(rs.getString("pref0"));
                sub.setPref1(rs.getString("pref1"));
                sub.setPref2(rs.getString("pref2"));
                sub.setPref3(rs.getString("pref3"));
                sub.setPref4(rs.getString("pref4"));
                sub.setPref5(rs.getString("pref5"));
                sub.setPref6(rs.getString("pref6"));
                sub.setPref7(rs.getString("pref7"));
                sub.setPref8(rs.getString("pref8"));
                sub.setPref9(rs.getString("pref9"));

                list.add(sub);
            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query (" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }

        return list;
    }

    public static ArrayList<ScheduledMessage> pollScheduledMessages(Connection conn, int limit,
            String currentTime, String currentDate, long lastMessageId) {

        ArrayList<ScheduledMessage> list = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT * FROM  tbl_scheduler_staging WHERE "
                    + "processing_status=0 AND "
                    + "send_start_time<=? AND "
                    + "send_end_time>=? AND "
                    + "schedule_date=? ORDER BY id ASC  LIMIT 0," + limit;

            stmt = conn.prepareStatement(query);
            stmt.setString(1, currentTime);
            stmt.setString(2, currentTime);
            stmt.setString(3, currentDate);
            rs = stmt.executeQuery();
            while (rs.next()) {

                Subscriber sub = new Subscriber();
                sub.setId(rs.getLong("sub_id"));
                sub.setMsisdn(rs.getString("msisdn"));

                list.add(new ScheduledMessage(
                        rs.getLong("id"),
                        sub,
                        rs.getInt("message_type"),
                        rs.getLong("reference_id"),
                        rs.getString("message"),
                        rs.getString("batch_id"),
                        rs.getString("schedule_date"),
                        rs.getString("send_start_time"),
                        rs.getString("send_end_time"),
                        rs.getInt("send_status"),
                        rs.getString("send_ref_id"),
                        rs.getString("send_log"),
                        rs.getString("delivery_status")
                ));

            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query (" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }

        return list;
    }

    public static Subscriber getSubscriber(Connection conn, String msidn) throws SQLException {

        Subscriber sub = new Subscriber();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT * FROM tbl_subscribers WHERE msisdn=? ORDER BY id ASC LIMIT 0,1";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, msidn);
            rs = stmt.executeQuery();

            while (rs.next()) {
                sub.setId(rs.getLong("id"));
                sub.setMsisdn(rs.getString("msisdn"));
                sub.setName(rs.getString("name"));
                sub.setAge(rs.getInt("age"));
                sub.setSex(rs.getString("sex"));
                sub.setLocation(rs.getString("location"));
                sub.setRegStatus(rs.getInt("reg_status"));
                sub.setStatus(rs.getInt("status"));
                sub.setReason(rs.getString("status_reason"));
                sub.setPreference(rs.getInt("preference"));
                sub.setSdpStatus(rs.getInt("sdp_status"));
                sub.setLoaded(true);
                sub.setLastNode(rs.getInt("last_node"));
                sub.setData0(rs.getString("data0"));
                sub.setData1(rs.getString("data1"));
                sub.setData2(rs.getString("data2"));
                sub.setData3(rs.getString("data3"));
                sub.setData4(rs.getString("data4"));
                sub.setData5(rs.getString("data5"));
                sub.setData6(rs.getString("data6"));
                sub.setData7(rs.getString("data7"));
                sub.setData8(rs.getString("data8"));
                sub.setData9(rs.getString("data9"));
                sub.setPref0(rs.getString("pref0"));
                sub.setPref1(rs.getString("pref1"));
                sub.setPref2(rs.getString("pref2"));
                sub.setPref3(rs.getString("pref3"));
                sub.setPref4(rs.getString("pref4"));
                sub.setPref5(rs.getString("pref5"));
                sub.setPref6(rs.getString("pref6"));
                sub.setPref7(rs.getString("pref7"));
                sub.setPref8(rs.getString("pref8"));
                sub.setPref9(rs.getString("pref9"));
            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query for number " + msidn + " (" + query + ")", ex);
            throw ex;
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }

        return sub;
    }

    public static int addSubscriber(Connection conn, Subscriber sub) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "INSERT INTO tbl_subscribers(msisdn,name,age,sex,location,reg_status,status,"
                    + "status_reason,created_on,last_updated_on,last_updated_by) "
                    + "VALUES(?,?,?,?,?,?,?,?,NOW(),NOW(),0)";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, sub.getMsisdn());
            stmt.setString(2, sub.getName());
            stmt.setInt(3, sub.getAge());
            stmt.setString(4, sub.getSex());
            stmt.setString(5, sub.getLocation());
            stmt.setInt(6, sub.getRegStatus());
            stmt.setInt(7, sub.getStatus());
            stmt.setString(8, sub.getReason());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for number " + sub + " (" + query + ")", ex);

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int addSubscriber(Connection conn, Subscriber sub, SubscriptionMessage message) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "INSERT INTO tbl_subscribers("
                    + "msisdn,name,age,sex,location,reg_status,status,"
                    + "status_reason,sdp_status,service_id,product_id,effective_time,expiry_time,created_on,last_updated_on,last_updated_by,last_node) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),0,?)";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, message.getMsisdn());
            stmt.setString(2, sub.getName());
            stmt.setInt(3, sub.getAge());
            stmt.setString(4, sub.getSex());
            stmt.setString(5, sub.getLocation());
            stmt.setInt(6, sub.getRegStatus());
            stmt.setInt(7, sub.getStatus());
            stmt.setString(8, sub.getReason());
            stmt.setInt(9, 1);
            stmt.setString(10, message.getServiceId());
            stmt.setString(11, message.getProductId());
            stmt.setString(12, message.getEffectiveTime());
            stmt.setString(13, message.getExpiryTime());
            stmt.setInt(14, sub.getLastNode());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for number " + sub + " (" + query + ")", ex);

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateSubscriberParamater(Connection conn, String paramName, String paramValue, int newLastNode, int newRegStatus, Subscriber subscriber) {
        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET " + paramName + "=?, last_updated_on = NOW(), last_node=?, reg_status=?, last_updated_by=0  WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, paramValue);
            stmt.setInt(2, newLastNode);
            stmt.setInt(3, newRegStatus);
            stmt.setLong(4, subscriber.getId());
            log.info("query: " + query + " parameters (" + paramName + ", " + paramValue + ", " + ", last node: " + newLastNode + ", reg_status:" + newRegStatus + ", " + subscriber + ")");
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for subscriber " + subscriber + " & param value = " + paramValue + "(" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateSubscriberParamater(Connection conn, String paramName, int paramValue, int newLastNode, int newRegStatus, Subscriber subscriber) {
        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET " + paramName + "=?, last_updated_on = NOW(), last_node=?, reg_status=?, last_updated_by=0  WHERE id=?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, paramValue);
            stmt.setInt(2, newLastNode);
            stmt.setInt(3, newRegStatus);
            stmt.setLong(4, subscriber.getId());
            log.debug("query: " + query + " parameters (" + paramName + ", " + paramValue + ", " + ", last node: " + newLastNode + ", reg_status:" + newRegStatus + ", " + subscriber + ")");
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for subscriber " + subscriber + " & param value = " + paramValue + "(" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateSubscriberNullParamater(Connection conn, int newLastNode, int newRegStatus, Subscriber subscriber) {
        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET last_updated_on = NOW(), last_node=?, reg_status=?, last_updated_by=0  WHERE id=?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, newLastNode);
            stmt.setInt(2, newRegStatus);
            stmt.setLong(3, subscriber.getId());
            log.debug("query: " + query + " parameters (last node: " + newLastNode + ", reg_status:" + newRegStatus + ", " + subscriber + ")");
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for subscriber " + subscriber + "(" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateSubscriberPreference(Connection conn, int regStatus, int preference, Subscriber subscriber) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET reg_status=?, preference=?, last_updated_on = NOW(), last_updated_by=0  WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, regStatus);
            stmt.setInt(2, preference);
            stmt.setLong(3, subscriber.getId());

            log.info("query: " + query + " parameters (" + RegistrationStatus.PENDING + ", " + preference + ", " + subscriber + ")");

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for subscriber " + subscriber + " & preference = " + preference + "(" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateLastMatchDate(Connection conn, Subscriber subscriber) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET last_shared_on=?, last_updated_on = NOW(), last_updated_by=0  WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            stmt.setLong(2, subscriber.getId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the " + subscriber + "(" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int acknowledgeInboxMessage(Connection conn, InboxMessage message) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_inbound_messages SET processing_status=1 WHERE message_id=?";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, message.getMessageId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for number " + message + " (" + query + ")", ex);

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int acknowledgeSubscription(Connection conn, SubscriptionMessage message) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscription_messages SET processing_status=1 WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, message.getMessageId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for number " + message + " (" + query + ")", ex);

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int acknowledgeDeliveryReceipt(Connection conn, DeliveryMessage message) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_delivery_receipts SET processing_status=1 WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, message.getMessageId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for number " + message + " (" + query + ")", ex);

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int pauseSubscriber(Connection conn, Subscriber sub) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET status=0,last_updated_on=NOW(), last_updated_by=0, status_reason='user initiated' WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, sub.getId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int resumeSubscriber(Connection conn, Subscriber sub) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET status=1,last_updated_on=NOW(), last_updated_by=0, status_reason='user initiated' WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, sub.getId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateSubscriptionParameters(Connection conn, Subscriber sub, SubscriptionMessage message) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET "
                    + "sdp_status=1,"
                    + "service_id=?,"
                    + "product_id=?,"
                    + "effective_time=?,"
                    + "expiry_time=?,"
                    + "last_updated_on=NOW(), last_updated_by=0 WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, message.getServiceId());
            stmt.setString(2, message.getProductId());
            stmt.setString(3, message.getEffectiveTime());
            stmt.setString(4, message.getExpiryTime());
            stmt.setLong(5, sub.getId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updatePersonalParameters(Connection conn, Subscriber sub) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_subscribers SET "
                    + "name=?,"
                    + "age=?,"
                    + "sex=?,"
                    + "location=?,"
                    + "reg_status=?,"
                    + "status=?,"
                    + "status_reason=?,"
                    + "last_updated_on=NOW(), last_updated_by=0 WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, sub.getName());
            stmt.setInt(2, sub.getAge());
            stmt.setString(3, sub.getSex());
            stmt.setString(4, sub.getLocation());
            stmt.setInt(5, sub.getRegStatus());
            stmt.setInt(6, sub.getStatus());
            stmt.setString(7, sub.getReason());
            stmt.setLong(8, sub.getId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int deleteSubscriber(Connection conn, Subscriber sub) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = " DELETE FROM tbl_subscribers WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, sub.getId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int addJob(Connection conn, Job job) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "INSERT INTO tbl_matching_jobs(job_date,batch_id,total_produced,no_date_matches,no_dating_tips,no_info_sms,status,"
                    + "reason,created_on,last_updated_on) "
                    + "VALUES(?,?,?,?,?,?,?,?,NOW(),NOW())";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, job.getJobDate());
            stmt.setString(2, job.getBatchId());
            stmt.setLong(3, job.getTotalProduced());
            stmt.setLong(4, job.getNoOfDateMatches());
            stmt.setLong(5, job.getNoOfDatingTips());
            stmt.setLong(6, job.getNoOfInfoSMS());
            stmt.setInt(7, job.getStatus());
            stmt.setString(8, job.getReason());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for number " + job + " (" + query + ")", ex);

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateJob(Connection conn, Job job) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_matching_jobs SET "
                    + "total_produced=?,"
                    + "no_date_matches=?,"
                    + "no_dating_tips=?,"
                    + "no_info_sms=?,"
                    + "status=?,"
                    + "reason=?,"
                    + "initiator=?,"
                    + "initiator_comments=?,"
                    + "last_updated_on=NOW() WHERE job_date=? AND batch_id=?";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, job.getTotalProduced());
            stmt.setLong(2, job.getNoOfDateMatches());
            stmt.setLong(3, job.getNoOfDatingTips());
            stmt.setLong(4, job.getNoOfInfoSMS());
            stmt.setInt(5, job.getStatus());
            stmt.setString(6, job.getReason());
            stmt.setInt(7, job.getInitiator());
            stmt.setString(8, job.getInitiatorComments());
            stmt.setString(9, job.getJobDate());
            stmt.setString(10, job.getBatchId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static ArrayList<Job> getJobs(Connection conn, String jobDate) throws SQLException {

        ArrayList<Job> jobs = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT * FROM tbl_matching_jobs WHERE job_date=? ORDER BY id ASC";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, jobDate);
            rs = stmt.executeQuery();

            while (rs.next()) {

                jobs.add(new Job(
                        rs.getInt("id"),
                        rs.getString("job_date"),
                        rs.getString("batch_id"),
                        rs.getLong("total_produced"), //total_produced
                        rs.getLong("no_date_matches"),
                        rs.getLong("no_dating_tips"),
                        rs.getLong("no_info_sms"),
                        rs.getInt("status"),
                        rs.getString("reason"),
                        rs.getInt("initiator"),
                        rs.getString("initiator_comments"))
                );
            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query for " + jobDate + " (" + query + ")", ex);
            throw ex;
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }

        return jobs;
    }

    public static int addSheduledMessage(Connection conn, ScheduledMessage message) throws SQLException {
        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "INSERT INTO tbl_scheduler_staging("
                    + "message_type,"
                    + "reference_id,"
                    + "message,"
                    + "schedule_date,"
                    + "send_start_time,"
                    + "send_end_time,"
                    + "send_status,"
                    + "send_ref_id,"
                    + "delivery_status,"
                    + "created_on,"
                    + "last_updated_on,"
                    + "sub_id,"
                    + "msisdn, "
                    + "batch_id,"
                    + "send_log) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,NOW(),NOW(),?,?,?,?)";

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, message.getMessageType());
            stmt.setLong(2, message.getReferenceId());
            stmt.setString(3, message.getMessage());
            stmt.setString(4, message.getScheduleDate());
            stmt.setString(5, message.getSendStartTime());
            stmt.setString(6, message.getSendEndTime());
            stmt.setInt(7, message.getSendStatus());
            stmt.setString(8, message.getSendRefId());
            stmt.setString(9, message.getDeliveryStatus());
            stmt.setLong(10, message.getSubscriber().getId());
            stmt.setString(11, message.getSubscriber().getMsisdn());
            stmt.setString(12, message.getBatchId());
            stmt.setString(13, message.getSendLog());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for " + message + " (" + query + ")", ex);
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateProcessedScheduledMessage(Connection conn, ScheduledMessage message) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_scheduler_staging SET "
                    + "send_status=?,"
                    + "send_ref_id=?,"
                    + "send_log=?,"
                    + "processing_status=?,"
                    + "last_updated_on=NOW() WHERE id=?";

            stmt = conn.prepareStatement(query);
            stmt.setInt(1, message.getSendStatus());
            stmt.setString(2, message.getSendRefId());
            stmt.setString(3, message.getSendLog());
            stmt.setInt(4, 1);
            stmt.setLong(5, message.getId());

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateDeliveredScheduledMessage(Connection conn, String deliveryStatus, long id, String msisdn) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_scheduler_staging SET "
                    + "delivery_status=?,"
                    + "last_updated_on=NOW() WHERE id=? AND msisdn=?";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, deliveryStatus);
            stmt.setLong(2, id);
            stmt.setString(3, msisdn);

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int deleteScheduledMessageNotDelivered(Connection conn, long id, String msisdn) throws SQLException {

        PreparedStatement stmt = null;
        String query;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = " DELETE FROM tbl_subscribers WHERE id=? AND msisdn=?";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, id);
            stmt.setString(2, msisdn);

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    /**
     *
     * @param conn
     * @param subId subscriber id
     * @param lowerAge lower limit for age
     * @param upperAge upper limit for age
     * @param sex sex
     * @param location location of subscriber
     * @param preference preference
     * @return subscriber
     * @throws SQLException
     */
    public static Subscriber getDateMatch(Connection conn, long subId, int lowerAge, int upperAge,
            String sex, String location, int preference) throws SQLException {

        Subscriber sub = null;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT "
                    + "id,msisdn,name,age,sex,location "
                    + "FROM "
                    + "tbl_subscribers WHERE "
                    + "status = 1 AND "
                    + "reg_status=3 AND "
                    + "id != ? AND "
                    + "sex = ? AND "
                    + "location = ? AND "
                    + "preference = ? AND "
                    + "age >= ? AND "
                    + "age <= ? AND "
                    + "last_shared_on < CURRENT_DATE AND "
                    + "id NOT IN (SELECT reference_id FROM tbl_scheduler_staging WHERE sub_id=? AND message_type=1) "
                    + "ORDER BY id ASC "
                    + "LIMIT 0,1;";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, subId);
            stmt.setString(2, sex);
            stmt.setString(3, location);
            stmt.setInt(4, preference);
            stmt.setInt(5, lowerAge);
            stmt.setInt(6, upperAge);
            stmt.setLong(7, subId);
            rs = stmt.executeQuery();

            //id,msisdn,name,age,sex,location 
            if (rs.next()) {
                sub = new Subscriber();
                sub.setId(rs.getLong("id"));
                sub.setMsisdn(rs.getString("msisdn"));
                sub.setName(rs.getString("name"));
                sub.setAge(rs.getInt("age"));
                sub.setSex(rs.getString("sex"));
                sub.setLocation(rs.getString("location"));
                sub.setLoaded(true);
            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query for sub " + subId + " (" + query + ")", ex);
            throw ex;
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }

        return sub;
    }

    public static DatingTip getDatingTip(Connection conn, long subId) throws SQLException {

        DatingTip tip = null;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;

        try {
            query = "SELECT  id, tip FROM tbl_tips WHERE "
                    + "status = 1 AND "
                    + "effective_date <= CURRENT_DATE AND "
                    + "expiry_date >= CURRENT_DATE AND "
                    + "id NOT IN (SELECT reference_id FROM tbl_scheduler_staging WHERE sub_id=? AND message_type=2) "
                    + "ORDER BY id ASC "
                    + "LIMIT 0,1";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, subId);
            rs = stmt.executeQuery();

            //id,msisdn,name,age,sex,location 
            if (rs.next()) {
                tip = new DatingTip();
                tip.setId(rs.getInt("id"));
                tip.setTip(rs.getString("tip"));
            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query for sub " + subId + " (" + query + ")", ex);
            throw ex;
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return tip;
    }

    public static int addActivityLog(Connection conn, ActivityLog activityLog) throws SQLException {
        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "INSERT INTO tbl_activity_logs("
                    + "sub_id,"
                    + "msisdn,"
                    + "request_type,"
                    + "operation,"
                    + "initiator,"
                    + "in_request_id,"
                    + "in_message_text,"
                    + "out_message_text,"
                    + "out_short_code,"
                    + "out_correlator,"
                    + "out_link_id,"
                    + "out_batch_id, "
                    + "out_send_status,"
                    + "out_send_desc,"
                    + "out_send_ref_id,"
                    + "out_send_time,"
                    + "out_delivery_status,"
                    + "out_delivery_time,"
                    + "created_on,"
                    + "last_updated_on,"
                    + "out_address) "
                    + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW(),?)";

            stmt = conn.prepareStatement(query);
            stmt.setLong(1, activityLog.getSubscriber().getId());
            stmt.setString(2, activityLog.getSubscriber().getMsisdn());
            stmt.setInt(3, activityLog.getRequestType());
            stmt.setInt(4, activityLog.getOperation());
            stmt.setInt(5, activityLog.getInitiator());
            stmt.setLong(6, activityLog.getInRequestId());
            stmt.setString(7, activityLog.getInMessageText());
            stmt.setString(8, activityLog.getOutMessageText());
            stmt.setString(9, activityLog.getShortCode());
            stmt.setString(10, activityLog.getCorrelator());
            stmt.setString(11, activityLog.getLinkedId());
            stmt.setString(12, activityLog.getBatchId());
            stmt.setInt(13, activityLog.getSendStatus());
            stmt.setString(14, activityLog.getSendDesc());
            stmt.setString(15, activityLog.getSendRefId());
            stmt.setString(16, activityLog.getSendTime());
            stmt.setString(17, activityLog.getDeliveryStatus());
            stmt.setString(18, activityLog.getDeliveryTime());
            stmt.setString(19, activityLog.getSubscriber().getMsisdn()); //redudant

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the query for " + activityLog + " (" + query + ")", ex);
            throw ex;

        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static int updateActivityLogDeliveryStatus(Connection conn, String deliveryStatus, String msisdn, String correlator) {

        PreparedStatement stmt = null;
        String query = null;
        int executionStatus = EXECUTE_FAIL;// defaulf status is failed

        try {
            query = "UPDATE tbl_activity_logs SET out_delivery_status=?, last_updated_on = NOW(), out_delivery_time=NOW()"
                    + "  WHERE out_correlator=? AND msisdn=?";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, deliveryStatus);
            stmt.setString(2, correlator);
            stmt.setString(3, msisdn);

            //execute the query
            stmt.execute();

            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

            executionStatus = EXECUTE_SUCCESS;

        } catch (SQLException ex) {
            log.error("Error executing the " + msisdn + ", " + correlator + ", " + deliveryStatus + "(" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return executionStatus;
    }

    public static Map<Integer, Node> getNodes(Connection conn) {
        HashMap<Integer, Node> map = new HashMap<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String query = null;
        try {
            query = "SELECT * FROM tbl_notification_messages ORDER BY id ASC";

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getInt("id"), new Node(
                        rs.getInt("id"),
                        rs.getString("message"),
                        rs.getString("validation_rule"),
                        rs.getString("validation_failure_message"),
                        rs.getString("field_name"),
                        rs.getInt("integer_value"),
                        rs.getInt("new_reg_status"),
                        rs.getInt("pause"),
                        rs.getInt("final"),
                        rs.getInt("next_node"),
                        rs.getString("matching_query"))
                );

            }
            //close connection 
            try {
                rs.close();
            } catch (SQLException ex) {
                log.warn("error closing result set", ex);
            }
            //close the statement
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.warn("error closing statement", ex);
            }

        } catch (SQLException ex) {
            log.error("Error executing the query (" + query + ")", ex);
        } finally {
            //close the resourrces opened
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                log.warn("An error closing a rs and stmt", ex);
            }
        }
        return map;
    }

}
