package com.sds.core.processors;

import com.sds.core.Subscriber;
import org.apache.log4j.Logger;
import com.sds.App;
import com.sds.core.DatingTip;
import com.sds.core.MessageTypes;
import com.sds.core.ScheduledMessage;
import com.sds.core.conf.RegistrationStatus;
import com.sds.core.conf.SDPStatus;
import com.sds.core.conf.Sex;
import com.sds.core.conf.TextConfigs;
import com.sds.core.exceptions.InvalidNodeException;
import com.sds.core.util.MessageUtils;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Samuel Kamochu
 */
public class Matcher {

    private final static Logger log = Logger.getLogger(Matcher.class.getName());
    private final static String smsSendStartTime = "0800";
    private final static String smsSendEndTime = "1800";
    private final static int MALE_AGE_DIFF = -10;
    private final static int FEMALE_AGE_DIFF = 10;

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

        ScheduledMessage message2 = new ScheduledMessage();

        Connection connection = null;
        Subscriber match = null;
        boolean found = false;
        boolean dataMatched = false;

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
            if (subscriber.getRegStatus() < RegistrationStatus.BASIC) {
                try {
                    message.setMessage(MessageUtils.resolveMessage(subscriber.getLastNode(), subscriber));
                } catch (InvalidNodeException ex) {
                    message.setMessage(MessageUtils.resolveMessage(TextConfigs.GEN_TECHNICAL_FAILURE_TEXT, subscriber));
                    log.error("error resolving message", ex);
                }
                message.setMessageType(MessageTypes.INFO_SMS);
                App.incrementInfoSMS();
                found = true;
            } else {
                try {
                    //proceed to checking date match
                    if (connection != null && connection.isValid(50) && !connection.isClosed()) {
                        DatingTip tip = null;

                        //try reloading the subscriber just in case there is a match that has happened immediately 
                        //get date match
                        try {
                            subscriber = DataManager.getSubscriber(connection, subscriber.getMsisdn());
                        } catch (SQLException ex) {
                            log.warn("error reloading the subscriber ", ex);
                        }

                        //get date match
                        try {

                            if (subscriber.getNextMatchDate().compareTo(new Date()) <= 0) {
                                match = DataManager.getDateMatch(connection,
                                        subscriber.getId(),
                                        compuetLowerAge(subscriber.getAge(), subscriber.getSex()),
                                        compuetUpperAge(subscriber.getAge(), subscriber.getSex()),
                                        getPreferedSex(subscriber.getSex()),
                                        subscriber.getLocation(),
                                        subscriber.getPreference()
                                );
                            } else {
                                log.warn("We are not allowed to do a match for this subecriber" + subscriber.getNextMatchDate());
                            }
                        } catch (SQLException ex) {
                            log.warn("error getting date match for " + subscriber.toString(), ex);
                        }

                        if (match != null) {

                            //set message for first meatch 
                            message.setMessage("Dear " + subscriber.getName() + ". "
                                    + "We have a new date for you! "
                                    + getPronoun(match.getSex()) + " is " + match.getName()
                                    + " aged " + match.getAge()
                                    + ". Mobile: " + getNormalizedMSISDN(match.getMsisdn()));
                            message.setMessageType(MessageTypes.DATE_MATCH);
                            message.setReferenceId(match.getId());
                            App.incrementDateMatches();

                            //set message for match 2
                            message2.setBatchId(App.getBatchId());
                            message2.setScheduleDate(App.mysqlDateFormat.format(new Date()));
                            message2.setSendStartTime(smsSendStartTime);
                            message2.setSendEndTime(smsSendEndTime);
                            message2.setSubscriber(match);
                            //set message for first meatch 
                            message2.setMessage("Dear " + match.getName() + ". "
                                    + "We have a new date for you! "
                                    + getPronoun(subscriber.getSex()) + " is " + subscriber.getName()
                                    + " aged " + subscriber.getAge()
                                    + ". Mobile: " + getNormalizedMSISDN(subscriber.getMsisdn()));
                            message2.setMessageType(MessageTypes.DATE_MATCH);
                            message2.setReferenceId(subscriber.getId());

                            //update next match dates
                            Calendar c = Calendar.getInstance();
                            c.setTime(new Date()); // Now use today date.
                            c.add(Calendar.DATE, App.MATCH_FREEZE_DAYS); // Adding 5 days
                            subscriber.setNextMatchDate(c.getTime());
                            match.setNextMatchDate(c.getTime());

                            //update found and matched flag
                            found = true;
                            dataMatched = true;
                        } else {
                            if (subscriber.getRegStatus() < RegistrationStatus.COMPLETE) {
                                //get the last node message id to remind customer to update profile
                                try {
                                    message.setMessage(MessageUtils.resolveMessage(subscriber.getLastNode(), subscriber));
                                } catch (InvalidNodeException ex) {
                                    message.setMessage(MessageUtils.resolveMessage(TextConfigs.GEN_TECHNICAL_FAILURE_TEXT, subscriber));
                                    log.error("error resolving message", ex);
                                }
                                message.setMessageType(MessageTypes.INFO_SMS);
                                App.incrementInfoSMS();
                                found = true;
                            } else {
                                //get the date tip
                                try {
                                    tip = DataManager.getDatingTip(connection, subscriber.getId());
                                } catch (SQLException ex) {
                                    log.warn("eror loading dating tip for " + subscriber.toString(), ex);
                                    App.incrementNoFailed();
                                }
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
                } catch (SQLException ex) {
                    log.error("Database connection error while processing subscriber: " + subscriber.toString(), ex);
                }
            }
        }

        //found tip or a date or info sms
        if (found) {

            if (dataMatched) {
                //schedule and update subscriber message
                try {
                    if (DataManager.addSheduledMessage(connection, message) == DataManager.EXECUTE_SUCCESS) {
                        if (DataManager.updateMatchDates(connection, subscriber) == DataManager.EXECUTE_FAIL) {
                            log.error("failed to updated the subscriber profile of the exact match date");
                        }
                    }
                } catch (SQLException ex) {
                    log.error("unable to schedule the message: " + message, ex);
                }

                //schedule and update match record
                try {
                    if (DataManager.addSheduledMessage(connection, message2) == DataManager.EXECUTE_SUCCESS) {
                        if (DataManager.updateMatchDates(connection, match) == DataManager.EXECUTE_FAIL) {
                            log.error("failed to updated the subscriber profile of the exact match date");
                        }
                    }
                } catch (SQLException ex) {
                    log.error("unable to schedule the message: " + message2, ex);
                }

            } else {
                //update one subscriber profile
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
