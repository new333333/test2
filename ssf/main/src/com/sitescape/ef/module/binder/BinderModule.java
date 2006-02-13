
package com.sitescape.ef.module.binder;

import java.util.List;
import java.util.Map;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.security.AccessControlException;

/**
 * @author Janet McCann
 *
 */
public interface BinderModule {
	/**
     * Find a binder of given name. 
     * 
     * @param binderName
     * @return
     * @throws NoBinderByTheNameException
     * @throws AccessControlException
     */
    public Binder getBinderByName(String binderName) 
		throws NoBinderByTheNameException, AccessControlException;
    /**
     * 
     * @param binderId
     * @return
     * @throws NoBinderByTheIdException
     * @throws AccessControlException
     */
    public Binder getBinder(Long binderId)
		throws NoBinderByTheIdException, AccessControlException;
    /**
     * 
     * @param binderId
     * @return
     * @throws NoBinderByTheIdException
     * @throws AccessControlException
     */	
    public Map getBinderFunctionMembership(Long binderId) 
		throws NoBinderByTheIdException, AccessControlException;

    /**
     * Modify the list of definitions assocated with a binder
     * @param binderId
     * @param definitionIds
     * @throws AccessControlException
     */
    public Binder modifyConfiguration(Long binderId, List definitionIds) throws AccessControlException;
    /**
      * Modify the list of definitions and workflows assocated with a binder
     * @param binderId
     * @param definitionIds
     * @param workflowAssociations
     * @throws AccessControlException
     */
    public Binder modifyConfiguration(Long binderId, List definitionIds, Map workflowAssociations) throws AccessControlException;
    
 
 }
