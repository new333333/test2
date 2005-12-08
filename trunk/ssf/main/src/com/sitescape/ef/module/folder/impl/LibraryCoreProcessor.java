
package com.sitescape.ef.module.folder.impl;

import java.util.Iterator;
import java.util.Map;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.LibraryEntryExistsException;

/**
 * Library folders have the restriction that the title of all toplevel entries must be unique
 * @author Janet McCann
 *
 */
public class LibraryCoreProcessor extends DefaultFolderCoreProcessor {
	private String[] cfAttrs = new String[]{"parentFolder", "HKey.level", "lower(title)"};

	   protected void addEntry_fillIn(Folder folder, FolderEntry entry, Map inputData, Map entryData) {  
    	//title must be unique
	   	String title = (String)entryData.get("title");
	   	if ((title == null) || title.equals("")) throw new IllegalArgumentException("title is required");
     	Object[] cfValues = new Object[]{folder, new Integer(1), title.toLowerCase()};
    	// see if title exists for this folder
     	Iterator result = getFolderDao().queryEntries(new FilterControls(cfAttrs, cfValues));
   		if (result.hasNext()) 
     		throw new LibraryEntryExistsException(title, "Title already exists");
      	super.addEntry_fillIn(folder, entry, inputData, entryData);
    }

}
