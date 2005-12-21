
package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.folder.FolderCoreProcessor;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclControlled;

/**
 * @author Janet McCann
 *
 */
public class BinderModuleImpl extends CommonDependencyInjection implements BinderModule {
	    
	public Binder getBinderByName(String binderName) 
   			throws NoBinderByTheNameException, AccessControlException {
		Binder binder = getCoreDao().findBinderByName(binderName, RequestContextHolder.getRequestContext().getZoneName());
	    
		// Check if the user has "read" access to the binder.
        getAccessControlManager().checkAcl(binder, AccessType.READ);
 
		return binder;
	}
   
	public Binder getBinder(Long binderId)
			throws NoBinderByTheIdException, AccessControlException {
		Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());

		// Check if the user has "read" access to the binder.
        getAccessControlManager().checkAcl(binder, AccessType.READ);

        return binder;        
	}

    public Entry getBinderEntry(Long parentFolderId, Long entryId) throws AccessControlException {
        User user = RequestContextHolder.getRequestContext().getUser();
        Binder parentBinder = coreDao.loadBinder(parentFolderId, user.getZoneName());
        Entry entry = binderEntry_load(parentBinder, entryId);
        return entry;
    }
    protected Entry binderEntry_load(Binder parentBinder, Long entryId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Entry entry = null;
        if (parentBinder instanceof ProfileBinder) {
        	entry = (Entry) coreDao.loadPrincipal(entryId, user.getZoneName());
        } else if (parentBinder instanceof Folder) {
            FolderCoreProcessor processor = (FolderCoreProcessor) getProcessorManager().getProcessor
        	(parentBinder, FolderCoreProcessor.PROCESSOR_KEY);
            entry = processor.getEntry((Folder)parentBinder, entryId, FolderModule.CURRENT_ENTRY);
        }
        //check access
        getEntry_accessControl(parentBinder, entry);
        
        return entry;
    }    
    protected void getEntry_accessControl(Binder parentFolder, Entry entry) {
        
        // Check if the user has the privilege to view the entries in the 
        // work area, which is the docshare forum.
    	getAccessControlManager().checkAcl(parentFolder, AccessType.READ);
              
        // Check if the user has "read" access to the particular entry.
    	if (entry instanceof AclControlled) {
    		getAccessControlManager().checkAcl(parentFolder, (AclControlled) entry, AccessType.READ);
    	}
        
        // TODO If there is a workflow attached to the entry, we must perform
        // additional access check based on the state the entry is currently in.
    }
         

    public Binder modifyConfiguration(Long binderId, List definitionIds, Map workflowAssociations) 
	throws AccessControlException {
		Binder binder = modifyConfiguration(binderId, definitionIds);
		binder.setProperty(ObjectKeys.BINDER_WORKFLOW_ASSOCIATIONS, workflowAssociations);
		return binder;
	}
	public Binder modifyConfiguration(Long binderId, List definitionIds) throws AccessControlException {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
		List definitions = new ArrayList(); 
		Definition def;
		Binder binder = getCoreDao().loadBinder(binderId, companyId);
		getAccessControlManager().checkAcl(binder, AccessType.WRITE);    	
		//	Build up new set - domain object will handle associations
		if (definitionIds != null) {
			for (int i=0; i<definitionIds.size(); ++i) {
				def = getCoreDao().loadDefinition((String)definitionIds.get(i), companyId);
				//	TODO:	getAccessControlManager().checkAcl(def, AccessType.READ);
				definitions.add(def);
			}
		}
	
		binder.setDefinitions(definitions);
		return binder;
	}
}
