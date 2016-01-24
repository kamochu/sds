package com.sds.core.conf;

/**
 *
 * @author Samuel Kamochu
 */
public class RegistrationStatus {

    /**
     * INITIAL = 1, first registration step
     */
    public final static int INITIAL = 1;
    /**
     * PENDING = 2, confirmed preference, pending subscription confirmed via
 SDP
     */
    public final static int PENDING = 2;
    /**
     * BASIC = 3, confirmed subscription
     */
    public final static int BASIC = 3;
    /**
     * REG_CANCELLED = 4, the user rejected the USSD reg prompt
     */
    public final static int ADVANCED = 4;
    /**
     * DEREG_PENDING = 5, pending un-subscription confirmed via SDP
     */
    public final static int COMPLETE = 5;
    
}
