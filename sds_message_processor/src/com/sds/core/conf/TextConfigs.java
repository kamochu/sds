package com.sds.core.conf;

/**
 *
 * @author Samuel Kamochu
 */
public class TextConfigs {
    
    public final static String REG_INIT_INVALID_ARGUMENTS_MESSAGE_TEXT = "Dear customer. To register send Name Age Gender County to short code 60010 E.g. John 30 M Nairobi";
    public final static String REG_INIT_INVALID_AGE_MESSAGE_TEXT = "Dear customer. Your age is incorrect. It need's to be a number. E.g. Send John 30 M Nairobi to short codd 60010. Than you for using our service.";
    public final static String REG_INIT_INVALID_SEX_MESSAGE_TEXT = "Dear customer. Your gender is incorrect. Expected values are 'M' for male or 'F' for female. E.g. Example send John 30 M Nairobi to short codd 60010.";
    public final static String REG_INIT_INVALID_REG_STATUS = "Dear customer. Your account is already registered but the account status is invalid. Please call customer care for more information.";
    public final static String REG_INIT_SUCCESS_MESSAGE_TEXT_PART1 = "Dear ";
    public final static String REG_INIT_SUCCESS_MESSAGE_TEXT_PART2 = " Your profile has been created. We need to know what you are looking for. Reply with 1. for Partner 2. for Friend 3. for Sex. E.g. 2";

    public final static String REG_PREF_SUCCESS_SUBSCRIPTION_CONFIRM = "Dear customer, your dating service profile fully is not fully setup. Please send the word REGISTER to 60010.";
    public final static String REG_UPDATE_SUCCESSFUL_TEXT = "Congratulations! Your are now fully register for discovery dating service. You will start receiving date matches and dating tips. For more information, please send HELP to 60010.";
    public final static String REG_UPDATE_SUCCESSFUL_NOT_CONFIRMED_TEXT = "Your profile has been updated successfully. To complete your registration process please send REGISTER to 60010. Thank you for choosing Discovery Dating service.";
    public final static String REG_UPDATE_INVALID_INPUT_TEXT = "Dear customer. We expected a number to confirm your preference. Please reply with  1. Looking for Partner 2. Looking for Friendship 3. Looking for Sex. E.g. send 2 to 60010.";
    public final static String REG_UPDATE_TECHNICAL_ERROR_TEXT = "Dear customer. We are unable to process your request at the moment.Please try again later.  Reply with 1. for Partner 2. for Friend 3. for Sex  E.g. Send 2 to short 60010.";

    public final static String REG_GEN_TECHNICAL_FAILURE_TEXT = "Dear customer. We are unable to process your request at the moment. Please try again later. Thank you.";
    public final static String REG_SERVICE_INFOMATION = "We are unable to process your request. Please reply with HELP for more imformation how to use the service.";

    /**
     * HELP messages
     */
    public final static String HELP_REGISTERED_PART1 = "Dear ";
    public final static String HELP_REGISTERED_PART2 = ". You can send HELP for help message, PAUSE to pause the service, UNPAUSE to resume service, and STOP to opt out. All messages to be sent to code 60010";
    public final static String HELP_NON_REGISTERED = "Dear customer. Welcome to dating service. You need to register by sending Name Age Sex County to 60010 E.g. send John 30 M Nairobi to 60010.";
    public final static String HELP_GENERAL = "Welcome to dating service. Send Name Age Sex County to 60010 to register E.g. John 30 M Nairobi. PAUSE to pause, UNPAUSE to resume, and STOP to opt out.";

    /**
     * First part of PAUSE message for registered subscribers
     */
    public final static String PAUSE_REGISTERED_PART1 = "Dear ";
    public final static String PAUSE_REGISTERED_PART2 = ". You will not receive date matches and dating tips from this service. Send UNPAUSE to 60010 to resume the service.";
    public final static String PAUSE_NON_REGISTERED = "Dear customer. You are not registered for this service. You can register by sending Name Age Sex County to 60010 E.g. John 30 M Nairobi,";
    public final static String PAUSE_ALREADY_PAUSED = "Dear customer. Your account is not active at the moment. To resume service send UNPAUSE to 60010";
    public final static String PAUSE_TECHNICAL_ERROR = "Dear customer. We are unable to process your request at the moment. Please try again later. Thank you.";

    /**
     * First part of PAUSE message for registered subscribers
     */
    public final static String RESUME_REGISTERED_PART1 = "Dear ";
    public final static String RESUME_REGISTERED_PART2 = ". Your account is now active. You will start receiving date matches and dating tips from this service. Thank for choosing discovery dating service.";
    public final static String RESUME_NON_REGISTERED = "Dear customer. You are not registered for this service. You can register by sending Name Age Sex County to 60010 E.g. John 30 M Nairobi,";
    public final static String RESUME_TECHNICAL_ERROR = "Dear customer. We are unable to process your request at the moment. Please try again later. Thank you.";
    public final static String RESUME_ALREADY_RESUMED = "Dear customer. Your account is already active. Please call customer service if you need help. Thank for choosing discovery dating service.";

    /**
     * First part of PAUSE message for registered subscribers
     */
    public final static String STOP_REGISTERED_PART1 = "Dear ";
    public final static String STOP_REGISTERED_PART2 = ". Your profile has been deactivated. You can opt in again by sending REGISTER to 60010.";
    public final static String STOP_NON_REGISTERED = "Dear customer. You are not registered for this service. You can register by sending Name Age Sex County to 60010 E.g. John 30 M Nairobi,";
    public final static String STOP_TECHNICAL_ERROR = "Dear customer. We are unable to process your request at the moment. Please try again later. Thank you.";
    public final static String STOP_HOW_TO = "Dear customer. To opt out from this service. Please send the word STOP to 60010. Thank you.";

    /**
     * Subscription requests processing messages
     */
    public final static String SUBSCRIPTION_ADD_SUCCESS_PART1 = "Dear ";
    public final static String SUBSCRIPTION_ADD_SUCCESS_PART2 = " . Thank for you for choosing dicovery dating service. Kindly update your personal details by replying with Name Age Sex County to 60010 e.g. John 30 M Nairobi";
    public final static String SUBSCRIPTION_ADD_TECHNICAL_ERROR = "Dear customer, we have received your request and your profile will be updated accordingly.";
    public final static String SUBSCRIPTION_UPDATE_SUCCESS_PART1 = "Dear ";
    public final static String SUBSCRIPTION_UPDATE_SUCCESS_PART2 = ". Congratulations! Your are now fully register for discovery dating service. Enjoy the discovery dating experience.";
    public final static String SUBSCRIPTION_UPDATE_TECHNICAL_ERROR = "Dear customer, we have received your request and your profile will be updated accordingly.";
    public final static String SUBSCRIPTION_UNSUPPORTED_REQUEST = "Dear customer, the operation you are requesting for is not support. Please call customer care for assistance.";

    /**
     * Matcher SMS
     */
    public final static String MATCHER_SDP_STATUS_INACTIVE_TEXT = "Dear customer. To starting enjoying discovery dtaing services, please send  REGISTER to 60010.";
    public final static String MATCHER_REGISTRATION_PENDING_TEXT = "Dear customer. To starting enjoying discovery dtaing services, we need to know what you are looking for. Reply with 1. for Partner 2. for Friend 3. for Sex. E.g. 2";

}
