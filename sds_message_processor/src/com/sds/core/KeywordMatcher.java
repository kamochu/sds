package com.sds.core;

import com.sds.core.conf.Keywords;
import com.sds.core.processors.Help;
import com.sds.core.processors.Pause;
import com.sds.core.processors.Register;
import com.sds.core.processors.Resume;
import com.sds.core.processors.Stop;

/**
 *
 * @author Samuel Kamochu
 */
public class KeywordMatcher {

    public static void process(InboxMessage message) {
        //trim then message
        message.setMessage(message.getMessage().trim());

        //match the keyword and resolve the processor, ignore case
        switch (message.getMessage().toUpperCase()) {
            case Keywords.STOP:
                new Stop().process(message);
                break;
            case Keywords.PAUSE:
                new Pause().process(message);
                break;
            case Keywords.RESUME:
                new Resume().process(message);
                break;
            case Keywords.HELP:
                new Help().process(message);
                break;
            default:
                new Register().process(message);
                break;
        }
    }

   

}
