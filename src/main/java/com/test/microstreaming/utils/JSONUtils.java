package com.test.microstreaming.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.util.ObjectUtils;

import java.io.IOException;

public class JSONUtils {

    protected static final ObjectMapper mapper;
    protected static final ObjectWriter writer;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);
        writer = mapper.writer();
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            if (ObjectUtils.isEmpty(json)) {
                return null;
            }
            return mapper.readValue(json, clazz);
        } catch (IOException ex) {
            throw new RuntimeException("JSON_ERROR", ex);
        }
    }

    public static <T> T fromJson(String json,  TypeReference<T> type) {
        try {
            if (ObjectUtils.isEmpty(json)) {
                return null;
            }
            return mapper.readValue(json, type);
        } catch (IOException ex) {
            throw new RuntimeException("JSON_ERROR", ex);
        }
    }

    public static String toJSON(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return writer.writeValueAsString(o);
        } catch (IOException ex) {
            throw new RuntimeException("Error writing JSON of Object: " + o, ex);
        }
    }
}
