/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sds.core;

import com.sds.core.conf.RequestTypes;
import com.sds.core.conf.Configs;

/**
 *
 * @author Samuel Kamochu
 */
public class SubscriptionMessage implements Message {

    private long messageId;
    private String msisdn;
    private String subscriberId;
    private String productId;
    private String serviceId;
    private int updateType;
    private String updateDesc;
    private String effectiveTime;
    private String expiryTime;

    public SubscriptionMessage() {
    }

    public SubscriptionMessage(long messageId, String subscriberId, String productId, String serviceId, int updateType, String updateDesc, String effectiveTime, String expiryTime) {
        this.messageId = messageId;
        this.subscriberId = subscriberId;
        this.productId = productId;
        this.serviceId = serviceId;
        this.updateType = updateType;
        this.updateDesc = updateDesc;
        this.effectiveTime = effectiveTime;
        this.expiryTime = expiryTime;

        //init MSISDN
        initMsisdn();
    }

    private void initMsisdn() {
        if (this.subscriberId != null) {
            this.msisdn = "tel:" + this.subscriberId.substring(3);
        }
    }

    /**
     * @return the messageId
     */
    @Override
    public long getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * @return the subscriberId
     */
    public String getSubscriberId() {
        return subscriberId;
    }

    /**
     * @param subscriberId the subscriberId to set
     */
    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
        //initialize msisdn based on the subscriber id
        initMsisdn();
    }

    /**
     * @return the productId
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @param productId the productId to set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * @return the serviceId
     */
    @Override
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId the serviceId to set
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return the updateType
     */
    public int getUpdateType() {
        return updateType;
    }

    /**
     * @param updateType the updateType to set
     */
    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    /**
     * @return the updateDesc
     */
    public String getUpdateDesc() {
        return updateDesc;
    }

    /**
     * @param updateDesc the updateDesc to set
     */
    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    /**
     * @return the effectiveTime
     */
    public String getEffectiveTime() {
        return effectiveTime;
    }

    /**
     * @param effectiveTime the effectiveTime to set
     */
    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    /**
     * @return the expiryTime
     */
    public String getExpiryTime() {
        return expiryTime;
    }

    /**
     * @param expiryTime the expiryTime to set
     */
    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    @Override
    public String toString() {
        return "SubscriptionMessage{" + "messageId=" + messageId + ", msisdn=" + msisdn + ", subscriberId=" + subscriberId + ", productId=" + productId + ", serviceId=" + serviceId + ", updateType=" + updateType + ", updateDesc=" + updateDesc + ", effectiveTime=" + effectiveTime + ", expiryTime=" + expiryTime + '}';
    }

    @Override
    public String getAddress() {
        return getMsisdn();
    }

    @Override
    public String getShortCode() {
        return Configs.SHORT_CODE;
    }

    @Override
    public String getSenderCorrelator() {
        return getIncomingReferenceId();
    }

    @Override
    public String getBatchId() {
        return Configs.REPLY_BATCH_ID;
    }

    @Override
    public String getIncomingReferenceId() {
        return this.getPrefix() + getMessageId();
    }

    @Override
    public String getLinkId() {
        return null;
    }

    @Override
    public int getRequestType() {
        return RequestTypes.SUBSCRIPTION_REQUEST;
    }

    @Override
    public String getPrefix() {
        return RequestTypes.SUBSCRIPTION_PREFIX;
    }

    @Override
    public String getMessage() {
        return "N/A";
    }

}
