package com.sitescape.ef.exception;

import java.util.Iterator;

public interface ContainerSupport {

	public void addException(Exception e);
	
	public Iterator iterator();

	public int size();
}
