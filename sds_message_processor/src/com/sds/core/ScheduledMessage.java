package com.sds.core;

/**
 *
 * @author Samuel Kamochu
 */
public class ScheduledMessage {

    private long id;
    private Subscriber subscriber;
    private int messageType;
    private long referenceId;
    private String message;
    private String batchId;
    private String scheduleDate;
    private String sendStartTime;
    private String sendEndTime;
    private int sendStatus;
    private String sendRefId;
    private String sendLog;
    private String deliveryStatus;

    public ScheduledMessage() {
    }

    public ScheduledMessage(long id, Subscriber subscriber, int messageType, long referenceId, String message, String batchId, String scheduleDate, String sendStartTime, String sendEndTime, int sendStatus, String sendRefId, String sendLog, String deliveryStatus) {
        this.id = id;
        this.subscriber = subscriber;
        this.messageType = messageType;
        this.referenceId = referenceId;
        this.message = message;
        this.batchId = batchId;
        this.scheduleDate = scheduleDate;
        this.sendStartTime = sendStartTime;
        this.sendEndTime = sendEndTime;
        this.sendStatus = sendStatus;
        this.sendRefId = sendRefId;
        this.sendLog = sendLog;
        this.deliveryStatus = deliveryStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getSendStartTime() {
        return sendStartTime;
    }

    public void setSendStartTime(String sendStartTime) {
        this.sendStartTime = sendStartTime;
    }

    public String getSendEndTime() {
        return sendEndTime;
    }

    public void setSendEndTime(String sendEndTime) {
        this.sendEndTime = sendEndTime;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSendRefId() {
        return sendRefId;
    }

    public void setSendRefId(String sendRefId) {
        this.sendRefId = sendRefId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getSendLog() {
        return sendLog;
    }

    public void setSendLog(String sendLog) {
        this.sendLog = sendLog;
    }

    @Override
    public String toString() {
        return "ScheduledMessage{" + "id=" + id + ", subscriber=" + subscriber + ", messageType=" + messageType + ", referenceId=" + referenceId + ", message=" + message + ", batchId=" + batchId + ", scheduleDate=" + scheduleDate + ", sendStartTime=" + sendStartTime + ", sendEndTime=" + sendEndTime + ", sendStatus=" + sendStatus + ", sendRefId=" + sendRefId + ", sendLog=" + sendLog + ", deliveryStatus=" + deliveryStatus + '}';
    }

    public String getSenderCorrelator() {
        return "SCH" + id;
    }

    public int getRequestType() {
        return RequestTypes.SYSTEM_REQUEST;
    }

    public String getPrefix() {
        return RequestTypes.SYSTEM_PREFIX;
    }

}
