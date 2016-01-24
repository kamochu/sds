package com.sds.core;

import com.sds.core.conf.RequestTypes;

/**
 *
 * @author Samuel Kamochu
 */
public class DeliveryMessage implements Message {

    private long messageId;
    private String destAddress;
    private String correlator;
    private String deliveryStatus;

    public DeliveryMessage() {
    }

    public DeliveryMessage(long messageId, String destAddress, String correlator, String deliveryStatus) {
        this.messageId = messageId;
        this.destAddress = destAddress;
        this.correlator = correlator;
        this.deliveryStatus = deliveryStatus;
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
     * @return the destAddress
     */
    public String getDestAddress() {
        return destAddress;
    }

    /**
     * @param destAddress the destAddress to set
     */
    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    /**
     * @return the correlator
     */
    public String getCorrelator() {
        return correlator;
    }

    /**
     * @param correlator the correlator to set
     */
    public void setCorrelator(String correlator) {
        this.correlator = correlator;
    }

    /**
     * @return the deliveryStatus
     */
    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * @param deliveryStatus the deliveryStatus to set
     */
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @Override
    public String toString() {
        return "DeliveryMessage{" + "messageId=" + messageId + ", destAddress=" + destAddress + ", correlator=" + correlator + ", deliveryStatus=" + deliveryStatus + '}';
    }

    @Override
    public String getServiceId() {
        return null;
    }

    @Override
    public String getAddress() {
        return this.destAddress;
    }

    @Override
    public String getShortCode() {
        return null;
    }

    @Override
    public String getSenderCorrelator() {
        return null;
    }

    @Override
    public String getBatchId() {
        return null;
    }

    @Override
    public String getIncomingReferenceId() {
        return null;
    }

    @Override
    public String getLinkId() {
        return null;
    }

    @Override
    public int getRequestType() {
        return RequestTypes.DELIVERY_RECEIPT_REQUEST;
    }

    @Override
    public String getPrefix() {
        return RequestTypes.DELIVERY_PREFIX;
    }

    @Override
    public String getMessage() {
        return "N/A";
    }

}
