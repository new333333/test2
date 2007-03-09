package com.sitescape.team.module.binder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
/**
 * This processor is used to setup the attributes of the target binder.  It is mostly here so
 * that the binder module and controllers can use existing code .
 * @author Janet
 *
 */
public class DefaultTemplateCoreProcessor extends AbstractBinderProcessor 
	implements BinderProcessor {
	//In this case we are configuring the target binder properties which may be workspace or folder
	//Many attribute are setup already as part of the actual template creation
	public Binder addBinder(final Binder binder, Definition def, Class clazz, 
	    		final InputDataAccessor inputData, Map fileItems) 
	    	throws AccessControlException, WriteFilesException {
		throw new NotSupportedException("Add not supported on TemplateBinder");

	}
    
    protected void modifyBinder_indexAdd(Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems,
    		Collection<FileAttachment> filesToIndex) {
    }
    public void deleteBinder(Binder binder) {
    	//Delete the template.  The interalId isn't meant
    	// to force the template allways exist
    	binder.setInternalId(null); 
    	super.deleteBinder(binder);
    		
    }
  	//not supported
	public void moveBinder(Binder source, Binder destination) {
		throw new NotSupportedException("Move not supported on TemplateBinder");
	
	}
	//nothing to index
	public void indexBinder(Binder binder) {
	
	}
	public void indexBinder(Binder binder, boolean includeEntries) {
		
	}
	//nothing to index
	public Collection indexTree(Binder binder, Collection exclusions) {
		return new ArrayList();
	}
	//nothing to log
	public ChangeLog processChangeLog(Binder binder, String operation) { 
		return null;
	
	}
}
