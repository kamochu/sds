package com.sds.core;

/**
 *
 * @author kamochu
 */
public class ActivityLog {

    private int id;
    private Subscriber subscriber;
    private int requestType;
    private int operation;
    private int initiator;
    private long inRequestId;
    private String inMessageText;
    private String outMessageText;
    private String shortCode;
    private String correlator;
    private String linkedId;
    private String batchId;
    private int sendStatus;
    private String sendDesc;
    private String sendRefId;
    private String sendTime;
    private String deliveryStatus;
    private String deliveryTime;

    public ActivityLog() {
    }

    public ActivityLog(int id) {
        this.id = id;
    }

    public ActivityLog(int id, Subscriber subscriber) {
        this.id = id;
        this.subscriber = subscriber;
    }

    public ActivityLog(int id, Subscriber subscriber, int requestType, int operation, int initiator, long inRequestId, String inMessageText, String outMessageText, String shortCode, String correlator, String linkedId, String batchId, int sendStatus, String sendDesc, String sendRefId, String sendTime, String deliveryStatus, String deliveryTime) {
        this.id = id;
        this.subscriber = subscriber;
        this.requestType = requestType;
        this.operation = operation;
        this.initiator = initiator;
        this.inRequestId = inRequestId;
        this.inMessageText = inMessageText;
        this.outMessageText = outMessageText;
        this.shortCode = shortCode;
        this.correlator = correlator;
        this.linkedId = linkedId;
        this.batchId = batchId;
        this.sendStatus = sendStatus;
        this.sendDesc = sendDesc;
        this.sendRefId = sendRefId;
        this.sendTime = sendTime;
        this.deliveryStatus = deliveryStatus;
        this.deliveryTime = deliveryTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public int getInitiator() {
        return initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }

    public long getInRequestId() {
        return inRequestId;
    }

    public void setInRequestId(long inRequestId) {
        this.inRequestId = inRequestId;
    }

    public String getInMessageText() {
        return inMessageText;
    }

    public void setInMessageText(String inMessageText) {
        this.inMessageText = inMessageText;
    }

    public String getOutMessageText() {
        return outMessageText;
    }

    public void setOutMessageText(String outMessageText) {
        this.outMessageText = outMessageText;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getCorrelator() {
        return correlator;
    }

    public void setCorrelator(String correlator) {
        this.correlator = correlator;
    }

    public String getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(String linkedId) {
        this.linkedId = linkedId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSendDesc() {
        return sendDesc;
    }

    public void setSendDesc(String sendDesc) {
        this.sendDesc = sendDesc;
    }

    public String getSendRefId() {
        return sendRefId;
    }

    public void setSendRefId(String sendRefId) {
        this.sendRefId = sendRefId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @Override
    public String toString() {
        return "ActivityLog{" + "id=" + id + ", subscriber=" + subscriber + ", requestType=" + requestType + ", operation=" + operation + ", initiator=" + initiator + ", inRequestId=" + inRequestId + ", inMessageText=" + inMessageText + ", outMessageText=" + outMessageText + ", shortCode=" + shortCode + ", correlator=" + correlator + ", linkedId=" + linkedId + ", batchId=" + batchId + ", sendStatus=" + sendStatus + ", sendDesc=" + sendDesc + ", sendRefId=" + sendRefId + ", sendTime=" + sendTime + ", deliveryStatus=" + deliveryStatus + ", deliveryTime=" + deliveryTime + '}';
    }

}
