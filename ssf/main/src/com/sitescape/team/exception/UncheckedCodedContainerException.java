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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;

public class UncheckedCodedContainerException extends UncheckedCodedException 
	implements ContainerSupport {
	
	private List exceptions;
	private String lineDelimiter = "\n";
	
    public UncheckedCodedContainerException(String errorCode) {
    	super(errorCode);
    	exceptions = new ArrayList();
    }
    public UncheckedCodedContainerException(String errorCode, Object[] errorArgs) {
    	super(errorCode, errorArgs);
    	exceptions = new ArrayList();
    }
    public UncheckedCodedContainerException(String errorCode, Object[] errorArgs, String message) {
    	super(errorCode, errorArgs, message);
    	exceptions = new ArrayList();
    }
    public UncheckedCodedContainerException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
    	super(errorCode, errorArgs, message, cause);
    	exceptions = new ArrayList();
    }
    public UncheckedCodedContainerException(String errorCode, Object[] errorArgs, Throwable cause) {
    	super(errorCode, errorArgs, cause);
    	exceptions = new ArrayList();
    }

    public String getLocalizedMessage() {
    	StringBuffer sb = new StringBuffer();
    	
    	sb.append(super.getLocalizedMessage());
    	
    	for(int i = 0; i < exceptions.size(); i++) {
    		sb.append(lineDelimiter).
    		append(String.valueOf(i+1)).
    		append(". ").
    		append(((Exception) exceptions.get(i)).getLocalizedMessage());
    	}
    	
    	return sb.toString();
    }
	
	public void addException(Exception e) {
		exceptions.add(e);
	}
	
	public void addExceptions(Collection e) {
		exceptions.addAll(e);
	}
	public Iterator iterator() {
		return exceptions.iterator();
	}
	
	public int size() {
		return exceptions.size();
	}
}
