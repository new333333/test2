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

package com.sitescape.team.domain;

import java.io.PrintStream;
import java.io.PrintWriter;

import com.sitescape.team.exception.UncheckedCodedException;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class TitleException extends UncheckedCodedException {
	private static final String TitleExistsException_ErrorCode = "errorcode.title.exists";
    public TitleException(String title) {
    	super(TitleExistsException_ErrorCode, new Object[]{title});
    	if (Validator.isNull(title)) setErrorCode("errorcode.title.missing");
    }
    public TitleException(String title, String message) {
        super(TitleExistsException_ErrorCode,  new Object[]{title}, message);
    	if (Validator.isNull(title)) setErrorCode("errorcode.title.missing");
    }
    public TitleException(String title, String message, Throwable cause) {
        super(TitleExistsException_ErrorCode, new Object[]{title}, message, cause);
    	if (Validator.isNull(title)) setErrorCode("errorcode.title.missing");
    }
    public TitleException(String title, Throwable cause) {
        super(TitleExistsException_ErrorCode,  new Object[]{title}, cause);
    	if (Validator.isNull(title)) setErrorCode("errorcode.title.missing");
    }
    //overload to remove stack trace filling log files
    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
    public void printStackTrace(PrintStream s) {
    	
    }
    //overload to remove stack trace filling log files
    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
    public void printStackTrace(PrintWriter s) {
    	
    }    
}
