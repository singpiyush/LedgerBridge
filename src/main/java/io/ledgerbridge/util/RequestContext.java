package io.ledgerbridge.util;

import java.util.UUID;

public final class RequestContext {

    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ACCOUNT_ID = new ThreadLocal<>();

    private RequestContext() {}

    public static void setRequestId(String requestId) {
        REQUEST_ID.set(requestId);
    }

    public static String getRequestId() {
        String id = REQUEST_ID.get();
        return id != null ? id : "req_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static void setAccountId(String accountId) {
        ACCOUNT_ID.set(accountId);
    }

    public static String getAccountId() {
        return ACCOUNT_ID.get();
    }

    public static void clear() {
        REQUEST_ID.remove();
        ACCOUNT_ID.remove();
    }
}
