package com.schedulify.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogUtils {
    public static final String TRACE_ID = "traceId";
    public static final String METHOD = "method";
    public static final String ARGS = "args";
    public static final String RESULT = "result";
    public static final String EXECUTION_TIME = "executionTime";
    public static final String EXCEPTION = "exception";

    public static void setTraceId() {
        MDC.put(TRACE_ID, generateTraceId());
    }

    public static void clearTraceId() {
        MDC.remove(TRACE_ID);
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }
}