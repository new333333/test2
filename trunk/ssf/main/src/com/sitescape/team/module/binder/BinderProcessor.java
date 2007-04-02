package com.sitescape.team.module.binder;

import java.util.Collection;
import java.util.Map;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;

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
  	//return search results
  	public Map getBinders(Binder binder, Map options);
  	public void modifyBinder(Binder binder, InputDataAccessor inputData, Map fileItems, Collection deleteAttachments) 
		throws AccessControlException, WriteFilesException;
    public void moveBinder(Binder source, Binder destination);
	public void indexBinder(Binder binder, boolean includeEntries);	
    public Collection indexTree(Binder binder, Collection exclusions);
	public ChangeLog processChangeLog(Binder binder, String operation);
}
