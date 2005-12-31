package com.sitescape.ef.remoting.impl;

import java.util.Iterator;
import java.util.Map;

import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.remoting.api.Entry;
import com.sitescape.ef.remoting.api.Facade;

/**
 * POJO implementation of Facade interface.
 * 
 * @author jong
 *
 */
public class FacadeImpl implements Facade {

	private FolderModule folderModule;

	protected FolderModule getFolderModule() {
		return folderModule;
	}
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	public Entry getEntry(long binderId, long entryId) {
		// TODO fake for now
		/*
		Entry entry = new Entry();
		entry.setBinderId(new Long(binderId));
		entry.setId(new Long(entryId));
		entry.setTitle("Hey, what do you expect?");
		*/
		
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		
		com.sitescape.ef.domain.FolderEntry domainEntry = 
			getFolderModule().getEntry(bId, eId);
		
		Entry entry = new Entry();
		entry.setBinderId(bId);
		entry.setId(eId);
		entry.setTitle(domainEntry.getTitle());
		
		// TODO The following code tests lazy loading - to be removed
		Map attrs = domainEntry.getCustomAttributes();
		for(Iterator i = attrs.entrySet().iterator(); i.hasNext();) {
			Map.Entry me = (Map.Entry) i.next();
		}
		
		return entry;
	}

}
