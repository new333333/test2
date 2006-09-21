package com.sitescape.ef.module.binder;

import java.util.Map;
import java.util.Collection;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface BinderProcessor {
    public static final String PROCESSOR_KEY = "processorKey_binderCoreProcessor";

    public Binder addBinder(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
  	public void deleteBinder(Binder binder) throws AccessControlException;
    public void modifyBinder(Binder binder, InputDataAccessor inputData, Map fileItems, Collection deleteAttachments) 
		throws AccessControlException, WriteFilesException;
    public void moveBinder(Binder source, Binder destination);
	public void indexBinder(Binder binder);	
}
