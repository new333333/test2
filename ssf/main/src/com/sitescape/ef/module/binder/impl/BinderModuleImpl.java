
package com.sitescape.ef.module.binder.impl;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.impl.AbstractModuleImpl;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.function.WorkAreaOperation;

/**
 * @author Janet McCann
 *
 */
public class BinderModuleImpl extends AbstractModuleImpl implements BinderModule {
	    
	public Binder findBinder(String binderName) 
   			throws NoBinderByTheNameException, AccessControlException {
		Binder binder = getCoreDao().findBinderByName(binderName, RequestContextHolder.getRequestContext().getZoneName());
		accessControlManager.checkOperation(binder, WorkAreaOperation.VIEW);
		return binder;
	}
   
	public Binder loadBinder(Long binderId)
			throws NoBinderByTheIdException, AccessControlException {
		Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
		accessControlManager.checkOperation(binder, WorkAreaOperation.VIEW);
		return binder;        
	}
}
