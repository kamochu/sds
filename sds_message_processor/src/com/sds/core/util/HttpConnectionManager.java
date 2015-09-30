package com.sds.core.util;

import static com.google.common.net.HttpHeaders.USER_AGENT;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * @author Samuel Kamochu
 */
public class HttpConnectionManager {

    private final static Logger log = Logger.getLogger(HttpConnectionManager.class.getName());

    public final static int CONNECTION_TIMEOUT = 5000;
    public final static int READ_TIMEOUT = 5000;

    public static String sendGet(String url) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setConnectTimeout(CONNECTION_TIMEOUT);
        con.setReadTimeout(READ_TIMEOUT);

        int responseCode = con.getResponseCode();

        log.debug("Sending 'GET' request to URL : " + url);
        log.debug("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        log.info("response text:" + response.toString());

        return response.toString();

    }

}
