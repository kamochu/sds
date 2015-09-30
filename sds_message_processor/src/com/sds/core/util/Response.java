package com.sds.core.util;

import com.sds.core.SendMessage;

/**
 *
 * @author Samuel Kamochu
 */
public class Response {

    private int status;
    private String response;
    private SendMessage outMessage;

    public Response() {
        this(0, null);
    }

    public Response(int status, String response) {
        this.status = status;
        this.response = response;
    }

    public Response(int status, String response, SendMessage outMessage) {
        this.status = status;
        this.response = response;
        this.outMessage = outMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public SendMessage getOutMessage() {
        return outMessage;
    }

    public void setOutMessage(SendMessage outMessage) {
        this.outMessage = outMessage;
    }

    @Override
    public String toString() {
        return "Response{" + "status=" + status + ", response=" + response + ", outMessage=" + outMessage + '}';
    }

}
