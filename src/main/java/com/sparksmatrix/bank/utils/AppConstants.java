package com.sparksmatrix.bank.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class AppConstants {
    public static ObjectMapper MAPPER =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Server response messages
     */
    public static class ServerResponses {
        public static final String INTERNAL_SERVER_ERROR = "A system error occurred while processing your request. "
            + "Kindly try again or contact the admin for assistance!";
        public static final String BAD_REQUEST = "Invalid request received. Check your input and try again.";
        public static final String SUCCESS = "Request processed successfully.";
    }
}
