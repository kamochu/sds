package com.sds.core.util;

import com.google.common.base.Splitter;
import com.sds.core.ActivityLog;
import com.sds.core.InboxMessage;
import com.sds.core.Message;
import com.sds.core.ScheduledMessage;
import com.sds.core.SendMessage;
import com.sds.core.Subscriber;
import com.sds.dao.DataManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class MessageUtils {

    private final static String SPLIT_PATTERN = "|";
    public final static Splitter RESPONSE_SPLITTER = Splitter.on(SPLIT_PATTERN);
    private final static Logger log = Logger.getLogger(MessageUtils.class.getName());
    private final static String BASE_URL = "http://localhost/ssg/send/sms/";
    public final static int SEND_SUCCESS = 0;
    public final static int SEND_FAIL_CONNECTION = -2;
    public final static int SEND_FAIL_ENCODING = -1;
    public final static int SEND_GENERAL = -99;

    /**
     * Acknowledge the message processed
     *
     * @param message the incoming being acknowledged
     */
    public void acknowledge(InboxMessage message) {
        log.info("ACK: " + message);

    }

    /**
     * send message
     *
     * @param message
     * @param incoming
     * @return
     */
    public static Response sendMessage(String message, Message incoming) {
        return sendMessage(message, incoming.getServiceId(), incoming.getBatchId(), incoming);
    }

    /**
     * send message
     *
     * @param message
     * @param serviceID
     * @param batchID
     * @param incoming
     * @return
     */
    public static Response sendMessage(String message, String serviceID, String batchID, Message incoming) {
        String response;
        log.info("SEND MESSAGE: " + message);
        String url;
        String params = "?"
                + "service_id=" + serviceID
                + "&dest_address=" + incoming.getAddress()
                + "&sender_address=" + incoming.getShortCode()
                + "&correlator=" + incoming.getSenderCorrelator() //
                + "&batch_id=" + batchID //generate one here
                + "&message=" + message
                + "&linked_incoming_msg_id=" + incoming.getIncomingReferenceId()
                + "&link_id=" + incoming.getLinkId();

        SendMessage outMessage = new SendMessage();
        outMessage.setServiceId(serviceID);
        outMessage.setMsisdn(incoming.getAddress());
        outMessage.setShortCode(incoming.getShortCode());
        outMessage.setCorrelator(incoming.getSenderCorrelator());
        outMessage.setBatchId(batchID);
        outMessage.setMessage(message);
        outMessage.setLinkedIncomingMessageId(message);
        outMessage.setLinkedId(incoming.getLinkId());
        outMessage.setSendTime(SendMessage.TIMESTAMP_FORMAT.format(new Date()));

        try {
            url = BASE_URL + URLEncoder.encode(params, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.error("error send message encoding url params (" + params + ")", ex);
            response = "error send message encoding url params";
            return new Response(SEND_FAIL_ENCODING, response, outMessage);
        }
        try {
            response = HttpConnectionManager.sendGet(url);
        } catch (IOException ex) {
            log.error("error sending message (" + url + ")", ex);
            response = "HTTP connection error";
            return new Response(SEND_FAIL_CONNECTION, response, outMessage);
        }

        log.info("out message" + outMessage);
        return new Response(SEND_SUCCESS, response, outMessage);
    }

    /**
     * sends a scheduled message through SSG
     *
     * @param shortCode short code to be used in sending out the message
     * @param serviceID service id to be used in sending out the message
     * @param scheduledMessage message scheduled for sending out messages
     * @return response object that has sending status and send message
     * parameters in it
     */
    public static Response sendMessage(String shortCode, String serviceID, ScheduledMessage scheduledMessage) {
        String response;
        log.info("SEND MESSAGE: " + scheduledMessage);
        String url;
        String params = "?"
                + "service_id=" + serviceID
                + "&dest_address=" + scheduledMessage.getSubscriber().getMsisdn()
                + "&sender_address=" + shortCode
                + "&correlator=" + scheduledMessage.getSenderCorrelator()
                + "&batch_id=" + scheduledMessage.getBatchId() //generate one here
                + "&message=" + scheduledMessage.getMessage()
                + "&linked_incoming_msg_id=" + scheduledMessage.getSenderCorrelator();

        SendMessage outMessage = new SendMessage();
        outMessage.setServiceId(serviceID);
        outMessage.setMsisdn(scheduledMessage.getSubscriber().getMsisdn());
        outMessage.setShortCode(shortCode);
        outMessage.setCorrelator(scheduledMessage.getSenderCorrelator());
        outMessage.setBatchId(scheduledMessage.getBatchId());
        outMessage.setMessage(scheduledMessage.getMessage());
        outMessage.setLinkedIncomingMessageId(scheduledMessage.getSenderCorrelator());
        outMessage.setLinkedId(null);
        outMessage.setSendTime(SendMessage.TIMESTAMP_FORMAT.format(new Date()));

        try {
            url = BASE_URL + URLEncoder.encode(params, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            log.error("error send message encoding url params (" + params + ")", ex);
            response = "URL Encoding error";
            return new Response(SEND_FAIL_ENCODING, response, outMessage);
        }
        try {
            response = HttpConnectionManager.sendGet(url);
        } catch (IOException ex) {
            log.error("error sending message (" + url + ")", ex);
            response = "HTTP connection error";
            return new Response(SEND_FAIL_CONNECTION, response, outMessage);
        }
        log.info("out message" + outMessage);
        return new Response(SEND_SUCCESS, response, outMessage);
    }

    /**
     * adds an activity for an incoming message
     *
     * @param con database connection
     * @param operation operation type
     * @param initiator initiator user id, normal 0
     * @param subscriber subscriber instance
     * @param message incoming request object
     * @param response send SMS response object
     */
    public static final void addActivityLog(Connection con, int operation, int initiator, Subscriber subscriber, Message message, Response response) {

        ActivityLog activityLog = new ActivityLog();
        activityLog.setSubscriber(subscriber);
        activityLog.setRequestType(message.getRequestType());
        activityLog.setOperation(operation);
        activityLog.setInitiator(initiator);
        activityLog.setInRequestId(message.getMessageId());
        activityLog.setInMessageText(message.getMessage());
        activityLog.setOutMessageText(response.getOutMessage().getMessage());
        activityLog.setShortCode(response.getOutMessage().getShortCode());
        activityLog.setCorrelator(response.getOutMessage().getCorrelator());
        activityLog.setLinkedId(response.getOutMessage().getLinkedId());
        activityLog.setBatchId(response.getOutMessage().getBatchId());
        activityLog.setSendTime(response.getOutMessage().getSendTime());

        if (response.getStatus() == MessageUtils.SEND_SUCCESS) {
            String str = response.getResponse();
            Iterator<String> iterator = MessageUtils.RESPONSE_SPLITTER.split(str).iterator();
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
                        sendStatus = response.getStatus();
                    }
                } else if (i == 4) {
                    refId = part;
                }
            }
            activityLog.setSendStatus(sendStatus);
            activityLog.setSendDesc(response.getResponse());
            activityLog.setSendRefId(refId);
        } else {
            activityLog.setSendStatus(response.getStatus());
            activityLog.setSendDesc(response.getResponse());
            activityLog.setSendRefId(null);
        }
        try {
            if (DataManager.addActivityLog(con, activityLog) == DataManager.EXECUTE_FAIL) {
                log.error("unbale to add the activity log: " + activityLog);
            }
        } catch (SQLException ex) {
            log.error("unable to update the scheduled message " + activityLog, ex);
        }
    }

    /**
     * add activity log for a scheduled message
     *
     * @param con database connection instance
     * @param operation operation type id
     * @param initiator initiator osf the system, by default set to 0 (system)
     * @param message scheduled message object
     * @param response send SMS response object
     */
    public static final void addActivityLog(Connection con, int operation, int initiator, ScheduledMessage message, Response response) {

        ActivityLog activityLog = new ActivityLog();
        activityLog.setSubscriber(message.getSubscriber());
        activityLog.setRequestType(message.getRequestType());
        activityLog.setOperation(operation);
        activityLog.setInitiator(initiator);
        activityLog.setInRequestId(0);
        activityLog.setInMessageText(null);
        activityLog.setOutMessageText(response.getOutMessage().getMessage());
        activityLog.setShortCode(response.getOutMessage().getShortCode());
        activityLog.setCorrelator(response.getOutMessage().getCorrelator());
        activityLog.setLinkedId(response.getOutMessage().getLinkedId());
        activityLog.setBatchId(response.getOutMessage().getBatchId());
        activityLog.setSendTime(response.getOutMessage().getSendTime());

        if (response.getStatus() == MessageUtils.SEND_SUCCESS) {
            String str = response.getResponse();
            Iterator<String> iterator = MessageUtils.RESPONSE_SPLITTER.split(str).iterator();
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
                        sendStatus = response.getStatus();
                    }
                } else if (i == 4) {
                    refId = part;
                }
            }
            activityLog.setSendStatus(sendStatus);
            activityLog.setSendDesc(response.getResponse());
            activityLog.setSendRefId(refId);
        } else {
            activityLog.setSendStatus(response.getStatus());
            activityLog.setSendDesc(response.getResponse());
            activityLog.setSendRefId(null);
        }

        try {
            if (DataManager.addActivityLog(con, activityLog) == DataManager.EXECUTE_FAIL) {
                log.error("unbale to add the activity log: " + activityLog);
            }
        } catch (SQLException ex) {
            log.error("unable to update the scheduled message " + activityLog, ex);
        }
    }
}
