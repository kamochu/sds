package com.sds.core.conf;

/**
 *
 * @author kamochu
 */
public class Notifications {

    //subscription flow messages
    public final static int SUBSCRIPTION_INITIAL_NODE_ID = 1; //the first message to be sent to subscriber
    public final static int SUBSCRIPTION_ALREADY_REGISTERED = 96;
    public final static int SUBSCRIPTION_UNSUPPORTED_REQUEST = 97;
    public final static int SUBSCRIPTION_GENERAL_TECHNICAL_ERROR = 98;

    //stop subscription flow messages
    public final static int STOP_SUCCESS = 92;
    public final static int STOP_TECHNICAL_ERROR = 93;
    public final static int STOP_NON_REGISTERED = 94;

    //register messages
    public final static int REG_GEN_TECHNICAL_FAILURE = 90;
    public final static int REG_GEN_INFORMATION = 91;

    //help messages 
    public final static int HELP_REGISTERED = 99;
    public final static int HELP_NON_REGISTERED = 100;
    public final static int HELP_GENERAL = 101;

    //pause messages
    public final static int PAUSE_SUCCESS = 102;
    public final static int PAUSE_ALREADY_PAUSED = 103;
    public final static int PAUSE_TECHNICAL_ERROR = 104;
    public final static int PAUSE_NON_REGISTERED = 105;

    //resume messages
    public final static int RESUME_REGISTERED = 106;
    public final static int RESUME_ALREADY_RESUMED = 107;
    public final static int RESUME_NON_REGISTERED = 108;
    public final static int RESUME_TECHNICAL_ERROR = 109;
}
