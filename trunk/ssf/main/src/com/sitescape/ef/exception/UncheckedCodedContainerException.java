package com.sitescape.ef.exception;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	
	public Iterator iterator() {
		return exceptions.iterator();
	}
	
	public int size() {
		return exceptions.size();
	}
}
