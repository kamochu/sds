package com.sds.core.processors;

import com.sds.core.InboxMessage;
import com.sds.core.conf.TextConfigs;
import com.sds.core.util.MessageUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class Stop implements Processor {

    private final static Logger log = Logger.getLogger(Stop.class.getName());

    @Override
    public void process(InboxMessage message) {
        log.info("STOP: " + message);
        String responseText = TextConfigs.GEN_TECHNICAL_FAILURE_TEXT;

        //send the message
        MessageUtils.sendMessage(responseText, message);
    }

}
