package com.sds.core;

/**
 *
 * @author Samuel Kamochu
 *
 * Incoming message class - the class to be used in wrapping up message once
 * they are read from the database.
 */
public class InboxMessage implements Message {

    private long messageId;
    private String serviceId;
    private String linkId;
    private String traceUniqueId;
    private String correlator;
    private String message;
    private String senderAddress;
    private String destAddress;

    public InboxMessage() {
        this(0, null, null, null, null, null, null, null);
    }

    public InboxMessage(long messageId, String serviceId, String linkId, String traceUniqueId, String correlator, String message, String senderAddress, String destAddress) {
        this.messageId = messageId;
        this.serviceId = serviceId;
        this.linkId = linkId;
        this.traceUniqueId = traceUniqueId;
        this.correlator = correlator;
        this.message = message;
        this.senderAddress = senderAddress;
        this.destAddress = destAddress;
    }

    @Override
    public String toString() {
        return "InboxMessage{" + "messageId=" + messageId + ", serviceId=" + serviceId + ", linkId=" + linkId + ", traceUniqueId=" + traceUniqueId + ", correlator=" + correlator + ", message=" + message + ", senderAddress=" + senderAddress + ", destAddress=" + destAddress + '}';
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getTraceUniqueId() {
        return traceUniqueId;
    }

    public void setTraceUniqueId(String traceUniqueId) {
        this.traceUniqueId = traceUniqueId;
    }

    public String getCorrelator() {
        return correlator;
    }

    public void setCorrelator(String correlator) {
        this.correlator = correlator;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    @Override
    public String getAddress() {
        return senderAddress;
    }

    @Override
    public String getShortCode() {
        return destAddress;
    }

    @Override
    public String getBatchId() {
        return "0";
    }

    @Override
    public long getMessageId() {
        return messageId;
    }

    @Override
    public String getIncomingReferenceId() {
        return getPrefix()+ messageId;
    }

    @Override
    public String getSenderCorrelator() {
        return getIncomingReferenceId();
    }

    @Override
    public int getRequestType() {
        return RequestTypes.INBOUND_SMS_REQUEST;
    }

    @Override
    public String getPrefix() {
        return RequestTypes.INBOUND_PREFIX;
    }

}
