package com.sds.core;

/**
 *
 * @author Samuel Kamochu
 */
public interface Message {

    public String getServiceId();

    public String getAddress();

    public String getShortCode();

    public String getSenderCorrelator();

    public String getBatchId();

    public long getMessageId();
    
    public String getMessage();

    public String getIncomingReferenceId();

    public String getLinkId();
    
    public int getRequestType();
    
    public String getPrefix(); 

}
