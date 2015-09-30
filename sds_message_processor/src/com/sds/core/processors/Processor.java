package com.sds.core.processors;

import com.sds.core.InboxMessage;

/**
 *
 * @author Samuel Kamochu
 */
public interface Processor {
    
    public void process(InboxMessage message);

}
