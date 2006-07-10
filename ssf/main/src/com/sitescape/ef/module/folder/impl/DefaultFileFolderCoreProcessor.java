
package com.sitescape.ef.module.folder.impl;

import java.util.Iterator;
import java.util.Map;

import com.sitescape.ef.NotSupportedException;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.domain.LibraryEntryExistsException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.security.AccessControlException;

/**
 * Library folders have the restriction that the title of all toplevel entries must be unique
 * @author Janet McCann
 *
 */
public class DefaultFileFolderCoreProcessor extends DefaultFolderCoreProcessor {
	private String[] cfAttrs = new String[]{"parentBinder", "HKey.level", "lower(title)"};

	   protected void addEntry_fillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {  
    	//title must be unique
	   	String title = (String)entryData.get("title");
	   	if ((title == null) || title.equals("")) throw new IllegalArgumentException("title is required");
     	Object[] cfValues = new Object[]{binder, new Integer(1), title.toLowerCase()};
    	// see if title exists for this folder
     	Iterator result = getFolderDao().queryEntries(new FilterControls(cfAttrs, cfValues));
   		if (result.hasNext()) 
     		throw new LibraryEntryExistsException(title, "Title already exists");
      	super.addEntry_fillIn(binder, entry, inputData, entryData);
    }
   public Long addBinder(final Binder parent, Definition def, Class clazz, 
	   		final InputDataAccessor inputData, Map fileItems) 
	   	throws AccessControlException, WriteFilesException {
	       // This default implementation is coded after template pattern. 
	           
	    //make sure sub-folder is file folder
        if (def.getType() != Definition.FILE_FOLDER_VIEW) {
        	throw new NotSupportedException("Sub-folder must be a file folder type");
        }
        return super.addBinder(parent, def, clazz, inputData, fileItems);
        
    }
}
