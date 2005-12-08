package com.sitescape.ef.exception;

/**
 * @author Jong Kim
 *
 */
public interface ErrorCodeSupport {
    /**
     * Returns an error code (as string) which can be used to lookup 
     * a message in the message facility for display to users. 
     * 
     * @return
     */
    public String getErrorCode();
    
    /**
     * Returns an array of arguments that will be filled in for params
     * within the identified message, or <code>null</code> if none. 
     * 
     * @return
     */
    public Object[] getErrorArgs();
    
    /**
     * sets an array of arguments. 
     * 
     * @param errorArgs
     */
    public void setErrorArgs(Object[] errorArgs);
}
