package com.sitescape.ef.remoting.api;


/**
 * WS facade for business tier.
 * 
 * @author jong
 *
 */
public interface Facade {
	public Entry getEntry(long binderId, long entryId);
	
	public String getEntryAsXML(long binderId, long entryId);
}
