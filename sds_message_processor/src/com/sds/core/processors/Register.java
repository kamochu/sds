package com.sds.core.processors;

import com.sds.App;
import com.sds.core.InboxMessage;
import com.sds.core.Node;
import com.sds.core.conf.OperationTypes;
import com.sds.core.conf.RegistrationStatus;
import com.sds.core.Subscriber;
import com.sds.core.conf.Notifications;
import com.sds.core.conf.TextConfigs;
import com.sds.core.exceptions.InvalidNodeException;
import com.sds.core.util.MessageUtils;
import com.sds.core.util.Response;
import com.sds.dao.DBConnectionPool;
import com.sds.dao.DataManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class Register implements Processor {

    private final static Logger log = Logger.getLogger(Register.class.getName());
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
                String userInput;
                String pattern;
                int newRegStatus;
                int newLastNode;

                log.info("subscriber loaded: " + subscriber);

                //subscriber is loaded
                if (subscriber.isLoaded()) {
                    if (subscriber.getRegStatus() == RegistrationStatus.COMPLETE) {
                        //tell the customer that registration is complete
                        responseText = "Dear " + subscriber.getName() + ". You are registered for dating service. You can send HELP to 60010 for options.";
                    } else {
                        Node currentNode = App.getNodesMap().get(subscriber.getLastNode());

                        log.info("loaded node: " + currentNode);

                        if (currentNode != null) {
                            userInput = message.getMessage().trim();
                            pattern = currentNode.getValidationRule();
                            if (pattern == null || "".equals(pattern) || isValid(userInput, pattern)) {
                                newRegStatus = currentNode.getNewRegStatus(); //update reg status
                                newLastNode = currentNode.getNextNode();
                                //get the new node
                                Node newNode = App.getNodesMap().get(newLastNode);
                                if (newNode != null) {
                                    //if current node is a pause node, final node or feild name is not defined, then do not update parameter and do not validate input
                                    if (currentNode.isPauseNode() || currentNode.isFinalNode()
                                            || currentNode.getDbFiledName() == null || "".equals(currentNode.getDbFiledName())) {
                                        
                                        //responseText = MessageUtils.resolveMessage(newLastNode, subscriber);
                                        if (DataManager.updateSubscriberNullParamater(connection,
                                                newLastNode, newRegStatus, subscriber) == DataManager.EXECUTE_SUCCESS) {
                                            log.info("pause or final node or no field name specified");
                                            try {
                                                //we do need to process he request, proceed to next node
                                                responseText = MessageUtils.resolveMessage(newLastNode, subscriber);
                                            } catch (InvalidNodeException ex) {
                                                log.error("error getting response text", ex);
                                                try {
                                                    responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                                                } catch (InvalidNodeException ex1) {
                                                    log.error("unable to load error message", ex1);
                                                    responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                                }
                                            }
                                        } else {
                                            try {
                                                responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                                            } catch (InvalidNodeException ex1) {
                                                log.error("unable to load error message", ex1);
                                                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                            }
                                        }
                                    } else {
                                        //validate user data and update db field where necessary 
                                        if (isValid(userInput, pattern)) {
                                            if (currentNode.isFieldIntegerValue()) {
                                                //update integer value after converting user input into number
                                                try {
                                                    int userValue = Integer.parseInt(userInput);
                                                    if (DataManager.updateSubscriberParamater(connection, currentNode.getDbFiledName(), userValue,
                                                            newLastNode, newRegStatus, subscriber) == DataManager.EXECUTE_SUCCESS) {
                                                        try {
                                                            //we do need to process he request, proceed to next node
                                                            responseText = MessageUtils.resolveMessage(newLastNode, subscriber);
                                                        } catch (InvalidNodeException ex) {
                                                            log.error("error getting response text", ex);
                                                            try {
                                                                responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                                                            } catch (InvalidNodeException ex1) {
                                                                log.error("unable to load error message", ex1);
                                                                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                                            }
                                                        }
                                                    } else {
                                                        try {
                                                            responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                                                        } catch (InvalidNodeException ex1) {
                                                            log.error("unable to load error message", ex1);
                                                            responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                                        }
                                                    }

                                                } catch (NumberFormatException ex) {
                                                    responseText = currentNode.getValidationFailureMessage();
                                                    log.warn("the user input is not an integer - expected an integer for the node", ex);
                                                }

                                            } else {
                                                //update string value
                                                if (DataManager.updateSubscriberParamater(connection, currentNode.getDbFiledName(), userInput,
                                                        newLastNode, newRegStatus, subscriber) == DataManager.EXECUTE_SUCCESS) {
                                                    try {
                                                        //we do need to process he request, proceed to next node
                                                        responseText = MessageUtils.resolveMessage(newLastNode, subscriber);
                                                    } catch (InvalidNodeException ex) {
                                                        log.error("error getting response text", ex);
                                                        try {
                                                            responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                                                        } catch (InvalidNodeException ex1) {
                                                            log.error("unable to load error message", ex1);
                                                            responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                                        }
                                                    }
                                                } else {
                                                    try {
                                                        responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                                                    } catch (InvalidNodeException ex1) {
                                                        log.error("unable to load error message", ex1);
                                                        responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                                    }
                                                }

                                            }

                                        } else {
                                            //do not update the user profile and respond with the validation error message
                                            responseText = currentNode.getValidationFailureMessage();
                                        }
                                    }
                                } else {
                                    log.error("new node does not exist in the system. check configs. node id: " + newLastNode);
                                    try {
                                        responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                                    } catch (InvalidNodeException ex1) {
                                        log.error("unable to load error message", ex1);
                                        responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                                    }
                                }
                            } else {
                                responseText = MessageUtils.resolveMessage(currentNode.getValidationFailureMessage(), subscriber);
                            }
                        } else {
                            log.error("current node does not exist in the system. check configs. node id: " + subscriber.getLastNode());
                            try {
                                responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                            } catch (InvalidNodeException ex1) {
                                log.error("unable to load error message", ex1);
                                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                            }
                        }
                    }
                } else {
                    //subscriber not registered - pass general information
                    try {
                        responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_INFORMATION, subscriber);
                    } catch (InvalidNodeException ex1) {
                        log.error("unable to load error message", ex1);
                        responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                    }
                }
            } catch (SQLException ex) {
                log.error("error loading suscriber: ", ex);
                try {
                    responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
                } catch (InvalidNodeException ex1) {
                    log.error("unable to load error message", ex1);
                    responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
                }
            }
        } else {
            try {
                responseText = MessageUtils.resolveMessage(Notifications.REG_GEN_TECHNICAL_FAILURE, subscriber);
            } catch (InvalidNodeException ex1) {
                log.error("unable to load error message", ex1);
                responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;
            }
        }

        Response response = MessageUtils.sendMessage(responseText, message);
        if (subscriber == null) {
            subscriber = new Subscriber();
        }
        MessageUtils.addActivityLog(connection, operationType, 0, subscriber, message, response);

        DBConnectionPool.closeConnection(connection);
    }

    /**
     * matches a string with a regular expression
     *
     * @param message message to be matched
     * @param pattern matching pattern
     * @return true if the message matches the pattern, otherwise returns false
     */
    private static boolean isValid(String message, String pattern) {

        if (pattern == null || "".equals(pattern)) {
            return true;
        }
        return Pattern.compile(pattern).matcher(message).matches();
    }

}
