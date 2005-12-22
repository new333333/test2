package com.sitescape.ef.remoting.api;


/**
 * WS facade for business tier.
 * 
 * @author jong
 *
 */
public interface Facade {
	public Entry getEntry(long binderId, long entryId);
}
