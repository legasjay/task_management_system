package com.olegandreevich.tms.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TestUtil {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static byte[] convertObjectToJsonBytes(Object object) throws Exception {
        return objectMapper.writeValueAsBytes(object);
    }
}
