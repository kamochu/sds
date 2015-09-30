package com.sds.core.processors;

import com.google.common.base.Splitter;
import com.sds.core.InboxMessage;
import com.sds.core.OperationTypes;
import com.sds.core.RegistrationStatus;
import com.sds.core.SDPStatus;
import com.sds.core.Sex;
import com.sds.core.Subscriber;
import com.sds.core.conf.TextConfigs;
import com.sds.core.exceptions.InvalidAgeException;
import com.sds.core.exceptions.InvalidNumberOfParametersException;
import com.sds.core.exceptions.InvalidSexException;
import com.sds.core.util.MessageUtils;
import com.sds.core.util.Response;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class Register implements Processor {
    
    private final static Logger log = Logger.getLogger(Register.class.getName());
    private final static String SPLIT_PATTERN = " ";
    private final static Splitter splitter = Splitter.on(SPLIT_PATTERN);
    
    public final static int UPDATE_OPTIONS[] = {1, 2, 3};
    
    public final static DBConnectionPool pool = DBConnectionPool.getInstance();
    
    @Override
    public void process(InboxMessage message) {
        
        String responseText;
        Subscriber subscriber = null;
        int operationType = OperationTypes.UPDATE_PERSONAL_DETAILS;
        //get connection 
        Connection connection = null;
        try {
            connection = pool.getConnection();
        } catch (SQLException ex) {
            log.error("error getting connection from pool: " + ex);
        }

        //use connection to process the registration request
        if (connection != null) {
            try {
                subscriber = DataManager.getSubscriber(connection, message.getSenderAddress());
                subscriber.setMsisdn(message.getSenderAddress());
                
                log.error("subscriber loaded: " + subscriber);

                //subscriber is loaded
                if (subscriber.isLoaded() && subscriber.getAge() != 0 && subscriber.getName() != null) {
                    if (subscriber.getRegStatus() == RegistrationStatus.REG_INITIAL) {
                        log.error("current registration status is initial");
                        //waiting for second registration
                        responseText = update(connection, subscriber, message);
                    } else {
                        if (subscriber.getSdpStatus() == SDPStatus.CONFIRMED) {
                            log.error("current registration status is confirmed");
                            //subscription request is complete, send information message
                            responseText = TextConfigs.REG_SERVICE_INFOMATION;
                        } else {
                            log.error("current registration status is confirmed");
                            //subscription request is complete, send information message
                            responseText = TextConfigs.REG_PREF_SUCCESS_SUBSCRIPTION_CONFIRM;
                        }
                    }
                } else {
                    //customer does not exist on SDS, proceed to registration flow
                    responseText = register(connection, subscriber, message);
                }
            } catch (SQLException ex) {
                log.error("error loading suscriber: ", ex);
                responseText = TextConfigs.REG_GEN_TECHNICAL_FAILURE_TEXT;
            }
        } else {
            responseText = TextConfigs.REG_GEN_TECHNICAL_FAILURE_TEXT;
        }
        
        Response response = MessageUtils.sendMessage(responseText, message);
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        MessageUtils.addActivityLog(connection, operationType, 0, subscriber, message, response);
        
        DBConnectionPool.closeConnection(connection);
    }

    /**
     * validates and registers the subscriber, initial step
     *
     * @param connection database connection to be used in registration is
     * validation is successful
     * @param subscriber subscriber instance
     * @param message the incoming message
     * @return message text to be sent to the subscriber
     */
    private static String register(Connection connection, Subscriber subscriber, InboxMessage message) {
        String responseText;
        Iterable<String> iterable = split(message.getMessage());
        try {
            if (!subscriber.isLoaded()) {
                subscriber = createSubscriber(message, iterable, subscriber);
                if (DataManager.addSubscriber(connection, subscriber) == DataManager.EXECUTE_SUCCESS) {
                    responseText = TextConfigs.REG_INIT_SUCCESS_MESSAGE_TEXT_PART1
                            + subscriber.getName()
                            + TextConfigs.REG_INIT_SUCCESS_MESSAGE_TEXT_PART1;
                } else {
                    responseText = TextConfigs.REG_GEN_TECHNICAL_FAILURE_TEXT;
                }
            } else {
                subscriber = createSubscriber(message, iterable, subscriber);
                if (DataManager.updatePersonalParameters(connection, subscriber) == DataManager.EXECUTE_SUCCESS) {
                    responseText = TextConfigs.REG_INIT_SUCCESS_MESSAGE_TEXT_PART1
                            + subscriber.getName() + TextConfigs.REG_INIT_SUCCESS_MESSAGE_TEXT_PART2;
                } else {
                    responseText = TextConfigs.REG_GEN_TECHNICAL_FAILURE_TEXT;
                }
            }
            
        } catch (InvalidNumberOfParametersException ex) {
            log.error(ex);
            responseText = TextConfigs.REG_INIT_INVALID_ARGUMENTS_MESSAGE_TEXT;
        } catch (InvalidAgeException ex) {
            log.error(ex);
            responseText = TextConfigs.REG_INIT_INVALID_AGE_MESSAGE_TEXT;
        } catch (InvalidSexException ex) {
            log.error(ex);
            responseText = TextConfigs.REG_INIT_INVALID_SEX_MESSAGE_TEXT;
        } catch (SQLException ex) {
            log.error(ex);
            responseText = TextConfigs.REG_GEN_TECHNICAL_FAILURE_TEXT;
        }
        return responseText;
    }

    /**
     * process second stage in registration, validates and updates user dating
     * preference
     *
     * @param connection connection to be used in updating after validation
     * @param subscriber subscriber instance
     * @param message incoming message instance
     * @return message text to be sent to subscriber
     */
    private String update(Connection connection, Subscriber subscriber, InboxMessage message) {
        log.info("updating the customer profile with user preference");
        String responseText;
        int option;
        try {
            option = Integer.parseInt(message.getMessage());
            log.info("comparing: options " + UPDATE_OPTIONS + " and  input option " + option);
            for (int currentOption : UPDATE_OPTIONS) {
                if (currentOption == option) {
                    
                    if (subscriber.getSdpStatus() == SDPStatus.CONFIRMED) {
                        //update database record, with current
                        if (DataManager.updateSubscriberPreference(connection, RegistrationStatus.REG_CONFIRMED, option, subscriber) != DataManager.EXECUTE_SUCCESS) {
                            //inform customer to retry again, record could not be updated
                            return TextConfigs.REG_UPDATE_TECHNICAL_ERROR_TEXT;
                        } else {
                            //send subscriber customer request to SDP - TO BE IMPLEMENTED
                            return TextConfigs.REG_UPDATE_SUCCESSFUL_TEXT;
                        }
                    } else {
                        //update database record, with current
                        if (DataManager.updateSubscriberPreference(connection, RegistrationStatus.REG_CONFIRMED, option, subscriber) != DataManager.EXECUTE_SUCCESS) {
                            //inform customer to retry again, record could not be updated
                            return TextConfigs.REG_UPDATE_TECHNICAL_ERROR_TEXT;
                        } else {
                            //send subscriber customer request to SDP - TO BE IMPLEMENTED
                            return TextConfigs.REG_UPDATE_SUCCESSFUL_NOT_CONFIRMED_TEXT;
                        }
                    }
                }
            }
            log.error("invalid input, expected " + UPDATE_OPTIONS.toString() + ", got " + option);
            responseText = TextConfigs.REG_UPDATE_INVALID_INPUT_TEXT;
        } catch (NumberFormatException ex) {
            log.error("invalid input, expected integer, got " + message.getMessage(), ex);
            responseText = TextConfigs.REG_UPDATE_INVALID_INPUT_TEXT;
        }
        return responseText;
    }

    /**
     * creates a subscriber instance message
     *
     * @param message incoming message
     * @param iterable collection with user data parts after splitting user
     * message
     * @return returns a new subscriber instance
     * @throws InvalidNumberOfParametersException
     * @throws InvalidAgeException
     * @throws InvalidSexException
     */
    private static Subscriber createSubscriber(InboxMessage message, Iterable<String> iterable, Subscriber sub)
            throws InvalidNumberOfParametersException, InvalidAgeException, InvalidSexException {
        
        String name;
        int age;
        String sex;
        String location = "";
        
        Iterator<String> iterator = iterable.iterator();

        //validate number of arguments
        int size;
        ArrayList<String> parts = new ArrayList<>();
        while (iterator.hasNext()) {
            parts.add(iterator.next());
        }
        size = parts.size();
        if (size < 4) {
            throw new InvalidNumberOfParametersException("invalid number of arguments; expected 4, got " + size + "(" + parts + ")");
        }

        //get the name
        name = parts.get(0);

        //get age by convertning to integer
        try {
            age = Integer.parseInt(parts.get(1)); // first part
        } catch (NumberFormatException ex) {
            throw new InvalidAgeException(ex);
        }

        //get sex by validating number of characters
        if (parts.get(2) == null || parts.get(2).length() > 1 || !(parts.get(2).equalsIgnoreCase(Sex.MALE) || parts.get(2).equalsIgnoreCase(Sex.FEMALE))) {
            throw new InvalidSexException("invalid sex, expected 'F' or 'M', got " + parts.get(2));
        }
        sex = parts.get(2); // first character

        //get the location, all other parts remaining
        for (int i = 3; i < size; i++) {
            location = location + "" + parts.get(i);
        }
        
        sub.setMsisdn(message.getSenderAddress());
        sub.setName(name);
        sub.setAge(age);
        sub.setSex(sex);
        sub.setLocation(location);
        return sub;
    }

    /**
     * splits the user message into various parts that can be used in
     * registration
     *
     * @param message message to be split
     * @return iterable collection that has the parts after splitting
     */
    private static Iterable<String> split(String message) {
        return splitter.trimResults().omitEmptyStrings().split(message);
    }
    
}
