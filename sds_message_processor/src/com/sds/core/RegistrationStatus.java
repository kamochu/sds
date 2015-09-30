package com.sds.core;

/**
 *
 * @author Samuel Kamochu
 */
public class RegistrationStatus {

    /**
     * REG_INITIAL = 1, first registration step
     */
    public final static int REG_INITIAL = 1;
    /**
     * REG_PENDING = 2, confirmed preference, pending subscription confirmed via
     * SDP
     */
    public final static int REG_PENDING = 2;
    /**
     * REG_CONFIRMED = 3, confirmed subscription
     */
    public final static int REG_CONFIRMED = 3;
    /**
     * REG_CANCELLED = 4, the user rejected the USSD reg prompt
     */
    public final static int REG_CANCELLED = 4;
    /**
     * DEREG_PENDING = 5, pending un-subscription confirmed via SDP
     */
    public final static int DEREG_PENDING = 5;
}
