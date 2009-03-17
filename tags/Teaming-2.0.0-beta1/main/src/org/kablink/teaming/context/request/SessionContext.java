package org.kablink.teaming.context.request;


public interface SessionContext {
	public Object getProperty(Object key);
	public void setProperty(Object key, Object value);
}