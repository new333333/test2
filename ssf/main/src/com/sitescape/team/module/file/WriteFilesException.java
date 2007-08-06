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
package com.sitescape.team.module.file;

import java.io.PrintStream;
import java.io.PrintWriter;

public class WriteFilesException extends Exception {
	
	private FilesErrors errors;
	private Long entityId;
	
	public WriteFilesException(FilesErrors errors, Long entityId) {
		this.errors = errors;
		this.entityId = entityId;
	}
	public WriteFilesException(FilesErrors errors) {
		this.errors = errors;
	}
	
	public FilesErrors getErrors() {
		return errors;
	}
	
	public String getMessage() {
		return getErrors().toString();
	}
	
    //overload to remove stack trace filling log files
    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
    public void printStackTrace(PrintStream s) {
    	
    }
    //overload to remove stack trace filling log files
    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
    public void printStackTrace(PrintWriter s) {
    	
    }
    public Long getEntityId() {
    	return entityId;
    }
}
