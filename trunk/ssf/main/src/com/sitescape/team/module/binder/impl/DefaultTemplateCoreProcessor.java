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
     protected Object deleteBinder_preDelete(Binder binder) { 
    	// changes are logged
    	return null;
    }
    protected Object deleteBinder_indexDel(Binder binder, Object ctx) {
    	// not indexed, nothing to do
    	return ctx;
    }
    protected void modifyBinder_postFillIn(Binder binder, InputDataAccessor inputData, Map entryData) {
     }
    
    protected void modifyBinder_indexRemoveFiles(Binder binder, Collection<FileAttachment> filesToDeindex) {
    	//not indexed
    }
    protected void modifyBinder_removeAttachments(Binder binder, Collection deleteAttachments,
    		List<FileAttachment> filesToDeindex, List<FileAttachment> filesToReindex) {
    }
    protected void modifyBinder_indexAdd(Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems,
    		Collection<FileAttachment> filesToIndex) {
    }
 	//not supported
	public void moveBinder(Binder source, Binder destination) {
		throw new NotSupportedException("Move not supported on TemplateBinder");
	
	}
	//nothing to index
	public void indexBinder(Binder binder) {
	
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
