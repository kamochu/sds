package com.sds.core.conf;

/**
 *
 * @author kamochu
 */
public class RequestTypes {

    public final static int INBOUND_SMS_REQUEST = 1;
    public final static int SUBSCRIPTION_REQUEST = 2;
    public final static int OPERATOR_REQUEST = 3;
    public final static int SYSTEM_REQUEST = 4;
    public final static int DELIVERY_RECEIPT_REQUEST = 98;
    public final static int OTHERS = 99;

    public final static String INBOUND_PREFIX = "INB";
    public final static String SUBSCRIPTION_PREFIX = "SUB";
    public final static String SYSTEM_PREFIX = "SCH";
    public final static String DELIVERY_PREFIX = "DEL";
}
