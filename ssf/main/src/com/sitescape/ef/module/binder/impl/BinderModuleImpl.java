
package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.acl.AccessType;

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
