package com.sitescape.ef.remoting.impl;

import com.sitescape.ef.remoting.api.Entry;
import com.sitescape.ef.remoting.api.Facade;

/**
 * POJO implementation of Facade interface.
 * 
 * @author jong
 *
 */
public class FacadeImpl implements Facade {

	public Entry getEntry(long binderId, long entryId) {
		// TODO fake for now
		Entry entry = new Entry();
		entry.setBinderId(new Long(binderId));
		entry.setId(new Long(entryId));
		entry.setTitle("Hey, what do you expect?");
		
		return entry;
	}

}
