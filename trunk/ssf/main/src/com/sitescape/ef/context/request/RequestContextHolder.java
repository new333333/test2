package com.sitescape.ef.context.request;

/**
 * @author Jong Kim
 *
 */
public class RequestContextHolder {
    private static final ThreadLocal requestContextTL = new ThreadLocal();

    public static void setRequestContext(RequestContext requestContext) {
        requestContextTL.set(requestContext);
    }
    public static RequestContext getRequestContext() {
        return (RequestContext) requestContextTL.get();
    }
    public static void clear() {
        setRequestContext(null);
    }
}
