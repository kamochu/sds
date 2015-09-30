/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sds.core;

import java.text.SimpleDateFormat;

/**
 *
 * @author kamochu
 */
public class SendMessage {

    public static SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private String serviceId;
    private String msisdn;
    private String shortCode;
    private String correlator;
    private String batchId;
    private String message;
    private String linkedIncomingMessageId;
    private String linkedId;
    private String sendTime;

    public SendMessage() {
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
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

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLinkedIncomingMessageId() {
        return linkedIncomingMessageId;
    }

    public void setLinkedIncomingMessageId(String linkedIncomingMessageId) {
        this.linkedIncomingMessageId = linkedIncomingMessageId;
    }

    public String getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(String linkedId) {
        this.linkedId = linkedId;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "SendMessage{" + "serviceId=" + serviceId + ", destAddress=" + msisdn + ", senderAddress=" + shortCode + ", correlator=" + correlator + ", batchId=" + batchId + ", message=" + message + ", linkedIncomingMessageId=" + linkedIncomingMessageId + ", linkedId=" + linkedId + ", sendTime=" + sendTime + '}';
    }

}
