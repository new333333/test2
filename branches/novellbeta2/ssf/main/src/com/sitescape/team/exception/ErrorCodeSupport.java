/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.exception;

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
