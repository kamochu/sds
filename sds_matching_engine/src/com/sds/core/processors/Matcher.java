package com.sds.core.processors;

import com.sds.core.Subscriber;
import org.apache.log4j.Logger;
import com.sds.App;
import com.sds.core.DatingTip;
import com.sds.core.MessageTypes;
import com.sds.core.RegistrationStatus;
import com.sds.core.SDPStatus;
import com.sds.core.ScheduledMessage;
import com.sds.core.Sex;
import com.sds.core.conf.TextConfigs;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author Samuel Kamochu
 */
public class Matcher {
    
    private final static Logger log = Logger.getLogger(Matcher.class.getName());
    private final static String smsSendStartTime = "0800";
    private final static String smsSendEndTime = "1800";
    private final static int MALE_AGE_DIFF = -5;
    private final static int FEMALE_AGE_DIFF = 5;
    
    public final static DBConnectionPool pool = DBConnectionPool.getInstance();
    
    public void process(Subscriber subscriber) {
        log.info("RUNNING matcher for: " + subscriber); 

        //init the send message and set default parameters
        ScheduledMessage message = new ScheduledMessage();
        message.setBatchId(App.getBatchId());
        message.setScheduleDate(App.mysqlDateFormat.format(new Date()));
        message.setSendStartTime(smsSendStartTime);
        message.setSendEndTime(smsSendEndTime);
        message.setSubscriber(subscriber);
        
        Connection connection = null;
        boolean found = false;

        //get connection 
        try {
            connection = pool.getConnection();
            
        } catch (SQLException ex) {
            log.error("error getting connection from pool: " + ex);
            App.incrementNoFailed();
        }
        
        if (subscriber.getSdpStatus() != SDPStatus.CONFIRMED) {
            message.setMessage(TextConfigs.MATCHER_SDP_STATUS_INACTIVE_TEXT);
            message.setMessageType(MessageTypes.INFO_SMS);
            App.incrementInfoSMS();
            found = true;
        } else {
            if (subscriber.getRegStatus() != RegistrationStatus.REG_CONFIRMED) {
                message.setMessage(TextConfigs.MATCHER_REGISTRATION_PENDING_TEXT);
                message.setMessageType(MessageTypes.INFO_SMS);
                App.incrementInfoSMS();
                found = true;
            } else {
                //proceed to checking date match

                if (connection != null) {
                    
                    Subscriber match = null;
                    DatingTip tip = null;

                    //get date match
                    try {
                        
                        match = DataManager.getDateMatch(connection,
                                subscriber.getId(),
                                compuetLowerAge(subscriber.getAge(), subscriber.getSex()),
                                compuetUpperAge(subscriber.getAge(), subscriber.getSex()),
                                getPreferedSex(subscriber.getSex()),
                                subscriber.getLocation(),
                                subscriber.getPreference()
                        );
                        
                    } catch (SQLException ex) {
                        log.warn("error getting date match for " + subscriber.toString(), ex);
                    }
                    
                    if (match != null) {
                        message.setMessage("Dear " + subscriber.getName() + ". "
                                + "We have a new date for you!"
                                + getPronoun(match.getSex()) + " is " + match.getName()
                                + " aged " + match.getAge() + " from " + match.getLocation()
                                + ". Mobile: " + getNormalizedMSISDN(match.getMsisdn()));
                        message.setMessageType(MessageTypes.DATE_MATCH);
                        message.setReferenceId(match.getId());
                        App.incrementDateMatches();
                        found = true;
                    } else {
                        try {
                            //get the date tip
                            tip = DataManager.getDatingTip(connection, subscriber.getId());
                        } catch (SQLException ex) {
                            log.warn("eror loading dating tip for " + subscriber.toString(), ex);
                            App.incrementNoFailed();
                        }
                    }
                    
                    if (tip != null) {
                        message.setMessage(tip.getTip());
                        message.setMessageType(MessageTypes.DATING_TIP);
                        message.setReferenceId(tip.getId());
                        App.incrementDatingTips();
                        found = true;
                    } else {
                        App.incrementNoNothing();
                    }
                }
            }
            
        }

        //found tip or a date or info sms
        if (found) {
            try {
                if (DataManager.addSheduledMessage(connection, message) == DataManager.EXECUTE_SUCCESS) {
                    if (DataManager.updateLastMatchDate(connection, subscriber) == DataManager.EXECUTE_FAIL) {
                        log.error("failed to updated the subscriber profile of the exact match date");
                    }
                }
            } catch (SQLException ex) {
                log.error("unable to schedule the message: " + message, ex);
            }
        }

        //close connection
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            log.warn("error closing connection", ex);
        }
    }
    
    public static int compuetLowerAge(int age, String sex) {
        int newAge;
        if (Sex.MALE.equalsIgnoreCase(sex)) {
            newAge = age + MALE_AGE_DIFF;
        } else {
            newAge = age + FEMALE_AGE_DIFF;
        }
        return (newAge >= age) ? age : newAge;
    }
    
    public static int compuetUpperAge(int age, String sex) {
        int newAge;
        if (Sex.MALE.equalsIgnoreCase(sex)) {
            newAge = age + MALE_AGE_DIFF;
        } else {
            newAge = age + FEMALE_AGE_DIFF;
        }
        return (newAge >= age) ? newAge : age;
    }
    
    public static String getPreferedSex(String sex) {
        if (Sex.MALE.equalsIgnoreCase(sex)) {
            return Sex.FEMALE;
        }
        
        return Sex.MALE;
    }
    
    public static String getPronoun(String sex) {
        if (Sex.MALE.equalsIgnoreCase(sex)) {
            return "He";
        }
        return "She";
    }
    
    public static String getNormalizedMSISDN(String msisdn) {
        
        return "0" + msisdn.substring(4);
        
    }
    
}